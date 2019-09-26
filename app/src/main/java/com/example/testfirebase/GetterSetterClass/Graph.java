package com.example.testfirebase.GetterSetterClass;

import java.util.List;

public class Graph {
    private final List<Vertex_cal> vertexes;
    private final List<Edge_cal> edges;

    public Graph(List<Vertex_cal> vertexes, List<Edge_cal> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public List<Vertex_cal> getVertexes() {
        return vertexes;
    }

    public List<Edge_cal> getEdges() {
        return edges;
    }
}
