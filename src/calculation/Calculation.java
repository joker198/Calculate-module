package calculation;

import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author joker
 */
public class Calculation {
    public static String markerUrl = "http://68.183.238.65:8000/api/data/markers";
    public static String rectangleUrl = "http://68.183.238.65:8000/api/data/rectangles";
    public static String cellsUrl = "http://68.183.238.65:8000/api/data/savedata";
    public static int count = 0, markerIndex = 0, ALGORITHM;
    public static String START_TIME;
    public static String END_TIME;
    public static final int CELLSIZE = 250;
    public static final int DURATION = 30;
    public static String[] mileStones;

    public static enum Interval {
        TINY(5),SMALL(10), DEFAULT(15), MEDIUM(30);
        private final int value;
        private Interval(int value) {
            this.value = value;
        }

    }

    /**
     * Initial calculate module with algorithm id
     *
     * @param algorithm
     * @return 
     */
    public Calculation(int algorithm)
    {
        try {
            mileStones = CalculateModule.initSplitDuration(DURATION, Interval.DEFAULT.value);
            //mileStones = CalculateModule.splitDuration("2019-05-10 07:15:04", 30,Interval.TINY.value);
        } catch (ParseException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        }
        START_TIME = mileStones[0];
        END_TIME = mileStones[mileStones.length-1];
        ALGORITHM = algorithm;
    }

    /**
     * 
     * @param markers
     * @param rectangle
     * @param mileStonesIndex
     * @return 
     */
    public static JSONObject[] fitCellsByMileStones(Marker[] markers, Rectangle rectangle, int mileStonesIndex){
        int id = rectangle.getId();
        int height = rectangle.getHeight(), width = rectangle.getWidth();
        double east = rectangle.getEast(), west = rectangle.getWest();
        double south = rectangle.getSouth(), north = rectangle.getNorth();
        Cells_detail cells[] = new Cells_detail[height * width];
        
        for (int i = 0; i < height * width; i++) {
            cells[i] = new Cells_detail();
        }
        
        count = 0;
        int x, y, tempMarkerCount, postion, i;
        double speed;
        Marker marker;
        
        for (i = markerIndex; i < markers.length; i++) {
            marker = markers[i];
            x = CalculateModule.whereX(east, west, marker.getLng(), width);
            y = CalculateModule.whereY(south, north, marker.getLat(), height);
            postion = y * width + x;
            if (marker.getRecord_time().compareTo(mileStones[mileStonesIndex]) >= 0) break;
            if(cells[postion].getMarkerCount() == 0) {
                ++count;
                cells[postion].setX_axis(x);
                cells[postion].setY_axis(y);
                cells[postion].setAlgorithm(ALGORITHM);
                speed = marker.getSpeed();
                cells[postion].setAvg_speed(speed);
                cells[postion].setId_cell(id);
                cells[postion].increaseMarkerCount();
                cells[postion].setStart_time(mileStones[mileStonesIndex-1]);
                cells[postion].setEnd_time(mileStones[mileStonesIndex]);
            } else {
                tempMarkerCount = cells[postion].getMarkerCount();
                speed = cells[postion].getAvg_speed();
                speed = ( marker.getSpeed() + speed * tempMarkerCount ) / ( tempMarkerCount + 1 );
                cells[postion].increaseMarkerCount();
                cells[postion].setAvg_speed(speed);
            }
        }
        JSONObject result[] = new JSONObject[count];
        for (i = 0; i < height*width; i++) {
            if(cells[i].getMarkerCount() > 0) {
                speed = cells[i].getAvg_speed();
                cells[i].setIndicator(speed);
                cells[i].setColor(CalculateModule.matchColor(speed));
                result[--count] = cells[i].cellToJSONObject(cells[i]);
            }
        }
        return result;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Calculation execute = new Calculation(1);
        Cells_detail cellsDetail = new Cells_detail(cellsUrl);
        Marker marker = new Marker(markerUrl, Calculation.START_TIME, Calculation.END_TIME);
        Rectangle rectangle = new Rectangle(rectangleUrl);
        JSONObject data;
        try {
            Rectangle[] rectangles = rectangle.getRectangles();
            Marker[] markers = marker.getMarkers();
            JSONArray diget;
            for (int i = 1; i < mileStones.length; i++) {
                diget = new JSONArray();
                for (Rectangle aRectangle:rectangles) {
                    JSONObject cells[] = Calculation.fitCellsByMileStones(markers, aRectangle, i);
                    for (JSONObject cell : cells) {
                        diget.put(cell);
                    }
                }
                data = new JSONObject();
                data.put("data", diget);
                //System.out.println(data);
                cellsDetail.postJsonToApi(data);
            }
        } catch (JSONException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
