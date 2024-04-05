package com.cloudwell.alarms;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class CheckAlarmMessage {
    public CheckAlarmMessage() {
    }
    AlarmResponse checkMessage(String input){
        int id = 0;
        double current_value = 0, alarm_value = 0;
        String alarm_type = "unknown";
        try {
            JSONObject jsonObject = new JSONObject(input);
            id = jsonObject.getInt("property_id");
            alarm_type = jsonObject.getString("alarm_type");
            current_value = jsonObject.getDouble("current_val");
            alarm_value = jsonObject.getDouble("alarm_val");

        }catch (JSONException e) {
            e.printStackTrace();
        }
        return new AlarmResponse(id,current_value,alarm_value,alarm_type);
    }
}
