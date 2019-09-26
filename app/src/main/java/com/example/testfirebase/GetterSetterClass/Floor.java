package com.example.testfirebase.GetterSetterClass;

public class Floor {
    private String floor_id,location_id,floor_name;
    public Floor(){}
    public Floor(String floor_id, String location_id, String floor_name) {
        this.floor_id = floor_id;
        this.location_id = location_id;
        this.floor_name = floor_name;
    }
    public String getFloor_id() {
        return floor_id;
    }
    public void setFloor_id(String floor_id) {
        this.floor_id = floor_id;
    }
    public String getLocation_id() {
        return location_id;
    }
    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }
    public String getFloor_name() {
        return floor_name;
    }
    public void setFloor_name(String floor_name) {
        this.floor_name = floor_name;
    }
    @Override
    public String toString() { return floor_name; }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Floor){ Floor l = (Floor ) obj;
            if(l.getFloor_name().equals(floor_name) && l.getFloor_id()==floor_id ) return true;
        }
        return false;
    }
}
