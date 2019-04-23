import java.util.ArrayList; 

public class Volunteer {

	String name, email, phoneNumber;
	ArrayList<Integer> shiftAvailables; // = new ArrayList<String>();
	ArrayList<Integer> shiftAssigned; // = new ArrayList<String>();

	public Volunteer(){
		this.name = "DefaultDay";
	}

	public Volunteer (String name, String email, String phoneNumber, ArrayList shiftAvailables) {
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.shiftAvailables = shiftAvailables;
	}

	public String toString() {
		return name + " " + email + " " + phoneNumber + " " + shiftAvailables;
	}

}