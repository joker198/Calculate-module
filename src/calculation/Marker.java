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
 * @author joker
 */
public class Marker{
    private String recordUserArray;
    private long record_user;
    private double lat, lng, speed;
    private String getUrl, vehicle, direct, record_time, distance, created_at, updated_at, start_time, end_time;
    
    public long getRecord_user()
    {
        return record_user;
    }

    public String getVehicle()
    {
        return vehicle;
    }

    public String getDirect()
    {
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
    public String getNumOfRecordUser()
    {
        return recordUserArray;
    }
    
    public Marker()
    {
        //
    }
    
    public Marker(String getUrl, String start_time, String end_time)
    {
        this.getUrl = getUrl;
        this.start_time = start_time;
        this.end_time = end_time;
        this.recordUserArray = "";
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public Marker[] getMarkers() throws JSONException
    {
        JSONObject responseData = getJsonFromAPI(this.getUrl);
        this.recordUserArray = responseData.getJSONArray("user").toString();
        JSONArray markers = responseData.getJSONArray("markers");
        return this.jsonToMarkers(markers);
    }
    
    /**
     * @param getUrl
     * @return 
     */
    public JSONObject getJsonFromAPI(String getUrl) {
        StringBuilder response = new StringBuilder();
        try {
            URL markerUrl = new URL(getUrl);
            HttpURLConnection connection = (HttpURLConnection) markerUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("starttime", start_time);
            connection.setRequestProperty("endtime", end_time);
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                System.out.println(response.toString());
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        try {
            JSONObject result = new JSONObject(response.toString());
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
            result[i].record_user = aRecord.getLong("record_user");
        }
        return result;
    }
}
