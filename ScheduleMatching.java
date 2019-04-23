import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collections;
import java.lang.Math;
import java.util.Random;

public class ScheduleMatching {

	public static int DAYS = 2;
	public static int SHIFTS_PER_DAY = 8;

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

    	//lets now print the master list to see if works as expected:
    	int day = 0;
    	for (ArrayList<Volunteer> i : allAvailabilities) {
    		System.out.print(" day " + day++ + " volunteers -> ");
    		for (Volunteer j : i ) {
    			System.out.print(j.name + " ");
    		}
    		System.out.println(""); //printing a new line
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
    		for (Integer j : i.shiftAvailables) {		//traverse the available shifts of such volunteer
    			allAvailabilities.get(j.intValue()).add(i);	//adding this volunteer to the respective allAvailabilityList
    		}
    	}
    	return allAvailabilities;
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