package com.grapheditor;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingGraphMousePlugin;
import org.apache.commons.collections15.Factory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class MyEditingGraphMousePlugin<V,E> extends EditingGraphMousePlugin {
    protected JPopupMenu popup = new JPopupMenu();
    public MyEditingGraphMousePlugin(Factory<V> vertexFactory, Factory<E> edgeFactory) {
        super(vertexFactory, edgeFactory);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(this.checkModifiers(e)) {
            VisualizationViewer vv = (VisualizationViewer)e.getSource();
            Point p = e.getPoint();
            GraphElementAccessor pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                Graph graph = vv.getModel().getGraphLayout().getGraph();
                if(graph instanceof DirectedGraph) {
                    this.edgeIsDirected = EdgeType.DIRECTED;
                } else {
                    this.edgeIsDirected = EdgeType.UNDIRECTED;
                }

                Object vertex = pickSupport.getVertex(vv.getModel().getGraphLayout(), p.getX(), p.getY());
                if(vertex != null) {
                    this.startVertex = vertex;
                    this.down = e.getPoint();
                    this.transformEdgeShape(this.down, this.down);
                    vv.addPostRenderPaintable(this.edgePaintable);
                    if((e.getModifiers() & 1) != 0 && !(vv.getModel().getGraphLayout().getGraph() instanceof UndirectedGraph)) {
                        this.edgeIsDirected = EdgeType.DIRECTED;
                    }

                    if(this.edgeIsDirected == EdgeType.DIRECTED) {
                        this.transformArrowShape(this.down, e.getPoint());
                        vv.addPostRenderPaintable(this.arrowPaintable);
                    }
                } else {
                    Entities.Node newVertex = (Entities.Node) this.vertexFactory.create();
                    String s = (String)JOptionPane.showInputDialog(
                            vv, "Enter the weight", "Customized dialog", JOptionPane.PLAIN_MESSAGE, null, null, null);
                    if (s != null){
                        try{
                            double w = Double.parseDouble(s);
                            newVertex.setWeight(w);
                            Layout layout = vv.getModel().getGraphLayout();
                            graph.addVertex(newVertex);
                            layout.setLocation(newVertex,
                                    vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
                        }catch (Exception e2){
                            JOptionPane.showMessageDialog(null, "Wrong input!");
                        }

                    }

                }
            }

            vv.repaint();
        }
    }
    private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float)down.getX();
        float y1 = (float)down.getY();
        float x2 = (float)out.getX();
        float y2 = (float)out.getY();
        AffineTransform xform = AffineTransform.getTranslateInstance((double)x1, (double)y1);
        float dx = x2 - x1;
        float dy = y2 - y1;
        float thetaRadians = (float)Math.atan2((double)dy, (double)dx);
        xform.rotate((double)thetaRadians);
        float dist = (float)Math.sqrt((double)(dx * dx + dy * dy));
        xform.scale((double)dist / this.rawEdge.getBounds().getWidth(), 1.0D);
        this.edgeShape = xform.createTransformedShape(this.rawEdge);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(this.checkModifiers(e)) {
            VisualizationViewer vv = (VisualizationViewer)e.getSource();
            Point p = e.getPoint();
            Layout layout = vv.getModel().getGraphLayout();
            GraphElementAccessor pickSupport = vv.getPickSupport();
            if(pickSupport != null) {
                Object vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
                if(vertex != null && this.startVertex != null && this.startVertex != vertex) {
                    Graph graph = vv.getGraphLayout().getGraph();
                    Entities.Link newEdge = (Entities.Link) this.edgeFactory.create();
                    String s = (String)JOptionPane.showInputDialog(
                            vv, "Enter the weight", "Customized dialog", JOptionPane.PLAIN_MESSAGE, null, null, null);
                    if (s != null) {
                        try{
                            double w = Double.parseDouble(s);
                            newEdge.setWeight(w);
                            layout.setLocation(newEdge,
                                    vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
                            graph.addEdge(newEdge, this.startVertex, vertex, this.edgeIsDirected);
                        }catch (Exception e1){
                            JOptionPane.showMessageDialog(null, "Wrong input!");
                        }
                        vv.repaint();
                    }
                }
            }

            this.startVertex = null;
            this.down = null;
            this.edgeIsDirected = EdgeType.UNDIRECTED;
            vv.removePostRenderPaintable(this.edgePaintable);
            vv.removePostRenderPaintable(this.arrowPaintable);
        }
    }

    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float)down.getX();
        float y1 = (float)down.getY();
        float x2 = (float)out.getX();
        float y2 = (float)out.getY();
        AffineTransform xform = AffineTransform.getTranslateInstance((double)x2, (double)y2);
        float dx = x2 - x1;
        float dy = y2 - y1;
        float thetaRadians = (float)Math.atan2((double) dy, (double) dx);
        xform.rotate((double) thetaRadians);
        this.arrowShape = xform.createTransformedShape(this.rawArrowShape);
    }
}
