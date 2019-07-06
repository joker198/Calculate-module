/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author phamt
 */
public class Calculation {
    public static String markerUrl = "http://68.183.238.65:8000/api/data/markers";
    public static String rectangleUrl = "http://68.183.238.65:8000/api/data/rectangles";
    public static String cellsUrl = "http://68.183.238.65:8000/api/data/savedata";
    public static String start_time = "2019-05-10 02:25:47";
    public static String end_time = "2019-05-10 07:14:59";
    public static int count = 0;
    
    public static String matchColor(double speed)
    {
        if(speed <= 10) return "#FF0000";
        if(speed <= 20) return "#FFFF00";
        if(speed <= 30) return "#00FF00";
        return "#0000FF";
    }
    
    public static int whereX(double east, double west, double lng, int width)
    {
        int result = (int) Math.floor((lng - west) / (east - west) *width );
        if(result == width) return result-1;
        if(result < 0 || result > width) return -1;
        return result;
    }
    
    public static int whereY(double south, double north, double lat, int height)
    {
        int result = (int) Math.floor((lat - south) / (north - south)  * height);
        if(result == height) return result-1;
        if(result < 0 || result > height) return -1;
        return result;
    }
    
    public static JSONObject[] fitCells(Marker[] markers, Rectangle rectangle, int algorithm)
    {
        int id = rectangle.getId();
        int height = rectangle.getHeight();
        int width = rectangle.getWidth();
        double east = rectangle.getEast();
        double west = rectangle.getWest();
        double south = rectangle.getSouth();
        double north = rectangle.getNorth();
        
        Cells_detail cells[] = new Cells_detail[height * width];
        for (int i = 0; i < height * width; i++) {
            cells[i] = new Cells_detail();
        }
        
        count = 0;
        int x, y, temp, postion;
        double speed;
        
        for (Marker marker : markers) {
            x = whereX(east, west, marker.getLng(), width);
            y = whereY(south, north, marker.getLat(), height);
            postion = y * width + x;
            if(cells[postion].getMarkerCount() == 0) {
                ++count;
                cells[postion].setX_axis(x);
                cells[postion].setY_axis(y);
                cells[postion].setAlgorithm(algorithm);
                speed = marker.getSpeed();
                cells[postion].setAvg_speed(speed);
                cells[postion].setColor(matchColor(speed));
                cells[postion].setIndicator(speed);
                cells[postion].setId_cell(id);
                cells[postion].increaseMarkerCount();
                cells[postion].setStart_time(start_time);
                cells[postion].setEnd_time(end_time);
            } else {
                temp = cells[postion].getMarkerCount();
                speed = cells[postion].getAvg_speed();
                speed = ( marker.getSpeed() + speed * temp ) / ( temp + 1 );
                cells[postion].increaseMarkerCount();
                cells[postion].setAvg_speed(speed);
            }
        }
        
        JSONObject result[] = new JSONObject[count];
        for (int i = 0; i < height*width; i++) {
            if(cells[i].getMarkerCount() > 0) result[--count] = cells[i].cellToJSONObject(cells[i]);
        }
        return result;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Marker marker = new Marker(markerUrl, start_time, end_time);
        Rectangle rectangle = new Rectangle(rectangleUrl);
        Cells_detail cellsDetail = new Cells_detail(cellsUrl);
        try {
            Rectangle[] rectangles = rectangle.getRectangles();
            Marker[] markers = marker.getMarkers();
            JSONArray diget = new JSONArray();
            for (Rectangle aRectangle:rectangles) {
                JSONObject cells[] = fitCells(markers, aRectangle, 1);
                for (JSONObject cell : cells) {
                    diget.put(cell);
                }
            }
            //kết quả:
            JSONObject data = new JSONObject();
            data.put("data", diget);
            cellsDetail.postJsonToApi(data);
            System.out.println(data);
        } catch (JSONException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
