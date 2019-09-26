package com.example.testfirebase.Helper;

import com.example.testfirebase.GetterSetterClass.Edge_cal;
import com.example.testfirebase.GetterSetterClass.Graph;
import com.example.testfirebase.GetterSetterClass.Vertex_cal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm {
    private final List<Vertex_cal> nodes;
    private final List<Edge_cal> edges;
    private Set<Vertex_cal> settledNodes;
    private Set<Vertex_cal> unSettledNodes;
    private Map<Vertex_cal, Vertex_cal> predecessors;
    private Map<Vertex_cal, Integer> distance;

    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<Vertex_cal>(graph.getVertexes());
        this.edges = new ArrayList<Edge_cal>(graph.getEdges());
    }
    public void execute(Vertex_cal source) {
        settledNodes = new HashSet<Vertex_cal>();
        unSettledNodes = new HashSet<Vertex_cal>();
        distance = new HashMap<Vertex_cal, Integer>();
        predecessors = new HashMap<Vertex_cal, Vertex_cal>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            Vertex_cal node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }
    private void findMinimalDistances(Vertex_cal node) {
        List<Vertex_cal> adjacentNodes = getNeighbors(node);
        for (Vertex_cal target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }
    }
    private int getDistance(Vertex_cal node, Vertex_cal target) {
        for (Edge_cal edge : edges) {
            if (edge.getEdge_cal_source().equals(node)
                    && edge.getEdge_cal_destination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }
    private List<Vertex_cal> getNeighbors(Vertex_cal node) {
        List<Vertex_cal> neighbors = new ArrayList<Vertex_cal>();
        for (Edge_cal edge : edges) {
            if (edge.getEdge_cal_source().equals(node)
                    && !isSettled(edge.getEdge_cal_destination())) {
                neighbors.add(edge.getEdge_cal_destination());
            }
        }
        return neighbors;
    }
    private Vertex_cal getMinimum(Set<Vertex_cal> vertexes) {
        Vertex_cal minimum = null;
        for (Vertex_cal vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }
    private boolean isSettled(Vertex_cal vertex) {
        return settledNodes.contains(vertex);
    }
    private int getShortestDistance(Vertex_cal destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }
    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Vertex_cal> getPath(Vertex_cal target) {
        LinkedList<Vertex_cal> path = new LinkedList<Vertex_cal>();
        Vertex_cal step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}
