package com.iitb.loadgenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MyBrowser extends WebViewClient {
	static long SECONDS_MILLISECONDS = 1000;
	static String LOGTAG = "DEBUG_MY_BROWSER";
	public static String js;
	
	int eventid;
	StringBuilder logwriter;
	boolean loggingOn;
	String baseURL;
	int totalResponseTime;
	Calendar pageStartTime = null;
	Calendar pageEndTime = null;
	
	MyBrowser(int id, String tbaseURL){
		eventid = id;
		logwriter = new StringBuilder();
		loggingOn = true;
		baseURL = tbaseURL;
		totalResponseTime = 0;
		logwriter.append("details: " + MainActivity.load.loadid + " " + eventid + " WEBVIEW" + "\n");
		logwriter.append("url: " + baseURL + "\n");
	}
   @Override
   public boolean shouldOverrideUrlLoading(WebView view, String url) {
      view.loadUrl(url);
      return true; //return true means this webview has handled the request. Returning false means host(system browser) takes over control
   }
   @Override
   public WebResourceResponse  shouldInterceptRequest (WebView view, String url){
	   //Log.d(LOGTAG + "-shouldInterceptReques-THREADID", url + " on " + android.os.Process.myTid());
	   if (url.startsWith("http")) {
		   //Log.d(LOGTAG + "-shouldInterceptRequest TRUE", "url= " +  url);
           return getResource(url);
       }
	   Log.d(LOGTAG + "-shouldInterceptRequest FALSE", "returning NULL " + url);
	   return null; //returning null means webview will load the url as usual.
   }
   
   public WebResourceResponse getResource(String url){
	   //Log.d(LOGTAG, "getJPG for url " + url);
	   HttpClient client = new DefaultHttpClient();
	   
	   HttpGet request = null;
	try {
		String newURL = getURL(url).toString();
		request = new HttpGet(newURL);
		//Log.d(LOGTAG, "url is " + newURL);
	} catch (MalformedURLException | URISyntaxException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		Log.d(LOGTAG + "-MALFORMED", "url malformed " + url);
		return null;
	}
	   
		
	 
		// add request header
//			request.addHeader("User-Agent", USER_AGENT);
		HttpResponse response;
		Calendar start = null;
		try {
			start = Calendar.getInstance();
			if(pageStartTime == null) pageStartTime = start;
			
			long startTime = start.getTimeInMillis();
			
			response = client.execute(request);
			
			int responseStatusCode = response.getStatusLine().getStatusCode();
			System.out.println("Response Code : " + responseStatusCode);
			
			
			Calendar end = Calendar.getInstance();
			long endTime = end.getTimeInMillis();
			WebResourceResponse wr = null;
			
			if(responseStatusCode == HttpURLConnection.HTTP_OK){
				if(response.getEntity() == null){
					Log.d(LOGTAG + "-RESPONSE-NULL", "response is NULL " + url);
					return null;
				}
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
			
				
				Header contentType = entity.getContentType();
				String mimeType = null;
				String charset = EntityUtils.getContentCharSet(entity);
				
				if(contentType != null){
					mimeType = contentType.getValue().split(";")[0].trim();
					Log.d(LOGTAG + "-HEADER-DETAILS", "mimeType=" + mimeType + " charset=" + charset);
				}
				wr = new WebResourceResponse(mimeType, "utf-8", is);
			}
			
			//String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
			if(true){
				
				if(loggingOn){
					totalResponseTime += (endTime - startTime); //cumulative response time
					Log.d(LOGTAG + "-SUCCESS", "LOGGING TRUE : response time [" + (endTime-startTime) + "] - " + url);
					String startTimeFormatted =  Utils.sdf.format(start.getTime());
					String endTimeFormatted =  Utils.sdf.format(end.getTime());
					
					if(responseStatusCode == HttpURLConnection.HTTP_OK){
						logwriter.append(Constants.SUMMARY_PREFIX + url + " [SUCCESS] " + "[RT = " + (endTime-startTime) + "]" + " [" + startTimeFormatted + " , " + endTimeFormatted + "] " +
							 "\n");
					}
					else{
						logwriter.append(Constants.SUMMARY_PREFIX + url + " [ERROR] " + "[ET = " + (endTime-startTime) + "]" + " [" + startTimeFormatted + " , " + endTimeFormatted + "] " +
								"[code " + responseStatusCode + "]" + "\n");
					}
				}
				else{
					Log.d(LOGTAG + "-SUCCESS", "LOGGING FALSE : response time [" + (endTime-startTime) + "] - " + url);
				}
				//MainActivity.js = responseString;
			}
			//return null;
			
			return wr;
			//return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			long startTime = start.getTimeInMillis();
			Calendar end = Calendar.getInstance();
			long endTime = end.getTimeInMillis();
			String startTimeFormatted =  Utils.sdf.format(start.getTime());
			String endTimeFormatted =  Utils.sdf.format(end.getTime());
			logwriter.append(Constants.SUMMARY_PREFIX + url + " [ERROR] " + "[ET = " + (endTime-startTime) + "]" + " [" + startTimeFormatted + " , " + endTimeFormatted + "] " +
					"[" + e.getMessage() + " | " + e.getCause() + "]" + "\n");
			/*logwriter.append("ERROR [" + startTimeFormatted + " , " + endTimeFormatted + "] " +
					"[RT = " + (endTime-startTime) + "] - " + url + "[" + e.getMessage() + "|" + e.getCause() + "]\n");*/
			Log.d(LOGTAG + "-ClientProtocolException", "clientprotocolexception " + url + " M "  + e.getMessage() + " C " + e.getCause());
			e.printStackTrace();
		} catch (IOException e){
			long startTime = start.getTimeInMillis();
			Calendar end = Calendar.getInstance();
			long endTime = end.getTimeInMillis();
			String startTimeFormatted =  Utils.sdf.format(start.getTime());
			String endTimeFormatted =  Utils.sdf.format(end.getTime());
			logwriter.append(Constants.SUMMARY_PREFIX + url + " [ERROR] " + "[ET = " + (endTime-startTime) + "]" + " [" + startTimeFormatted + " , " + endTimeFormatted + "] " +
					"[" + e.getMessage() + " | " + e.getCause() + "]" + "\n");
			/*logwriter.append("ERROR [" + startTimeFormatted + " , " + endTimeFormatted + "] " +
					"[RT = " + (endTime-startTime) + "] - " + url + "[" + e.getMessage() + "|" + e.getCause() + "]\n");*/
			Log.d(LOGTAG + "-IOEXCEPTION", "ioexception " + url + " M "  + e.getMessage() + " C " + e.getCause());
		}
	   
	   return null;
   }
   
   public static URL getURL(String rawURL) throws MalformedURLException, URISyntaxException{
	   URL url = new URL(rawURL);
	   URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
	   url = uri.toURL();
	   return url;
   }
   
   @Override
   public void onPageFinished(WebView view, String url) {
	   Log.d(LOGTAG, "########## onPageFinished() called for url " + baseURL);
	   pageEndTime = Calendar.getInstance();
       super.onPageFinished(view, url);
       
       if(loggingOn){
	       loggingOn = false; //no more log collection
	       
	       Runnable r = new Runnable() {
				public void run() {
					int num = MainActivity.numDownloadOver++;
					
					logwriter.append(Constants.SUMMARY_PREFIX + "summary total RT = " +  totalResponseTime + "\n");
					logwriter.append("success\n");
					logwriter.append(Constants.SUMMARY_PREFIX + Constants.LINEDELIMITER); //this marks the end of this log
					
					if(num+1 == MainActivity.load.events.size()){
						logwriter.append(Constants.EOF); //this indicates that all GET requests have been seen without interruption from either user/server
					}
					String logString = logwriter.toString();
					String msg = "";
				    String retmsg = Threads.writeToLogFile(MainActivity.logfilename, logString); //write the log to file. This is a synchronized operation, only one thread can do it at a time
						
					msg += retmsg;
						
						
					if(num+1 == MainActivity.load.events.size()){
						Log.d(LOGTAG, "Now wrapping up the experiment");
						//Dummy ending of all requests - assuming only one request
						
						msg += "Experiment over : all GET requests completed\n";
						//msg += "Trying to send log file\n";
					
						
					   Log.d(Constants.LOGTAG, "MyBrowser : Sending the log file");
						int ret = Threads.sendLog(MainActivity.logfilename);
						if(ret == 200){
							msg += "log file sent successfully\n";
						}
						else{
							msg += "log file sending failed\n";
						}
					}
					
					Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
					.putExtra(Constants.BROADCAST_MESSAGE, msg);
					
					// Broadcasts the Intent to receivers in this application.
					LocalBroadcastManager.getInstance(MainActivity.context).sendBroadcast(localIntent);
					MainActivity.removeWebView(eventid); //remove the reference to current this webview so that it gets garbage collected
				}
			};
			
			Thread t = new Thread(r);
			
	        t.start();
       }
       //MainActivity.webview1.setVisibility(View.VISIBLE);
       //MainActivity.progressBar.setVisibility(View.GONE);
       //MainActivity.goButton.setText("GO");
       /*if(!MainActivity.js.isEmpty()){
    	   Log.d(LOGTAG, "onPageFinished() : loading js = " + MainActivity.js);
    	   MainActivity.webview1.loadUrl(MainActivity.js);
    	   MainActivity.js = "";
       }*/
       //MainActivity.textview.setText(MainActivity.js);
   }
}