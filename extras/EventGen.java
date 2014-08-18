import java.util.Calendar;
import java.util.Random;
public class EventGen{
    //Just for testing parseLine
    public static String generateLine(Calendar cal, String type){
        String line = type + " ";
        
        line += cal.get(Calendar.YEAR) + " ";
        line += cal.get(Calendar.MONTH) + " ";
        line += cal.get(Calendar.DAY_OF_MONTH) + " ";
        line += cal.get(Calendar.HOUR_OF_DAY) + " ";
        line += cal.get(Calendar.MINUTE) + " ";
        line += cal.get(Calendar.SECOND) + " ";
        line += cal.get(Calendar.MILLISECOND) + " ";
        
        line += "http://www.cse.iitb.ac.in/~ashishsonone/serve.php?user=ashish@" + cal.get(Calendar.SECOND);
        return line;
    }
    
    public static void generateEvents(int count){
        Random rand = new Random();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 120); //two minutes in the future. So that even after 1 min these events are not stale
        for(int i=0; i<count; i++){
            String line = generateLine(cal, "GET");
            System.out.println(line);
            cal.add(Calendar.SECOND, 5 + rand.nextInt(15));
            cal.add(Calendar.MILLISECOND, rand.nextInt(1000));
        }
    }

    public static void main(String[] args){
        generateEvents(10);
    }
}
