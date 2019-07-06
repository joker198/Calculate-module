/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import org.json.JSONArray;
/**
 *
 * @author phamt
 */
public interface NeededTool {
    /**
     * Fetching data from JSON array to String array
     * @param url
     * @return JSONArray
     */
    public JSONArray getJsonFromAPI(String url);
}
