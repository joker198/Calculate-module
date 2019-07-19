package calculation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author phamt
 */
public class Marker implements NeededTool{
    private int record_user;
    private String url, vehicle, direct, record_time, distance, created_at, updated_at;
    private double lat, lng, speed;
    private String start_time, end_time;
    
    public int getRecord_user() {
        return record_user;
    }

    public String getVehicle() {
        return vehicle;
    }

    public String getDirect() {
        return direct;
    }

    public String getRecord_time() {
        return record_time;
    }

    public String getDistance() {
        return distance;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getSpeed() {
        return speed;
    }
    
    public Marker()
    {
        //
    }
    
    public Marker(String url, String start_time, String end_time)
    {
        this.url = url;
        this.start_time = start_time;
        this.end_time = end_time;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public Marker[] getMarkers() throws JSONException
    {
        JSONArray jsonArray = getJsonFromAPI(this.url);
        return this.jsonToMarkers(jsonArray);
    }
    
    /**
     * @param url
     * @return 
     */
    @Override
    public JSONArray getJsonFromAPI(String url) {
        StringBuilder response = new StringBuilder();
        try {
            URL markerUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) markerUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("start_time", start_time);
            connection.setRequestProperty("end-time", end_time);
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            JSONArray result = new JSONArray(response.toString());
            return result;
        } catch (JSONException ex) {
            Logger.getLogger(Marker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * @param jsonArray
     * @return
     * @throws JSONException 
     */
    private Marker[] jsonToMarkers(JSONArray jsonArray) throws JSONException {
        int size = jsonArray.length();
        Marker result[] = new Marker[size];
        for (int i = 0; i < size; ++i) {
            JSONObject aRecord = new JSONObject(jsonArray.getJSONObject(i).toString());
            result[i] = new Marker();
            result[i].lat = (double) aRecord.getDouble("lat");
            result[i].lng = (double) aRecord.getDouble("lng");
            result[i].speed = (double) aRecord.getDouble("speed");
            result[i].vehicle = aRecord.getString("vehicle");
            result[i].direct = aRecord.getString("direct");
            result[i].record_time = aRecord.getString("record_time");
            result[i].record_user = aRecord.getInt("record_user");
            result[i].created_at = aRecord.getString("created_at");
            result[i].updated_at = aRecord.getString("updated_at");
        }
        return result;
    }
    
    /**
     * @param marker 
     */
    public void displayMarker(Marker marker)
    {
        System.out.println(marker.lat+" "+marker.lng+"\n");
    }
}
