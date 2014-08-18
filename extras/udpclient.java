import java.io.*;
import java.net.*;
public class udpclient {
    public static void main(String[] args) {
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket client = null;
        InetAddress IPAddress = null;
       
        
        try {
        	client = new DatagramSocket();
        	IPAddress = InetAddress.getByName("10.3.101.113");
        }
         catch (Exception e) {
            System.err.println("Couldn't get I/O for the connection to: hostname" );
            e.printStackTrace();
        }
        
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];
        int i = 0;
        while(i++ < 5){
	        String sentence = "hello" + i;
	        sendData = sentence.getBytes();
	        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5000);
	        try{
	        	client.send(sendPacket);
	        }
	        catch(Exception e){
	        }
        }
        client.close();
        		
    }

}