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
    //generate a single event and returns it
    public static String generateLine(Calendar cal, String mode, String type, String link){
        String line = type + " ";
        
        line += cal.get(Calendar.YEAR) + " ";
        line += cal.get(Calendar.MONTH) + " ";
        line += cal.get(Calendar.DAY_OF_MONTH) + " ";
        line += cal.get(Calendar.HOUR_OF_DAY) + " ";
        line += cal.get(Calendar.MINUTE) + " ";
        line += cal.get(Calendar.SECOND) + " ";
        line += cal.get(Calendar.MILLISECOND) + " ";
        line += mode + " ";
        line += link;
        return line + "\n";
    }
    
    /**
    * reads event file line-by line uploaded by web-client and genetates events
    * which are send to the android-clients 
    */
    public static String generateEvents(int expid){
		String error="ERROR";
		String data="";
		String folderPath = Constants.mainExpLogsDir + expid + "/";
		String eventFile = Utils.getEventFileOfExperiment(expid);
		if(eventFile.equals(Constants.ERRORFILE)) return error+": file not found";

		Path path = Paths.get(folderPath, eventFile);
		System.out.println(path);
		Charset charset = Charset.forName("UTF-8");
		Calendar cal = Calendar.getInstance();
		try (BufferedReader reader = Files.newBufferedReader(path , charset)) {
			String line=null;
			while ((line = reader.readLine()) != null ) {
				if (line.isEmpty() || line.trim().equals("") || line.trim().equals("\n")) continue;

				String[] lineVariables = line.split(" ");
				int offset = Integer.parseInt(lineVariables[1]);
				cal.add(Calendar.SECOND, offset);
				data+=generateLine(cal,lineVariables[2],lineVariables[0],lineVariables[3]);
				System.out.println(data);
				cal.add(Calendar.SECOND, -1*offset);
			}
		} catch (IOException e) {
			System.err.println(e);
			return error;
		}
		if(data.equals("")) return error;
		return expid + "\n" + data;
	}
}
