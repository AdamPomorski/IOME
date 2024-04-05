package com.cloudwell.alarms;

public class AlarmResponse {
    private int id;
    private double current_val;
    private  double alarm_val;
    private String alarm_type;

    public AlarmResponse(int id, double current_val, double alarm_val, String alarm_type) {
        this.id = id;
        this.current_val = current_val;
        this.alarm_val = alarm_val;
        this.alarm_type = alarm_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlarm_type() {
        return alarm_type;
    }

    public void setAlarm_type(String alarm_type) {
        this.alarm_type = alarm_type;
    }

    public double getCurrent_val() {
        return current_val;
    }

    public void setCurrent_val(double current_val) {
        this.current_val = current_val;
    }

    public double getAlarm_val() {
        return alarm_val;
    }

    public void setAlarm_val(double alarm_val) {
        this.alarm_val = alarm_val;
    }
}
