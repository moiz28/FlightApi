package api;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    static class FlightHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	URI uri = t.getRequestURI();
        	String query = uri.getQuery();
        	String[] querySplitted = query.split("=");
        	String departureTime = querySplitted[1];
			
			SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
			SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mma");
			Date date = null;
			try {
				date = parseFormat.parse(departureTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(parseFormat.format(date) + " = " + displayFormat.format(date));
			
			JSONObject obj = null;
			try {
				obj = new JSONObject("{  \"flights\": [{\"flight\": \"Air Canada 8099\",\"departure\": \"7:30AM\"},{\"flight\": \"United Airline 6115\",\"departure\": \"10:30AM\"},{\"flight\": \"WestJet 6456\",\"departure\": \"12:30PM\"},{\"flight\": \"Delta 3833\",\"departure\": \"3:00PM\"}  ]}");
			} catch (JSONException e) {
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
					System.out.println(objArr.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
			
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
