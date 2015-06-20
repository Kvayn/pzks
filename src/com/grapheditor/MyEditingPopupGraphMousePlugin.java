package com.grapheditor;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;
import org.apache.commons.collections15.Factory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class MyEditingPopupGraphMousePlugin<V,E> extends EditingPopupGraphMousePlugin {
    protected JPopupMenu popup = new JPopupMenu();
    protected GraphEntity graphEntity;

    public MyEditingPopupGraphMousePlugin(Factory vertexFactory, Factory edgeFactory, GraphEntity graphEntity) {
        super(vertexFactory, edgeFactory);
         this.graphEntity = graphEntity;
    }

    @Override
    protected void handlePopup(MouseEvent e) {
        final VisualizationViewer vv = (VisualizationViewer)e.getSource();
        final Layout layout = vv.getGraphLayout();
        final Graph graph = layout.getGraph();
        final Point p = e.getPoint();
        GraphElementAccessor pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            final Object vertex = pickSupport.getVertex(layout, p.getX(), p.getY());
            final Object edge = pickSupport.getEdge(layout, p.getX(), p.getY());
            final PickedState pickedVertexState = vv.getPickedVertexState();
            final PickedState pickedEdgeState = vv.getPickedEdgeState();
            if(vertex == null) {
                if(edge != null) {
                    this.popup.add(new AbstractAction("Edit Edge") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            pickedEdgeState.pick(edge, false);
                            Entities.Link link = (Entities.Link)edge;
                            String s = (String) JOptionPane.showInputDialog(
                                    vv, "Enter the weight", "Customized dialog", JOptionPane.PLAIN_MESSAGE, null, null, null);
                            if (s != null) {
                                try {
                                    double w = Double.parseDouble(s);
                                    link.setWeight(w);
                                } catch (Exception e2) {
                                    JOptionPane.showMessageDialog(null, "Wrong input!");
                                }
                            }
                            vv.repaint();
                        }
                    });
                    this.popup.add(new AbstractAction("Remove Edge") {
                        public void actionPerformed(ActionEvent e) {
                            pickedEdgeState.pick(edge, false);
                            graph.removeEdge(edge);
                            vv.repaint();
                        }
                    });
                }
            } else {
                if (vertex != null){
                    if (edge == null){
                        this.popup.add(new AbstractAction("Edit Vertex") {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                pickedVertexState.pick(vertex, false);
                                Entities.Node node = (Entities.Node) vertex;
                                String s = (String) JOptionPane.showInputDialog(
                                        vv, "Enter the weight", "Customized dialog", JOptionPane.PLAIN_MESSAGE, null, null, null);
                                if (s != null) {
                                    try {
                                        double w = Double.parseDouble(s);
                                        node.setWeight(w);
                                    } catch (Exception e2) {
                                        JOptionPane.showMessageDialog(null, "Wrong input!");
                                    }
                                }
                                vv.repaint();
                            }
                        });
                        this.popup.add(new AbstractAction("Remove Vertex") {
                            public void actionPerformed(ActionEvent e) {
                                pickedVertexState.pick(vertex, false);
                                graph.removeVertex(vertex);
                                vv.repaint();
                            }
                        });

                    }
                }

            }
            if(this.popup.getComponentCount() > 0) {
                this.popup.show(vv, e.getX(), e.getY());
            }
        }
        this.popup = new JPopupMenu();

    }
}

