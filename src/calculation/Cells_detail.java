/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author phamt
 */
public class Cells_detail {
    private int x_axis, y_axis, id_cell, marker_count, algorithm;
    private String start_time, end_time, color;
    private double avg_speed, indicator;
    private String url;
    
    public Cells_detail()
    {
        marker_count = 0;
    }
    
    public Cells_detail(String url)
    {
        this.url = url;
    }
    
    public void setX_axis(int x_axis) {
        this.x_axis = x_axis;
    }

    public void setY_axis(int y_axis) {
        this.y_axis = y_axis;
    }

    public void setId_cell(int id_cell) {
        this.id_cell = id_cell;
    }

    public void increaseMarkerCount() {
        this.marker_count ++;
    }

    public int getMarkerCount(){
        return this.marker_count;
    }
    
    public void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setAvg_speed(double avg_speed) {
        this.avg_speed = avg_speed;
    }

    public double getAvg_speed() {
        return avg_speed;
    }

    public void setIndicator(double indicator) {
        this.indicator = indicator;
    }
    
    /**
     * Post JSONObject array to API to save data
     *
     * @param jsonObject
     * @return 
     */
    public int postJsonToApi(JSONObject jsonObject)
    {
        StringBuilder response = new StringBuilder();
        try {
            URL markerUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) markerUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            try(OutputStream outputStream = connection.getOutputStream()){
                String data = jsonObject.toString();
                byte[] input = data.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }
            
            try(BufferedReader br = new BufferedReader(
            new InputStreamReader(connection.getInputStream(), "utf-8"))) {
              String responseLine = null;
              while ((responseLine = br.readLine()) != null) {
                  response.append(responseLine.trim());
              }
              System.out.println(response.toString());
          }
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println();
        return 0;
    }
    
    /**
     * Convert a cell to JSON
     *
     * @param oneCell
     * @return 
     */
    public JSONObject cellToJSONObject(Cells_detail oneCell)
    {
        JSONObject cell = new JSONObject();
        try {
            cell.put("x_axis", oneCell.x_axis);
            cell.put("y_axis", oneCell.y_axis);
            cell.put("id_cell", oneCell.id_cell);
            cell.put("start_time", oneCell.start_time);
            cell.put("end_time", oneCell.end_time);
            cell.put("marker_count", oneCell.marker_count);
            cell.put("avg_speed", oneCell.avg_speed);
            cell.put("color", oneCell.color);
            cell.put("indicator", oneCell.indicator);
            cell.put("algorithm", oneCell.algorithm);
            return cell;    
        } catch (JSONException e) {
            System.out.println(e);
        }
        return null;
    }
    
    /**
     * Print cells_detail record to test
     *
     * @param cell 
     */
    public void detail(Cells_detail cell)
    {
        System.out.println(cell.x_axis+" "+cell.y_axis+" "+cell.color+" "+cell.avg_speed
                +" "+cell.marker_count
        );
    }

    private String cellsToJson(Cells_detail[] cellArray) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
