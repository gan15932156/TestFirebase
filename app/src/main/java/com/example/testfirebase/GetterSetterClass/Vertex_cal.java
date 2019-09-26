package com.example.testfirebase.GetterSetterClass;

public class Vertex_cal {
    final private String vertex_id;
    final private String vertex_name;
    final private String vertex_lat;
    final private String vertex_long;
    final private String vertex_number;
    final private String vertex_type;
    final private String vertex_building_name;

    public Vertex_cal(String vertex_id, String vertex_name, String vertex_lat, String vertex_long, String vertex_number, String vertex_type, String vertex_building_name) {
        this.vertex_id = vertex_id;
        this.vertex_name = vertex_name;
        this.vertex_lat = vertex_lat;
        this.vertex_long = vertex_long;
        this.vertex_number = vertex_number;
        this.vertex_type = vertex_type;
        this.vertex_building_name = vertex_building_name;
    }

    public String getVertex_id() {
        return vertex_id;
    }

    public String getVertex_name() {
        return vertex_name;
    }

    public String getVertex_lat() {
        return vertex_lat;
    }

    public String getVertex_long() {
        return vertex_long;
    }

    public String getVertex_number() {
        return vertex_number;
    }

    public String getVertex_type() {
        return vertex_type;
    }

    public String getVertex_building_name() {
        return vertex_building_name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((vertex_id == null) ? 0 : vertex_id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vertex_cal other = (Vertex_cal) obj;
        if (vertex_id == null) {
            if (other.vertex_id != null)
                return false;
        } else if (!vertex_id.equals(other.vertex_id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return vertex_name;
    }
}
