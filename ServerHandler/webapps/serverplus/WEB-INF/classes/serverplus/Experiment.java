package serverplus;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author sanchitgarg
 * This class is used for holding information about an experiment.
 * 
 */
public class Experiment{
	@Getter @Setter int ID=-1;
	@Getter @Setter String Name="";
	@Getter @Setter String Location="IIT Bombay";
	@Getter @Setter String Description="";
	@Getter @Setter String User="";
	@Getter @Setter String FileName="";
	
	/**
	* constructor
	*/
	public Experiment(String a, String b, String c, String d, String e){
		Name=a;Location=b; Description=c;User=d;FileName=e;
	}
	
	/**
	* prints the experiment information
	*/
	public void print(){
		System.out.println("printing experiment details....");
		System.out.println("expID: " +ID);
		System.out.println("Name: " +Name);
		System.out.println("Location: " +Location);
		System.out.println("Description: " +Description);
		System.out.println("User: " +User);
		System.out.println("FileName: " +FileName);
	}
	
	
}