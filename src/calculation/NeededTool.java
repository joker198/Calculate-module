package calculation;

import org.json.JSONArray;
/**
 *
 * @author joker
 */
public interface NeededTool {
    /**
     * Fetching data from JSON array to String array
     * @param url
     * @return JSONArray
     */
    public JSONArray getJsonFromAPI(String url);
}
