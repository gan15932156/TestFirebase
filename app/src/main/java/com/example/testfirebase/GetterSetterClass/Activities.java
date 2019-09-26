package com.example.testfirebase.GetterSetterClass;

public class Activities {
    private String ac_id,ac_name,ac_place_name,ac_des,ac_image;
    private String lat,mlong;

    public Activities(String ac_id, String ac_name, String ac_place_name, String ac_des, String ac_image, String lat, String mlong) {
        this.ac_id = ac_id;
        this.ac_name = ac_name;
        this.ac_place_name = ac_place_name;
        this.ac_des = ac_des;
        this.ac_image = ac_image;
        this.lat = lat;
        this.mlong = mlong;
    }
    public Activities(){}

    public String getAc_id() {
        return ac_id;
    }

    public void setAc_id(String ac_id) {
        this.ac_id = ac_id;
    }

    public String getAc_name() {
        return ac_name;
    }

    public void setAc_name(String ac_name) {
        this.ac_name = ac_name;
    }

    public String getAc_place_name() {
        return ac_place_name;
    }

    public void setAc_place_name(String ac_place_name) {
        this.ac_place_name = ac_place_name;
    }

    public String getAc_des() {
        return ac_des;
    }

    public void setAc_des(String ac_des) {
        this.ac_des = ac_des;
    }

    public String getAc_image() {
        return ac_image;
    }

    public void setAc_image(String ac_image) {
        this.ac_image = ac_image;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getMlong() {
        return mlong;
    }

    public void setMlong(String mlong) {
        this.mlong = mlong;
    }
    @Override
    public String toString() { return ac_name; }
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Activities){ Activities ac = (Activities ) obj;
            if(ac.getAc_name().equals(ac_name) && ac.getAc_id()==ac_id ) return true;
        }
        return false;
    }
}
