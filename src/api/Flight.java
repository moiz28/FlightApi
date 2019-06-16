package api;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class Flight {
	public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/flights", new FlightHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
	
	 public static String readFileAsString()throws Exception 
	  { 
	    String data = ""; 
	    data = new String(Files.readAllBytes(Paths.get("/Users/moiz/Desktop/finalWorkSpace3/FlightApi/src/api/flights.json"))); 
	    return data; 
	  } 

    static class FlightHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	ArrayList<String> selectedFlights = new ArrayList<String>();
        	String query = uri.getQuery();
        	String[] querySplitted = query.split("=");
        	String departureTime = querySplitted[1];
			
			SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");
			Date date = null;
			try {
				date = parseFormat.parse(departureTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calMIN = Calendar.getInstance();
			Calendar calMAX = Calendar.getInstance();
			calMIN.setTime(date);
			calMAX.setTime(date);
			calMIN.add(Calendar.HOUR_OF_DAY, -5);
			calMAX.add(Calendar.HOUR_OF_DAY, 5);
			JSONObject obj = null; 
			try {
				String dataStr = readFileAsString();
				System.out.println(dataStr);
				obj = new JSONObject(dataStr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			JSONArray objArr = null;
			try {
				objArr = (JSONArray) obj.get("flights");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			for(int i = 0; i < objArr.length(); i++)
            {
                try {
					//System.out.println(objArr.get(i));
                	JSONObject singleJSONObject = objArr.getJSONObject(i);
                    String strTime = singleJSONObject.getString("departure");
                    String flightName = singleJSONObject.getString("flight");
                    Calendar invTimeCal = Calendar.getInstance();
                    Date invTime = parseFormat.parse(strTime);
                    invTimeCal.setTime(invTime);
                    if((invTimeCal.getTime().compareTo(calMIN.getTime()) > 0 || invTimeCal.getTime().compareTo(calMIN.getTime()) == 0) 
                    		&& (invTimeCal.getTime().compareTo(calMAX.getTime()) < 0 || invTimeCal.getTime().compareTo(calMAX.getTime()) == 0) ){
                    	
                        String flight = "Flight: "+ flightName + " Departure Time: " + strTime + "\n";
                        selectedFlights.add(flight);            	
                    }
                    
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }
			// check arraylist length

			String response = selectedFlights.toString().replace('[',' ').replace(']', ' ').replace(',', ' ');
			t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();  
        }
    }
}
