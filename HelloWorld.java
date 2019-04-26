import java.util.*; 
import java.lang.*; 
import java.io.*; 

public class HelloWorld {
	

    public static void main(String[] args) {
        // Prints "Hello, World" to the terminal window.
        System.out.println("Hello, World");
        ArrayList<Integer> arr = new ArrayList<Integer>();
        arr.add(11);
		arr.add(9);
		arr.add(4);
		arr.add(11);
		arr.add(3);

		Integer min = arr.get(0);
		Iterator<Integer> itr = arr.iterator();
		Integer temp;
		while (itr.hasNext()){
			temp = itr.next();
			if (temp.compareTo(min) < 0) {
				min = temp;
			}
		}
		System.out.println(" the min of the list is: " + min);
    }
}