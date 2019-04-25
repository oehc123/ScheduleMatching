import java.util.ArrayList; 

public class Volunteer {

	String name, email, phoneNumber;
	ArrayList<Integer> shiftAvailable; // = new ArrayList<String>();
	ArrayList<Integer> shiftAssigned; // = new ArrayList<String>();

	public Volunteer (String name, String email, String phoneNumber, ArrayList shiftAvailable) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.shiftAvailable = shiftAvailable;
		this.shiftAssigned = new ArrayList<Integer>();
	}

	public Volunteer (String notAvailable) {			// used to fill up when volunteers are not available
		this.name = "notAvailable on this day";
		this.email = "notAvailable on this day";
		this.phoneNumber = "notAvailable on this day";
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