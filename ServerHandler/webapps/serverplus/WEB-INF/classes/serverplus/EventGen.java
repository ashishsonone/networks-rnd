package serverplus;
import java.util.Calendar;
import java.util.Random;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.BufferedReader;


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
    
    /*
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
        	//String link = "http://www.cse.iitb.ac.in/~ashishsonone/video.mp4";
        	String link = "http://www.cse.iitb.ac.in/~ashishsonone/gaana.mp3";
            String line = generateLine(cal, "GET", link);
            data+=line;
            cal.add(Calendar.SECOND, 5 + rand.nextInt(15));
            cal.add(Calendar.MILLISECOND, rand.nextInt(1000));
        }
        
        return data;
    }
    */
    
    public static String generateEvents(int expid){
		String data="";
		String folderPath = Constants.mainExpLogsDir + Integer.toString(expid) + "/";
		Path path = Paths.get(folderPath, Constants.eventFile);
		Charset charset = Charset.forName("UTF-8");
		Calendar cal = Calendar.getInstance();
		try (BufferedReader reader = Files.newBufferedReader(path , charset)) {
			String line=null;
			while ((line = reader.readLine()) != null ) {
				String[] lineVariables = line.split(" ");
				int offset = Integer.parseInt(lineVariables[1]);
				cal.add(Calendar.SECOND, offset);
				data+=generateLine(cal,lineVariables[0],lineVariables[2]);
				System.out.println(data);
				cal.add(Calendar.SECOND, -1*offset);
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		return data;
	}
}
