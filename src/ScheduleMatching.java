/*
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;
import java.lang.Math;
import java.util.Random;*/
import java.util.*; 
import java.lang.*; 
import java.io.*; 

public class ScheduleMatching {

	public static int DAYS = 2;
	public static int SHIFTS_PER_DAY = 8;
	public static int VOLUNTEERS_PER_SHIFT = 4;

    public static void main(String[] args) {
    	int totalShifts = SHIFTS_PER_DAY * DAYS;
        System.out.println("Hello, to Jose's ScheduleMatching project.");
        System.out.println("This sample consiste of " + DAYS + " days event, ");
        System.out.println("with " + SHIFTS_PER_DAY + " shifts");
        System.out.println("with a total of " + totalShifts + " shifts to be filled up");
        System.out.println("Let's see how what is the best way for us to fill it up");

        //below we populate volunteers
        ArrayList<Volunteer> volunteers = generateVolunteers(totalShifts);
        System.out.println("there is a total of " + volunteers.size() + " volunteers in this run ");
        System.out.println("created volunteers are: ");
        for (Volunteer i : volunteers) {
    		System.out.println(i);
    	}

    	//2) traverse all the shifts of all volunteers to create our total matrix of shifts
    	ArrayList<ArrayList<Volunteer>> allAvailabilities = createMasterSchedule(volunteers, totalShifts);

    	//3) assignt who are the selected volunteers for each shift
    	//based on previous shifts assigned
    	// and based on less shit availables
    	ArrayList<ArrayList<Volunteer>> finalVolunteerList = generateFinalVolunteerSchedule(allAvailabilities);

    	//4) print final volunteer list to study results:

    	System.out.println("The final Schedule is: ");
    	int j = 0;
    	for( ArrayList<Volunteer> i : finalVolunteerList) {
    		for( Volunteer v : i) {
    			System.out.print(" for day " + j + " assingt to: ");
        		System.out.println(v.name);
    		}
    		j++;
    	}

    	System.out.println("***********************");
    	System.out.println("The final Volunteer list is: ");

    	for( Volunteer i : volunteers) {
    		System.out.println(i.name + " shiftAssigned: " + i.shiftAssigned);
    	}
    }

    public static ArrayList<ArrayList<Volunteer>> generateFinalVolunteerSchedule(ArrayList<ArrayList<Volunteer>> allAvailabilities) {
    	ArrayList<ArrayList<Volunteer>> finalVolunteerList = new ArrayList<ArrayList<Volunteer>>();
    	int shiftDate = 0;
    	for (ArrayList<Volunteer> i : allAvailabilities) {	//traverses over the volunteer list
    		finalVolunteerList.add(assignShiftTo(i, shiftDate, new ArrayList<Volunteer>()));
    		shiftDate++;
    	}
    	return finalVolunteerList;
    }
    
//this returns an array list with the set of volunteers for that specific shift
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

    public static ArrayList<ArrayList<Volunteer>> createMasterSchedule(ArrayList<Volunteer> volunteers, int totalShifts) {
    	ArrayList<ArrayList<Volunteer>> allAvailabilities = new ArrayList<>();
    	//prepopulate MasterArray
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
    	for (ArrayList<Volunteer> i : allAvailabilities) {
    		System.out.print(" day " + day2++ + " volunteers -> ");
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

    //Generates volunteers for testing
    //the number of volunteers are SHIFTS_PER_DAY + DAYS (random assumption)
    //volunteers will have at least DAYS/2 numbers of shifts availables
    public static ArrayList<Volunteer> generateVolunteers(int totalShifts) {
    	System.out.println("Generating Volunteers");
    	int number_of_volunteers = SHIFTS_PER_DAY + DAYS;
		int min_number_of_shifts_per_volunteer = DAYS/2;
		ArrayList volunteers = new ArrayList<Volunteer>();
		Random random = new Random();
		int temp;

		for (int i = 0; i < number_of_volunteers; i++) {	// loops by all the volunteers
			//create array of available shifts for volunteer
			int shiftsAvailableForThisVolunteer = random.nextInt((totalShifts - min_number_of_shifts_per_volunteer) + 1) + min_number_of_shifts_per_volunteer;
			ArrayList availableShifts = new ArrayList<Integer>();
			for (int j = 0; j < shiftsAvailableForThisVolunteer; j++) {		//creates the different availables shifts
				do {
					temp = random.nextInt(totalShifts);
				} while (availableShifts.contains(temp));
				availableShifts.add(temp);
			}
			Collections.sort(availableShifts);
			Volunteer tempVolunteer = new Volunteer ("Volunteer" + i, "Volunteer" + i + "@email.com", "xxx-xxx-xxxx", availableShifts);
			volunteers.add(tempVolunteer);
			System.out.print(".");
		}
		System.out.println(".");
	    return volunteers;
    }

} 