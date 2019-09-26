package com.example.testfirebase.GetterSetterClass;

public class Toilet {
    private String toilet_id,floor_id,toilet_name,toiley_map_pic;
    public Toilet(){}
    public Toilet(String toilet_id, String floor_id, String toilet_name, String toiley_map_pic) {
        this.toilet_id = toilet_id;
        this.floor_id = floor_id;
        this.toilet_name = toilet_name;
        this.toiley_map_pic = toiley_map_pic;
    }
    public String getToilet_id() {
        return toilet_id;
    }
    public void setToilet_id(String toilet_id) {
        this.toilet_id = toilet_id;
    }
    public String getFloor_id() {
        return floor_id;
    }
    public void setFloor_id(String floor_id) {
        this.floor_id = floor_id;
    }
    public String getToilet_name() {
        return toilet_name;
    }
    public void setToilet_name(String toilet_name) {
        this.toilet_name = toilet_name;
    }
    public String getToiley_map_pic() {
        return toiley_map_pic;
    }
    public void setToiley_map_pic(String toiley_map_pic) {
        this.toiley_map_pic = toiley_map_pic;
    }
    @Override
    public String toString() { return toilet_name; }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Toilet){ Toilet l = (Toilet ) obj;
            if(l.getToilet_name().equals(toilet_name) && l.getToilet_id()==toilet_id ) return true;
        }
        return false;
    }
}
