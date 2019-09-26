package com.example.testfirebase.GetterSetterClass;

public class Edge {
    private final String edge_id;
    private final String edge_source;
    private final String edge_destination;
    private final String edge_distance;

    public Edge(String edge_id,String edge_source, String edge_destination, String edge_distance) {
        this.edge_id = edge_id;
        this.edge_source = edge_source;
        this.edge_destination = edge_destination;
        this.edge_distance = edge_distance;
    }

    public String getEdge_id() {
        return edge_id;
    }

    public String getEdge_source() {
        return edge_source;
    }

    public String getEdge_destination() {
        return edge_destination;
    }

    public String getEdge_distance() {
        return edge_distance;
    }

    @Override
    public String toString() {
        return edge_source + " " + edge_destination;
    }
}
