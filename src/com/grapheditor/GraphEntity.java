package com.grapheditor;

import agape.tools.Operations;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;
import java.util.List;

public class GraphEntity {
    Graph<Entities.Node, Entities.Link> graph;
    VisualizationViewer<Entities.Node,Entities.Link> vv;
    Layout<Entities.Node, Entities.Link> layout;
    private int linkCounter;
    private int nodeCounter;
    Factory<Entities.Node> nodeFactory;
    Factory<Entities.Link> linkFactory;
    ArrayList<Entities.Node> path = new ArrayList<>();
    ArrayList<ArrayList<Entities.Node>> pathes;
    ArrayList<ArrayList<Entities.Node>> tasks;

    public GraphEntity(boolean directivity){
        if (directivity){
            graph = new MyDirectedSparseGraph();

        }else{
            graph = new SparseGraph<>();
        }

        nodeFactory = new Factory<Entities.Node>() {

            @Override
            public Entities.Node create() {
                return new Entities().new Node(1.0, nodeCounter++);
            }
        };
        linkFactory = new Factory<Entities.Link>() {
            @Override
            public Entities.Link create() {
                return new Entities().new Link(1.0, linkCounter++);
            }
        };

        layout = new StaticLayout<>(graph);
        layout.setSize(new Dimension(500, 500));
        if (directivity){
            setTaskVv();
        }else{
            setSystemVv();
        }
    }
    Transformer<Entities.Node, Paint> paintTransformer = new Transformer<Entities.Node, Paint>() {
        @Override
        public Paint transform(Entities.Node node) {
            return Color.LIGHT_GRAY;
        }
    };

    public void setLinkCounter(int linkCounter) {
        this.linkCounter = linkCounter;
    }

    public void setNodeCounter(int nodeCounter) {
        this.nodeCounter = nodeCounter;
    }
    public void decLinkCounter(){
        linkCounter--;
    }
    public void decNodeCounter(){
        nodeCounter--;
    }

    private void setTaskVv(){
        vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(550, 550)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        vv.getRenderContext().setVertexFillPaintTransformer(paintTransformer);
        Transformer<Entities.Node, Shape> shapeTaskTransformer = new Transformer<Entities.Node, Shape>() {
            @Override
            public Shape transform(Entities.Node node) {
                Ellipse2D circle = new Ellipse2D.Double(-30, -30, 60, 60);
                return circle;
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(shapeTaskTransformer);
    }
    private  void setSystemVv(){
        vv = new VisualizationViewer<>(layout);
        vv.setPreferredSize(new Dimension(550, 550)); //Sets the viewing area size
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<>());
        vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
        Transformer<Entities.Node, Shape> shapeSystemTransformer = new Transformer<Entities.Node, Shape>() {
            @Override
            public Shape transform(Entities.Node node) {
                Rectangle2D rectangle = new Rectangle2D.Double(-30, -30, 60, 60);
                return  rectangle;
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(shapeSystemTransformer);
        vv.getRenderContext().setVertexFillPaintTransformer(paintTransformer);
    }
    public void saveGraph(File file) {
        try{
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.toGraphSerializable());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public GraphSerializable toGraphSerializable(){
        GraphSerializable result = new GraphSerializable();
        ArrayList<Entities.Node> nodes =  new ArrayList<>(graph.getVertices());
        HashMap<Entities.Node, Point2D> points = new HashMap<>();
        for (Entities.Node node : nodes){
            points.put(node, layout.transform(node));
        }
        result.setLinkCounter(linkCounter);
        result.setNodeCounter(nodeCounter);
        result.setGraph(graph);
        result.setPoints(points);
        return result;
    }
    public boolean isConnected(){
        List<Entities.Link> path = null;
        DijkstraShortestPath<Entities.Node, Entities.Link> alg  =
                new DijkstraShortestPath<>(graph);
        ArrayList<Entities.Node> arr = new ArrayList<>(graph.getVertices());
        for (int j = 0; j < arr.size() - 1; j++) {
            path = alg.getPath(arr.get(0), arr.get(j+1));
            if (path.size() == 0){
                return false;
            }
        }
        return true;
    }
    public  boolean isCycled(){
        Operations operations = new Operations();
        return !operations.isAcyclic(graph);
    }
    public ArrayList<resultVertex> labTwo(){
        ArrayList<resultVertex> result = new ArrayList<>();
        ArrayList<Entities.Node> maxPathByVCount = new ArrayList<>();
        ArrayList<Entities.Node> maxPathByTime = new ArrayList<>();
        ArrayList<ArrayList<Entities.Node>> tmp = new ArrayList<>();
        ArrayList<ArrayList<Entities.Node>> tmp1 = new ArrayList<>();
        ArrayList<Entities.Node> ends = new ArrayList<>();
        Iterator<Entities.Node> nodes = graph.getVertices().iterator();
        while (nodes.hasNext()){
            Entities.Node node = nodes.next();
            if (graph.getSuccessorCount(node) == 0 ){
                ends.add(node);
            }
        }
        Iterator<Entities.Node> iterator = graph.getVertices().iterator();
        while (iterator.hasNext()){
            Entities.Node node = iterator.next();
            for (int i = 0; i < ends.size(); i++){
                tmp.add(getCriticalPathByVertexcount(node, ends.get(i)));
                tmp1.add(getCriticalPathByTime(node, ends.get(i)));
            }
            maxPathByVCount = getMaxPathByVertexCount(tmp);
            maxPathByTime = getMaxPathByTime(tmp1);
            tmp = new ArrayList<>();
            tmp1 = new ArrayList<>();
            result.add(new resultVertex(node, getNormalizedVertexRate(maxPathByTime, maxPathByVCount)));
        }
        Collections.sort(result);
        return  result;
    }
    public ArrayList<Entities.Node> labThree(){
        ArrayList<Entities.Node> result = new ArrayList<>();
        tasks = new ArrayList<>();
        ArrayList<ArrayList<Entities.Node>> tmp = new ArrayList<>();
        ArrayList<Entities.Node> ends = new ArrayList<>();
        Iterator<Entities.Node> nodes = graph.getVertices().iterator();
        while (nodes.hasNext()){
            Entities.Node node = nodes.next();
            if (graph.getSuccessorCount(node) == 0 ){
                ends.add(node);
            }
        }
        Iterator<Entities.Node> iterator = graph.getVertices().iterator();
        while (iterator.hasNext()){
            Entities.Node node = iterator.next();
            for (int i = 0; i < ends.size(); i++){
                tmp.add(getCriticalPathByVertexcount(node, ends.get(i)));
            }
            tasks.add(getMaxPathByVertexCount(tmp));
            tmp = new ArrayList<>();
        }
        for (int i = 0; i < tasks.size(); i++){
            if (tasks.get(i).size() > result.size()){
                result = tasks.get(i);
            }
        }
        removeCritical();
        for (int i = 0; i < tasks.size(); i++){
            result.add(tasks.get(i).get(0));
        }
        return  result;
    }
    private void sortDecTasks(){
        ArrayList<Entities.Node> tmp = new ArrayList<>();
        for (int i = 0; i < tasks.size() - 1; i ++){
            for (int j = i + 1; j < tasks.size(); j ++){
                if(tasks.get(i).size() < tasks.get(j).size() ){
                    tmp = tasks.get(i);
                    tasks.set(i , tasks.get(j));
                    tasks.set(j, tmp);
                }
            }
        }
    }
    private void sortIncByTimeTasks(){
        ArrayList<Entities.Node> tmp = new ArrayList<>();
        for (int i = 0; i < tasks.size() - 1; i ++){
            for (int j = i + 1; j < tasks.size(); j ++){
                if(getPathTime(tasks.get(i)) > getPathTime(tasks.get(j)) ){
                    tmp = tasks.get(i);
                    tasks.set(i , tasks.get(j));
                    tasks.set(j, tmp);
                }
            }
        }
    }
    private  void removeCritical(){
        sortDecTasks();
        ArrayList<Entities.Node> critical = tasks.get(0);
        tasks.remove(0);
        for (int i = 0; i < tasks.size();i++){
            if(critical.contains(tasks.get(i).get(0))){
                tasks.remove(i);
                i--;
            }
        }
    }
    public ArrayList<Entities.Node> getMaxPathByVertexCount(ArrayList<ArrayList<Entities.Node>> pathes){
        ArrayList<Entities.Node> result = new ArrayList<>();
        if (result.size() == 1){
            return pathes.get(0);
        }
        for (int i = 0; i < pathes.size(); i++){
            if (result.size() < pathes.get(i).size()){
                result = pathes.get(i);
            }
        }
        return result;
    }
    public ArrayList<Entities.Node> getCriticalPathByVertexcount(Entities.Node start, Entities.Node finish){
        ArrayList<Entities.Node> result = new ArrayList<>();
        pathes = new ArrayList<>();
        checkVertex(start, finish);
        for (int i = 0; i < pathes.size(); i++){
            if (pathes.get(i).size() > result.size()){
                result = pathes.get(i);
            }
        }
        return result;
    }
    public ArrayList<Entities.Node> labFour(){
        ArrayList<Entities.Node> result = new ArrayList<>();
        ArrayList<ArrayList<Entities.Node>> tmp = new ArrayList<>();
        tasks = new ArrayList<>();
        ArrayList<Entities.Node> begins = new ArrayList<>();
        Iterator<Entities.Node> iterator = graph.getVertices().iterator();
        while (iterator.hasNext()){
            Entities.Node node = iterator.next();
            if(graph.getPredecessorCount(node) == 0){
                begins.add(node);
            }
        }
        Iterator<Entities.Node> iterator1 = graph.getVertices().iterator();
        while (iterator1.hasNext()){
            Entities.Node node = iterator1.next();
            for (int i = 0; i < begins.size(); i++){
                tmp.add(getCriticalPathByTime(begins.get(i), node));

            }
            tasks.add(getMaxPathByTime(tmp));
            tmp = new ArrayList<>();
        }
        sortIncByTimeTasks();
        for (int i = 0; i < tasks.size(); i++){
            result.add(tasks.get(i).get(tasks.get(i).size() - 1));
        }
        return result;
    }
    private ArrayList<Entities.Node> getMaxPathByTime(ArrayList<ArrayList<Entities.Node>> pathes){
        ArrayList<Entities.Node> result = new ArrayList<>();
        for (int i = 0; i < pathes.size(); i++){
            if (getPathTime(pathes.get(i)) > getPathTime(result)){
                result = pathes.get(i);
            }
        }
        return result;
    }
    private double getNormalizedVertexRate(ArrayList<Entities.Node> pathByTime,
                                           ArrayList<Entities.Node> pathByVCount){
        double result;
        double rate = 0.0;
        double general = getGeneralTime() + graph.getVertexCount();
        rate = rate + getPathTime(pathByTime);
        rate = rate + pathByVCount.size();
        result = rate/general;
        return result;
    }
    private double getGeneralTime(){
        double result = 0.0;
        ArrayList<Entities.Link> links = new ArrayList<>(graph.getEdges());
        for (Entities.Link link: links){
            result = result + link.getWight();
        }
        return result;
    }
    public ArrayList<Entities.Node> getCriticalPathByTime(Entities.Node start, Entities.Node finish){
        ArrayList<Entities.Node> result = new ArrayList<>();
        pathes = new ArrayList<>();
        checkVertex(start, finish);

        for (int i = 0; i < pathes.size(); i++){
            if (getPathTime(pathes.get(i)) > getPathTime(result)){
                result = pathes.get(i);
            }
        }
        return result;
    }
    private double getPathTime(ArrayList<Entities.Node> path){
        if (path.size() == 0){
            return -1.0;
        }
        ArrayList<Entities.Link> edges = getEdgesPath(path);
        double result = 0.0;
        for (int i = 0; i < edges.size(); i++){
            result = result + edges.get(i).getWight();
        }
        return  result;
    }
    private ArrayList<Entities.Link> getEdgesPath(ArrayList<Entities.Node> path){
        ArrayList<Entities.Link> result = new ArrayList<>();
        for (int i = 0; i < path.size() - 1; i++){
            result.add(graph.findEdge(path.get(i), path.get(i + 1)));
        }
        return result;
    }
    public void checkVertex(Entities.Node first, Entities.Node second){
        path.add(first);
        if(first == second) {
            pathes.add(path);
            path = new ArrayList<>(path);
            path.remove(path.size() - 1);
            return;
        }
        if (graph.getSuccessorCount(first) == 0){
            path.remove(path.size() - 1);
            return;
        }

        Iterator<Entities.Node> iter = graph.getSuccessors(first).iterator();
        while (iter.hasNext()){
            Entities.Node n = iter.next();

            checkVertex(n, second);
        }
        path.remove(path.size() - 1);

    }
}
