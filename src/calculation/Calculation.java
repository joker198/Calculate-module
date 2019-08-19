package calculation;

import static calculation.CalculateModule.DATE_FORMAT;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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
    public static String markerUrl = "http://68.183.238.65:5000/api/data/markers";
    public static String rectangleUrl = "http://68.183.238.65:5000/api/data/rectangles";
    public static String cellsUrl = "http://68.183.238.65:5000/api/data/cells-detail";
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
     */
    public Calculation(int algorithm)
    {
        try {
            mileStones = CalculateModule.initSplitDuration(DURATION, Interval.DEFAULT.value);
        } catch (ParseException ex) {
            Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
        }
        START_TIME = mileStones[0];
        END_TIME = mileStones[mileStones.length-1];
        ALGORITHM = algorithm;
    }

    public Calculation(int algorithm, String time)
    {
        try {
           mileStones = CalculateModule.splitDuration(time, DURATION,Interval.DEFAULT.value);
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
     * @param modal
     * @return 
     * @throws org.json.JSONException 
     */
    public static JSONObject[] fitCellsByMileStones(Marker[] markers, Rectangle rectangle, int mileStonesIndex, JSONArray modal) throws JSONException{
        int id = rectangle.getId();
        int height = rectangle.getHeight(), width = rectangle.getWidth();
        int gridSize = height * width;
        double east = rectangle.getEast(), west = rectangle.getWest();
        double south = rectangle.getSouth(), north = rectangle.getNorth();
        Cells_detail cells[] = new Cells_detail[gridSize];
        RecordUsers[] getIn = new RecordUsers[gridSize];
        
        for (int i = 0; i < gridSize; i++) {
            cells[i] = new Cells_detail();
            getIn[i] = new RecordUsers(modal);
        }
        
        count = 0;
        int x, y, postion, i;
        double[] speedArray = new double[gridSize];
        Marker marker;
        
        for (i = markerIndex; i < markers.length; i++) {
            marker = markers[i];
            x = CalculateModule.whereX(east, west, marker.getLng(), width);
            y = CalculateModule.whereY(south, north, marker.getLat(), height);
            postion = y * width + x;
            if (x < 0 || y < 0) continue;
            if (marker.getRecord_time().compareTo(mileStones[mileStonesIndex]) >= 0) break;
            speedArray[postion] += marker.getSpeed();
            getIn[postion].turnStatus(marker.getRecord_user());
            if(cells[postion].getMarkerCount() == 0) {
                ++count;
                cells[postion].setX_axis(x);
                cells[postion].setY_axis(y);
                cells[postion].setAlgorithm(ALGORITHM);
                cells[postion].setId_cell(id);
                cells[postion].increaseMarkerCount();
                cells[postion].setStart_time(mileStones[mileStonesIndex-1]);
                cells[postion].setEnd_time(mileStones[mileStonesIndex]);
            } else {
                cells[postion].increaseMarkerCount();
            }
        }
        JSONObject result[] = new JSONObject[count];
        double speed;
        for (i = 0; i < gridSize; i++) {
            if(cells[i].getMarkerCount() > 0) {
                speed = speedArray[i] / cells[i].getMarkerCount();
                cells[i].setAvg_speed(speed);
                cells[i].setIndicator(speed);
                cells[i].setMarkerCount(getIn[i].recordedUserCount());
                cells[i].setColor(CalculateModule.matchColor(speed));
                result[--count] = cells[i].cellToJSONObject(cells[i]);
                
            }
        }
        return result;
    }
    
    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws InterruptedException, ParseException {
//        Calendar calendar = Calendar.getInstance();//
//        Date startTime = DATE_FORMAT.parse("2019-08-16 23:00:00");//
//        calendar.setTime(startTime);//
//        for (int  index= 1; index <= 30; index++) {//
//            if(index % 9 == 0) Thread.sleep(30000);//
//            else Thread.sleep(1000);
//            calendar.add(Calendar.MINUTE, DURATION);//
//            Calculation execute = new Calculation(1, DATE_FORMAT.format(calendar.getTime()));//
//            Cells_detail cellsDetail = new Cells_detail(cellsUrl);
//            Marker marker = new Marker(markerUrl, Calculation.START_TIME, Calculation.END_TIME);
//            Rectangle rectangle = new Rectangle(rectangleUrl);
//            JSONObject data;
//            try {
//                Rectangle[] rectangles = rectangle.getRectangles();
//                Marker[] markers = marker.getMarkers();
//                if (markers.length == 0 || rectangles.length == 0) continue;
//                JSONArray diget;
//                for (int i = 1; i < mileStones.length; i++) {
//                    diget = new JSONArray();
//                    for (Rectangle aRectangle:rectangles) {
//                        JSONObject cells[] = Calculation.fitCellsByMileStones(markers, aRectangle, i, new JSONArray(marker.getNumOfRecordUser()));
//                        for (JSONObject cell : cells) {
//                            diget.put(cell);
//                        }
//                    }
//                    data = new JSONObject();
//                    data.put("data", diget);
//                    //System.out.println(data);
//                    cellsDetail.postJsonToApi(data);
//                }
//            } catch (JSONException ex) {
//                Logger.getLogger(Calculation.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }//
//        System.out.println(DATE_FORMAT.format(calendar.getTime()));//

        Calculation execute = new Calculation(1);//
        Cells_detail cellsDetail = new Cells_detail(cellsUrl);
        Marker marker = new Marker(markerUrl, Calculation.START_TIME, Calculation.END_TIME);
        Rectangle rectangle = new Rectangle(rectangleUrl);
        JSONObject data;
        try {
            Rectangle[] rectangles = rectangle.getRectangles();
            Marker[] markers = marker.getMarkers();
            if (markers.length == 0 || rectangles.length == 0) return;
            JSONArray diget;
            for (int i = 1; i < mileStones.length; i++) {
                diget = new JSONArray();
                for (Rectangle aRectangle:rectangles) {
                    JSONObject cells[] = Calculation.fitCellsByMileStones(markers, aRectangle, i, new JSONArray(marker.getNumOfRecordUser()));
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
