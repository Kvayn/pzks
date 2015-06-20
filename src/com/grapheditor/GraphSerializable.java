package com.grapheditor;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;

public class GraphSerializable implements Serializable {
    private Graph<Entities.Node, Entities.Link> graph;
    private HashMap<Entities.Node, Point2D> points;
    private int linkCounter;
    private int nodeCounter;

    public void setGraph(Graph<Entities.Node, Entities.Link> graph) {
        this.graph = graph;
    }

    public void setPoints(HashMap<Entities.Node, Point2D> points) {
        this.points = points;
    }
    public String toString(){
        String result = graph.toString() + points.toString();
        return result;
    }
    public GraphEntity getGraphEntity(){
        GraphEntity graphEntity = new GraphEntity(true);
        List<Entities.Node> nodes = new ArrayList<>(graph.getVertices());
        graphEntity.graph = (DirectedSparseMultigraph<Entities.Node, Entities.Link>) graph;
        graphEntity.layout.setGraph(graph);

        for (int i = 0; i < nodes.size(); i++) {
            graphEntity.layout.setLocation(nodes.get(i), points.get(i));
        }
        System.out.println("   get Graph Ebtity   " + graphEntity.layout.transform(nodes.get(0)));
        return graphEntity;
    }
    public GraphSerializable loadGraph(File file){
        GraphSerializable result = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (GraphSerializable) ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public HashMap<Entities.Node, Point2D> getPoints() {
        return points;
    }

    public Graph<Entities.Node, Entities.Link> getGraph() {
        return graph;
    }

    public void setLinkCounter(int linkCounter) {
        this.linkCounter = linkCounter;
    }

    public void setNodeCounter(int nodeCounter) {
        this.nodeCounter = nodeCounter;
    }

    public int getLinkCounter() {
        return linkCounter;
    }

    public int getNodeCounter() {
        return nodeCounter;
    }
}
