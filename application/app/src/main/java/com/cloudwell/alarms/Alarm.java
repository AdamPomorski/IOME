package com.cloudwell.alarms;

public class Alarm {
    private int id;
    private AlarmTypes t;
    private String value;

    public Alarm(int id, AlarmTypes t, String value) {
        this.id = id;
        this.t = t;
        this.value = value;
    }

    public AlarmTypes getT() {
        return t;
    }

    public void setT(AlarmTypes t) {
        this.t = t;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
