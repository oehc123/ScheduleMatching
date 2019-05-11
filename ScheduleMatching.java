import java.io.*;
import java.util.*; 
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class ScheduleMatching {

	public static int NUMBEROFTYPES = 2;			//numbers of shifts types per day (early shift and late shift) or (shit A and Shift B)
	public static int VOLUNTEERS_PER_SHIFT = 4;
	public static int NAMECOL, PHONECOL, EMAILCOL;
	public static ArrayList<Integer> AVAILABILITYCOL = new ArrayList<Integer>();	// the indexes of columns that start with Availability
	public static ArrayList<String> AVAILABILITYTITLES = new ArrayList<String>();  //tracks the different availabilities in this table

    public static void main(String[] args) {    	 
        //populate volunteers
        ArrayList<Volunteer> volunteers = loadVolunteersFromCSVFile();
        System.out.println("Volunteers loaded:");
        for (Volunteer i : volunteers) {
    		System.out.println(i);
    	}
        
    	//2) traverse all the shifts of all volunteers to create our total matrix of shifts
    	ArrayList<ArrayList<Volunteer>> allAvailabilities = createMasterSchedule(volunteers);

    	//3) assign who are the selected volunteers for each shift
    	//based on previous shifts assigned
    	// and based on less shit available per volunteer
    	ArrayList<ArrayList<Volunteer>> finalVolunteerList = generateFinalVolunteerSchedule(allAvailabilities);

        //4) print final volunteer list:
        System.out.println("The final Schedule is: ");
        int j = 0;
        for( ArrayList<Volunteer> i : finalVolunteerList) {
            for( Volunteer v : i) {
                System.out.print(integerToShift(j) + " assingt to: ");
                System.out.println(v.name + j);
            }
            j++;
        }


    	System.out.println("***********************");
    	System.out.println("The final Volunteer list with their assinged shifts is: ");
    	for(Volunteer i : volunteers) {
    		System.out.print(i.name + " shiftAssigned: ");
    		for (int k : i.shiftAssigned) {
    			System.out.print(", " + integerToShift(k).replaceFirst("Availability", ""));
    		}
    		System.out.println(""); //printing new line
    	}
    	createOutputFile(finalVolunteerList, volunteers);		//to show the final table of schedules and volunteers with assinged shifts
    }

    private static void createOutputFile(ArrayList<ArrayList<Volunteer>> finalTable, ArrayList<Volunteer> volunteers) {
    	//create output file:
    	File file = new File("./ScheduleOutput.csv");
    	 try { 
             // create FileWriter object with file as parameter 
             FileWriter outputfile = new FileWriter(file); 
       
             // create CSVWriter object filewriter object as parameter 
             CSVWriter writer = new CSVWriter(outputfile); 
             String[] header = new String [AVAILABILITYTITLES.size()+1];
             header[0] = "TypeShift/Dates";
             String temp;
             for (int i = 1; i < header.length; i++) {
            	 temp = AVAILABILITYTITLES.get(i-1);
            	 header[i] = temp;
             }
             writer.writeNext(header); 			//Writing the headers

             String[] data;// = new String[AVAILABILITYTITLES.size()+1];
             int tableIndexRowReader = 0;
             int dataIndex = 0;
             for(int tableIndexColumnReader = 0; tableIndexColumnReader < finalTable.get(tableIndexRowReader).size(); tableIndexColumnReader ++) 
             { 
                 dataIndex = 0;
            	 data = new String[AVAILABILITYTITLES.size()+1];
                 data[dataIndex] = "A";
                 dataIndex++; 
	             while(tableIndexRowReader < finalTable.size()) {
					if (tableIndexColumnReader < finalTable.get(tableIndexRowReader).size()) {
						data[dataIndex] = finalTable.get(tableIndexRowReader).get(tableIndexColumnReader).name;
						dataIndex++;
					}
					tableIndexRowReader = tableIndexRowReader + 2;
				}
				writer.writeNext(data);
				tableIndexRowReader = 0;
             }
             writer.close(); 
    	 }	catch (IOException e) { 
             e.printStackTrace(); 
         } 
		
	}

    //input: index of AVAILABILITYTITLES TABLE
    //output: Shift A or Shift B
	private static String integerToShift(int j) {
    	String answer = "";
    	int col;
    	if (j % 2 == 0) { // is even thus, is shift Type A
    		col = j/2;
    		answer = "Shift A";
    	}
    	else {				//is odd thus, is Type B
    		col = (j-1) / 2;
    		answer = "Shift B";
    	}
    	answer = AVAILABILITYTITLES.get(col) + " " + answer;
    	answer = answer.toLowerCase().replace("availability", "");
    	answer = answer.replace("[", "");
    	answer = answer.replace("]", "");
		return answer;
	}

	public static ArrayList<ArrayList<Volunteer>> generateFinalVolunteerSchedule(ArrayList<ArrayList<Volunteer>> allAvailabilities) {
    	ArrayList<ArrayList<Volunteer>> finalVolunteerList = new ArrayList<ArrayList<Volunteer>>();
        int shiftDate = 0;
        for (ArrayList<Volunteer> i : allAvailabilities) {  //traverses over the volunteer list
            finalVolunteerList.add(assignShiftTo(i, shiftDate, new ArrayList<Volunteer>()));
            shiftDate++;
        }
        return finalVolunteerList;
    }


    public static ArrayList<Volunteer> assignShiftTo (ArrayList<Volunteer> list, int shiftDate, ArrayList<Volunteer> volunteerSet) {
        if (volunteerSet.size() == VOLUNTEERS_PER_SHIFT) {
            return volunteerSet;
        }
        else if (list.size() < 1) {
            volunteerSet.add(new Volunteer("not Available"));
            return volunteerSet;
        }
        else if (list.size() == 1) {
            list.get(0).shiftAssigned.add(shiftDate);
            volunteerSet.add(list.get(0));
            return volunteerSet;
        }
        else {
            int min = list.get(0).shiftAssigned.size();
            Iterator<Volunteer> itr = list.iterator();
            Volunteer temp;
            while (itr.hasNext()){
                temp = itr.next();
                if (temp.shiftAssigned.size() < min) {
                    min = temp.shiftAssigned.size();
                }
            }
            for(Volunteer i : list) {
                if (i.shiftAssigned.size() == min) {
                    i.shiftAssigned.add(shiftDate);
                    volunteerSet.add(i);
                    list.remove(i);
                    break;
                }
            }
            return assignShiftTo(list, shiftDate, volunteerSet);
        }
    }

    public static ArrayList<ArrayList<Volunteer>> createMasterSchedule(ArrayList<Volunteer> volunteers) {
    	int totalShifts = AVAILABILITYCOL.size() * 2;
    	ArrayList<ArrayList<Volunteer>> allAvailabilities = new ArrayList<>();
    	for (int i = 0; i < totalShifts; i++) {
    		ArrayList <Volunteer> vol = new ArrayList<>();
    		allAvailabilities.add(vol);
		}
    	for (Volunteer i : volunteers) {					//traverse all volunteers
    		for (Integer j : i.shiftAvailable) {		//traverse the available shifts of such volunteer
    			allAvailabilities.get(j.intValue()).add(i);	//adding this volunteer to the respective allAvailabilityList
    		}
    	}

    	//sort the lists by shiftAvailable de menor a mayor
    	for (ArrayList<Volunteer> v : allAvailabilities) {
    		Collections.sort( v, new sortByNumberOfShiftsAvailables());
    	}

    	//lets now print the SORTED master list to see if works as expected:
    	System.out.println("Printing Sorted Matrix with all availabilities");
    	int day2 = 0;
    	String shift;
    	for (ArrayList<Volunteer> i : allAvailabilities) {
    		shift = integerToShift(day2++);
    		System.out.print(shift + " volunteers -> ");
    		for (Volunteer j : i ) {
    			System.out.print(j.name + ", ");
    		}
    		System.out.println(""); //printing a new line
    	}

    	return allAvailabilities;
    }

	public static class sortByNumberOfShiftsAvailables implements Comparator<Volunteer> 
	{ 
	    public int compare(Volunteer a, Volunteer b) 
	    { 
	        return a.shiftAvailable.size() - b.shiftAvailable.size(); 
	    } 
	} 

    private static void getDataIndexes (String [] titles) {
    	for (int i = 0; i < titles.length; i++) {
	    	if(titles[i].toLowerCase().contains("name")) {
				 NAMECOL = i; 
			 }
			 else if (titles[i].toLowerCase().contains("phone")) {
				PHONECOL = i; 
			 }
			 else if (titles[i].toLowerCase().contains("email")) {
				EMAILCOL = i; 
			 }
			 else if (titles[i].startsWith("Availability")) {
				AVAILABILITYTITLES.add(titles[i]);
				AVAILABILITYCOL.add(i); 
			}
    	}
    }
    
    private static int formulaShiftToInteger(Integer i, String type) {
    	int IndexRow = i - AVAILABILITYCOL.get(0); //considering the offset of the availabilies in the table
    	if (type.equals("Shift A")) {
    		
    		return (IndexRow * NUMBEROFTYPES) + 0;
    	}
    	else if (type.equals("Shift B")) {
    		return (IndexRow * NUMBEROFTYPES) + 1;
    	}
    	else {
    		System.out.println("ERROR SHIFT TIME AT formulaShiftToInteger");
    		return -1;
    	}
    }
    
    private static ArrayList<Integer> getAvailableShifts(String [] entry) {
    	ArrayList answer = new ArrayList<Integer>();
    	for( int i = 0; i < AVAILABILITYCOL.size(); i++) {
    		if (entry[AVAILABILITYCOL.get(i)].contains("Shift A")) {
    			answer.add(formulaShiftToInteger(AVAILABILITYCOL.get(i), "Shift A"));
    		}
    		if (entry[AVAILABILITYCOL.get(i)].contains("Shift B")) {
    			answer.add(formulaShiftToInteger(AVAILABILITYCOL.get(i), "Shift B"));
    		}
    	}
    	return answer;
    }
    
    public static ArrayList<Volunteer> loadVolunteersFromCSVFile() {
    	System.out.println("loadVolunteersFromCSVFile");
        //load the reader
    	CSVReader csvReader = null;
    	String [] nextLine;
    	ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();

    	try {
    		csvReader = new CSVReader(new FileReader("./data/data2.csv"));
    		System.out.println("reader created");
    		nextLine = csvReader.readNext();			// reading the titles of columns
    		getDataIndexes(nextLine);		// gets the indexes where name, email and phone are
    	     while ((nextLine = csvReader.readNext()) != null) {
    	    	 if (nextLine.length > 1) {
	    	    	 String name = nextLine[NAMECOL];
	    	    	 String phone = nextLine[PHONECOL];
	    	    	 String email = nextLine[EMAILCOL];
	    	    	 ArrayList<Integer> availableShifts = getAvailableShifts(nextLine);
	    	    	 Volunteer temp = new Volunteer(name, phone, email, availableShifts);
	    	    	 volunteers.add(temp);
    	    	 }
    	     }
    	} catch(Exception ee)
        {
            ee.printStackTrace();
        }
	    return volunteers;
    }

} 