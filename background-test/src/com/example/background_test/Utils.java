package com.example.background_test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class Utils {
	static SimpleDateFormat sdf = new SimpleDateFormat("ZZZZ HH:mm:s : S", Locale.US);
	static String Tag = "Background-Test";
	static String getTimeInFormat(){
		Calendar cal = Calendar.getInstance();
		return sdf.format(cal.getTime());
	}
	
	static Date getTime(){
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
	
	
	static void writeLogFile(String fileName){
      FileWriter fw = null;
      File log = null;
      log = new File(fileName);
      try {
			fw = new FileWriter(log.getAbsoluteFile(), true);
			int i=1;
	        while(i<1000){
					fw.write("LOG EVENT TIME: " + getTimeInFormat() + "\n");
					if(i%100 == 0){
						Log.d(Tag, "File " + fileName +" Writing " + i + "\n");
					}
					i++;
	        }
	        fw.close();
//	        try {
//	            Thread.sleep(3000);
//	        } catch(InterruptedException ex) {
//	            Thread.currentThread().interrupt();
//	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
