import java.io.*;
import java.net.*;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

public class httpclient {
    public static void main(String[] args) {
    	HttpClient httpClient = new DefaultHttpClient();
    	//HttpGet httpGet = new HttpGet("http://www.cse.iitb.ac.in/~ashishsonone/ip.txt");
    	HttpMethod method = new GetMethod("http://www.cse.iitb.ac.in/~ashishsonone/ip.txt");
    	try {
    		HttpResponse response = httpClient.execute(httpGet);
    		System.out.println(response);
    	} catch (ClientProtocolException e) {
            // writing exception to log
            e.printStackTrace();
        } catch (IOException e) {
            // writing exception to log
            e.printStackTrace();
        }
    }
}