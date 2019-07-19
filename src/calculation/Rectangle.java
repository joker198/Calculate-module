/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class Rectangle implements NeededTool{
    private int id, height, width;
    private double east, west, south, north;
    private String url;
    
    public Rectangle()
    {
        //
    }
    public Rectangle(String url)
    {
        this.url = url;
    }

    public int getId() {
        return id;
    }
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getEast() {
        return east;
    }

    public double getWest() {
        return west;
    }

    public double getSouth() {
        return south;
    }

    public double getNorth() {
        return north;
    }
    
    /**
     * @return
     * @throws JSONException 
     */
    public Rectangle[] getRectangles() throws JSONException
    {
        JSONArray jsonArray = getJsonFromAPI(this.url);
        return this.jsonToRectangle(jsonArray);
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
     * Convert JSONArray to array Rectangle
     *
     * @param jsonArray
     * @return
     * @throws JSONException 
     */
    private Rectangle[] jsonToRectangle(JSONArray jsonArray) throws JSONException
    {
        int numOfRecord = jsonArray.length();
        Rectangle rectangles[] = new Rectangle[numOfRecord];
        for (int i = 0; i < numOfRecord; ++i) {
            rectangles[i] = new Rectangle();
            JSONObject aRecord = new JSONObject(jsonArray.getJSONObject(i).toString());
            rectangles[i].id = aRecord.getInt("id");
            rectangles[i].height = aRecord.getInt("height");
            rectangles[i].width = aRecord.getInt("width");
            rectangles[i].east = (double) aRecord.getDouble("east");
            rectangles[i].west = (double) aRecord.getDouble("west");
            rectangles[i].south = (double) aRecord.getDouble("south");
            rectangles[i].north = (double) aRecord.getDouble("north");
        }
        return rectangles;
    }
    
    /**
     * Print Rectangle record to test
     *
     * @param rectangle 
     */
    public void displayRectangle(Rectangle rectangle)
    {
        System.out.println(rectangle.height+" "+rectangle.width+"\n");
    }
}
