import java.io.*;
import java.net.*;
public class smtpClient {
    public static void main(String[] args) {
// declaration section:
// smtpClient: our client socket
// os: output stream
// is: input stream
        Socket smtpSocket = null;  
        DataOutputStream os = null;
        DataInputStream is = null;
// Initialization section:
// Try to open a socket on port 25
// Try to open input and output streams
        try {
            smtpSocket = new Socket("www.google.com", 80);
            os = new DataOutputStream(smtpSocket.getOutputStream());
            is = new DataInputStream(smtpSocket.getInputStream());
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: hostname" );
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname" );
            e.printStackTrace();
        }

        System.out.println("Here");
// If everything has been initialized then we want to write some data
// to the socket we have opened a connection to on port 25
    if (smtpSocket != null && os != null && is != null) {
            try {
// The capital string before each colon has a special meaning to SMTP
// you may want to read the SMTP specification, RFC1822/3
            	os.writeBytes("GET / HTTP/1.1\r\n");
        		/*
                os.writeBytes("Connection: close\r\n");
                os.writeBytes("Cache-Control: no-cache\r\n");
                os.writeBytes("User-Agent: Mozilla/5.0\r\n");
                os.writeBytes("Accept-Encoding: gzip,deflate,sdch\r\n"); // message body
                */
                os.writeBytes("\r\n");
// keep on reading from/to the socket till we receive the "Ok" from SMTP,
// once we received that then we want to break.
                String responseLine;
                while ((responseLine = is.readLine()) != null) {
                    System.out.println("Server: " + responseLine);
                    if (responseLine.indexOf("Ok") != -1) {
                      break;
                    }
                }
// clean up:
// close the output stream
// close the input stream
// close the socket
        os.close();
                is.close();
                smtpSocket.close();  
                System.out.println("The End"); 
            } catch (UnknownHostException e) {
                System.err.println("Trying to connect to unknown host: " + e);
            } catch (IOException e) {
                System.err.println("IOException:  " + e);
            }
        }
    }           
}