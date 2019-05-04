import java.io.FileReader;
import java.util.*; 
import java.io.*; 

import com.opencsv.CSVReader;

import javafx.util.Pair;

public class ScheduleMatching {

	public static int DAYS = 2;
	public static int SHIFTS_PER_DAY = 8;
	public static int NAMECOL, PHONECOL, EMAILCOL;
	public static ArrayList<Integer> AVAILABILITYCOL = new ArrayList<Integer>();
	public static int NUMBEROFTYPES = 2;
	public static ArrayList<String> AVAILABILITYTITLES = new ArrayList<String>();;

    public static void main(String[] args) {
    /*	int totalShifts = SHIFTS_PER_DAY * DAYS;
        System.out.println("Hello, to Jose's ScheduleMatching project.");
        System.out.println("This sample consiste of " + DAYS + " days event, ");
        System.out.println("with " + SHIFTS_PER_DAY + " shifts");
        System.out.println("with a total of " + totalShifts + " shifts to be filled up");
        System.out.println("Let's see how what is the best way for us to fill it up");
*/
    	//int totalShifts = AVAILABILITYCOL.size() * NUMBEROFTYPES;
    	int totalShifts = 18;
    	
        //below we populate volunteers
        ArrayList<Volunteer> volunteers = loadVolunteersFromCSVFile(totalShifts);
 //       System.out.println("there is a total of " + volunteers.size() + " volunteers in this run ");
 //       System.out.println("created volunteers are: ");
        for (Volunteer i : volunteers) {
    		System.out.println(i);
    	}
        
    	//2) traverse all the shifts of all volunteers to create our total matrix of shifts
    	ArrayList<ArrayList<Volunteer>> allAvailabilities = createMasterSchedule(volunteers, totalShifts);

    	//3) assignt who are the selected volunteers for each shift
    	//based on previous shifts assigned
    	// and based on less shit availables
    	ArrayList<Volunteer> finalVolunteerList = generateFinalVolunteerSchedule(allAvailabilities);

    	//4) print final volunteer list to study results:

    	System.out.println("The final Schedule is: ");
    	int j = 0;
    	for(Volunteer i : finalVolunteerList) {
    		String shift = integerToShift(j++);
    		System.out.println(shift + " assigned to " + i.name);
    	}

    	System.out.println("***********************");
    	System.out.println("The final Volunteer list is: ");

    	for(Volunteer i : volunteers) {
    		System.out.println(i.name + " shiftAssigned: " + i.shiftAssigned);
    	}
    }

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
		return answer;
	}

	public static ArrayList<Volunteer> generateFinalVolunteerSchedule(ArrayList<ArrayList<Volunteer>> allAvailabilities) {
    	ArrayList<Volunteer> finalVolunteerList = new ArrayList<Volunteer>();
    	int shiftDate = 0;
    	for (ArrayList<Volunteer> i : allAvailabilities) {	//traverses over the volunteer list
    		if (i.size() < 1) {
    			finalVolunteerList.add(new Volunteer("not Available"));
    		}
    		else {
    			finalVolunteerList.add(assignShiftTo(i, shiftDate));
    		}
    		shiftDate++;
    	}
    	return finalVolunteerList;
    }


    public static Volunteer assignShiftTo (ArrayList<Volunteer> list, int shiftDate) {
    	Volunteer volFinal = new Volunteer();
    	if (list.size() == 1) {
    		list.get(0).shiftAssigned.add(shiftDate);
    		volFinal = list.get(0);
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
					volFinal = i;
					break;
				}
			}
			volFinal.shiftAssigned.add(shiftDate);
		}
		return volFinal;
    }

    public static ArrayList<ArrayList<Volunteer>> createMasterSchedule(ArrayList<Volunteer> volunteers, int totalShifts) {
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
    	System.out.println("Printing Sorted Matrix");
    	int day2 = 0;
    	String shift;
    	for (ArrayList<Volunteer> i : allAvailabilities) {
    		shift = integerToShift(day2++);
    		System.out.print(shift + " volunteers -> ");
    		for (Volunteer j : i ) {
    			System.out.print(j.name + " ");
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
    
    public static ArrayList<Volunteer> loadVolunteersFromCSVFile(int totalShifts) {
    	System.out.println("loadVolunteersFromCSVFile");
        //load the reader
    	CSVReader csvReader = null;
    	String [] nextLine;
    	ArrayList<Volunteer> volunteers = new ArrayList<Volunteer>();

    	try {
    		csvReader = new CSVReader(new FileReader("./data/data1.csv"));
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
        //read lines

	    return volunteers;
    }

} 