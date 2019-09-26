package com.example.testfirebase.GetterSetterClass;

public class Location {
    private String location_name,location_des,image_path,type,location_id;
    private String lat,longti;
    public Location(){}
    public Location(String location_name, String location_des, String image_path, String type, String location_id, String lat, String longti) {
        this.location_name = location_name;
        this.location_des = location_des;
        this.image_path = image_path;
        this.type = type;
        this.location_id = location_id;
        this.lat = lat;
        this.longti = longti;
    }
    public String getLocation_name() {
        return location_name;
    }
    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }
    public String getLocation_des() {
        return location_des;
    }
    public void setLocation_des(String location_des) {
        this.location_des = location_des;
    }
    public String getImage_path() {
        return image_path;
    }
    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLocation_id() {
        return location_id;
    }
    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getLongti() {
        return longti;
    }
    public void setLongti(String longti) {
        this.longti = longti;
    }
    @Override
    public String toString() { return location_name; }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Location){ Location l = (Location ) obj;
            if(l.getLocation_name().equals(location_name) && l.getLocation_id()==location_id ) return true;
        }
        return false;
    }
}
