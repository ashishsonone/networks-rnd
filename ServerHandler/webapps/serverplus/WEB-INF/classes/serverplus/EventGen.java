package serverplus;
import java.util.Calendar;
import java.util.Random;
public class EventGen{
    //Just for testing parseLine
    public static String generateLine(Calendar cal, String type, String link){
        String line = type + " ";
        
        line += cal.get(Calendar.YEAR) + " ";
        line += cal.get(Calendar.MONTH) + " ";
        line += cal.get(Calendar.DAY_OF_MONTH) + " ";
        line += cal.get(Calendar.HOUR_OF_DAY) + " ";
        line += cal.get(Calendar.MINUTE) + " ";
        line += cal.get(Calendar.SECOND) + " ";
        line += cal.get(Calendar.MILLISECOND) + " ";
        
        line += link;
        return line + "\n";
    }
    
    public static String generateEvents(int count){
    	String data ="";
        Random rand = new Random();
        Calendar cal = Calendar.getInstance();
        //data += Long.toString(cal.getTimeInMillis());		//id of events
        //data += Integer.toString(Main.experimentID+1);	//generate events with id one more than the current experimentID
        //data +="\n";
        
        cal.add(Calendar.SECOND, 60*1); 
        for(int i=0; i<count; i++){
        	//String link = "http://www.cse.iitb.ac.in/~ashishsonone/video.mp4";
        	//String link = "http://192.168.150.1/video.mp4";
        	String link = "http://www.cse.iitb.ac.in/~ashishsonone/video.mp4";
            String line = generateLine(cal, "GET", link);
            data+=line;
            cal.add(Calendar.SECOND, 5 + rand.nextInt(15));
            cal.add(Calendar.MILLISECOND, rand.nextInt(1000));
        }
        
        return data;
    }
}
