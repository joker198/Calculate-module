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
public class Rectangle implements NeededTool{
    private int id, height, width, cell_width, cell_height;
    private double east, west, south, north;
    private String getUrl, postUrl;
    
    public Rectangle()
    {
        //
    }

    public int getCell_width() {
        return cell_width;
    }

    public void setCell_width(int cell_width) {
        this.cell_width = cell_width;
    }

    public int getCell_height() {
        return cell_height;
    }

    public void setCell_height(int cell_height) {
        this.cell_height = cell_height;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public Rectangle(String getUrl)
    {
        this.getUrl = getUrl;
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
        JSONArray jsonArray = getJsonFromAPI(this.getUrl);
        return this.jsonToRectangle(jsonArray);
    } 

    /**
     * @param getUrl
     * @return 
     */
    @Override
    public JSONArray getJsonFromAPI(String getUrl) {
        StringBuilder response = new StringBuilder();
        try {
            URL markerUrl = new URL(getUrl);
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
     * @return Rectangle Array
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
}
