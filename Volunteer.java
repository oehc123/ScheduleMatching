import java.util.ArrayList; 

public class Volunteer {

	String name, email, phoneNumber;
	ArrayList<Integer> shiftAvailable; // = new ArrayList<String>();
	ArrayList<Integer> shiftAssigned; // = new ArrayList<String>();

	public Volunteer() {
		this.name = "defaultVolunteer";
	}

	public Volunteer (String name, String phoneNumber, String email, ArrayList shiftAvailable) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.shiftAvailable = shiftAvailable;
		this.shiftAssigned = new ArrayList<Integer>();
	}

	public Volunteer (String str) {			// used to fill up when volunteers are not available
		this.name = str;
		this.email = str;
		this.phoneNumber = str;
	}
	
	public int compareByShiftAssigned(Volunteer vol) {					//returns >0 if this has MORE than vol shifs assigned, 0 if equal, or <0 if this has LESS than vol shifts assigned
		//System.out.println("Jose thisVolunteer: " + this);
		//System.out.println("Jose vol: " + vol);
		return this.shiftAssigned.size() - vol.shiftAssigned.size();
	} 

	public String toString() {
		return name + " " + email + " " + phoneNumber + " shiftAvailable: " + shiftAvailable + " shiftAssigned: " + shiftAssigned;
	}

}