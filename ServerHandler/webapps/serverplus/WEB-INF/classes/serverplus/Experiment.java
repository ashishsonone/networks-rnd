package serverplus;
import lombok.Getter;
import lombok.Setter;

public class Experiment{
	@Getter @Setter int ID=0;
	@Getter @Setter String Name="";
	@Getter @Setter String Location="IIT Bombay";
	@Getter @Setter String Description="";
	
	
	public Experiment(String a, String b, String c){
		Name=a;Location=b; Description=c;
	}
	
	public void print(){
		System.out.println("printing experiment details....");
		System.out.println("expID: " +ID);
		System.out.println("Name: " +Name);
		System.out.println("Location: " +Location);
		System.out.println("Description: " +Description);
	}
	
	
}
