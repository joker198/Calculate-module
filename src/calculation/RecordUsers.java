/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author joker
 */
public class RecordUsers {
    private boolean[] value;
    private long[] index;

    public RecordUsers() {
    }
    
    public RecordUsers(JSONArray userIdArray) throws JSONException {
        index = new long[userIdArray.length()];
        value = new boolean[userIdArray.length()];
        JSONObject aRecord;
        for (int i = 0; i < userIdArray.length() ; i++) {
            aRecord = userIdArray.getJSONObject(i);
            index[i] = aRecord.getLong("record_user");
            value[i] = false;
        }
    }
    
    public String getValue()
    {
        return Arrays.toString(value);
    }
    public void turnStatus(long id)
    {
        for (int i = 0; i < value.length; i++) {
            if (!value[i] && index[i] == id) {
                this.value[i] = true;
                return ;
            }
        }
    }
    
    public int recordedUserCount()
    {
        int count = 0;
        for (int i = 0; i < value.length; i++) {
            if (value[i]) ++count;
        }
        return count;
    }
}
