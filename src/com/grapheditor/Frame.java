package com.grapheditor;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.PickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.PluggableGraphMouse;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class Frame extends JFrame {
    GraphEntity taskGraphEntity;
    GraphEntity systemGraphEntity;
    JTabbedPane jtp;
    public Frame(){
        super("Window");
        taskGraphEntity = new GraphEntity(true);
        systemGraphEntity = new GraphEntity(false);

        PluggableGraphMouse editingTaskPlugin = new PluggableGraphMouse();
        PluggableGraphMouse editingSystemPlugin = new PluggableGraphMouse();
        editingTaskPlugin.add(new MyEditingGraphMousePlugin<>(taskGraphEntity.nodeFactory,
                taskGraphEntity.linkFactory));
        editingTaskPlugin.add(new MyEditingPopupGraphMousePlugin(taskGraphEntity.nodeFactory,
                taskGraphEntity.linkFactory, taskGraphEntity));
        editingSystemPlugin.add(new MyEditingGraphMousePlugin<>(systemGraphEntity.nodeFactory,
                systemGraphEntity.linkFactory));
        editingSystemPlugin.add(new MyEditingPopupGraphMousePlugin<>(systemGraphEntity.nodeFactory,
                systemGraphEntity.linkFactory, systemGraphEntity));
        PluggableGraphMouse pickingPlugin = new PluggableGraphMouse();
        pickingPlugin.add(new PickingGraphMousePlugin<>());


        jtp = new JTabbedPane();
        getContentPane().add(jtp);
        JPanel taskGraphPanel = new JPanel();
        JPanel systemGraphPanel = new JPanel();
        jtp.addTab("Task Graph", taskGraphPanel);
        jtp.addTab("System Graph", systemGraphPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        taskGraphPanel.add(taskGraphEntity.vv);
        systemGraphPanel.add(systemGraphEntity.vv);

        JMenuBar menuBar = new JMenuBar();
        JMenu modeMenu = new JMenu("Change mode");
        JMenuItem editingItem = new JMenuItem("Editing mode");
        JMenuItem pickingItem = new JMenuItem("Picking mode");

        editingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jtp.getSelectedIndex() == 0){
                    taskGraphEntity.vv.setGraphMouse(editingTaskPlugin);
                }else{
                    systemGraphEntity.vv.setGraphMouse(editingSystemPlugin);
                }
            }
        });
        pickingItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(jtp.getSelectedIndex() == 0){
                    taskGraphEntity.vv.setGraphMouse(pickingPlugin);
                }else{
                    systemGraphEntity.vv.setGraphMouse(pickingPlugin);
                }
            }
        });

        modeMenu.add(editingItem);
        modeMenu.add(pickingItem);

        JMenu menuFile = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem saveButton = new JMenuItem("Save");
        JMenuItem loadButton = new JMenuItem("Open");
        JMenuItem exitButton = new JMenuItem("Exit");
        menuFile.add(newItem);
        menuFile.add(loadButton);
        menuFile.add(saveButton);
        menuFile.add(exitButton);
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph<Entities.Node, Entities.Link> graph;
                if (jtp.getSelectedIndex() == 0){
                    graph = taskGraphEntity.vv.getModel().getGraphLayout().getGraph();
                }else{
                    graph = systemGraphEntity.vv.getModel().getGraphLayout().getGraph();
                }

                ArrayList<Entities.Node> rnodes = new ArrayList<Entities.Node>(graph.getVertices());
                for (int i = 0; i < rnodes.size(); i++){
                    graph.removeVertex(rnodes.get(i));
                }
                taskGraphEntity.vv.repaint();
                systemGraphEntity.vv.repaint();
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                if (jtp.getSelectedIndex() == 0) {
                    int ret = fileChooser.showDialog(null, "Save task graph");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        taskGraphEntity.saveGraph(file);
                    }
                } else {
                    int ret = fileChooser.showDialog(null, "Save system graph");
                    if (ret == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        systemGraphEntity.saveGraph(file);
                    }
                }
                taskGraphEntity.vv.repaint();
            }
        });
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int ret = fileChooser.showDialog(null, "Open graph");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    GraphSerializable graphSerializable = new GraphSerializable().loadGraph(file);
                    Graph<Entities.Node, Entities.Link> graph;
                    Layout layout;
                    if (jtp.getSelectedIndex() == 0) {
                        graph = taskGraphEntity.vv.getModel().getGraphLayout().getGraph();
                        layout = taskGraphEntity.vv.getModel().getGraphLayout();
                        taskGraphEntity.setLinkCounter(graphSerializable.getLinkCounter());
                        taskGraphEntity.setNodeCounter(graphSerializable.getNodeCounter());
                    } else {
                        graph = systemGraphEntity.vv.getModel().getGraphLayout().getGraph();
                        layout = systemGraphEntity.vv.getModel().getGraphLayout();
                        systemGraphEntity.setLinkCounter(graphSerializable.getLinkCounter());
                        systemGraphEntity.setNodeCounter(graphSerializable.getNodeCounter());
                    }

                    ArrayList<Entities.Node> rnodes = new ArrayList<Entities.Node>(graph.getVertices());
                    for (int i = 0; i < rnodes.size(); i++) {
                        graph.removeVertex(rnodes.get(i));
                    }


                    Iterator<Entities.Node> nodesIter = graphSerializable.getGraph().getVertices().iterator();
                    while (nodesIter.hasNext()) {
                        Entities.Node node = nodesIter.next();
                        graph.addVertex(node);
                        layout.setLocation(node, graphSerializable.getPoints().get(node));
                    }
                    Iterator<Entities.Link> linksIter = graphSerializable.getGraph().getEdges().iterator();
                    while (linksIter.hasNext()) {
                        Entities.Link link = linksIter.next();
                        if (jtp.getSelectedIndex() == 0) {
                            graph.addEdge(link, graphSerializable.getGraph().getSource(link),
                                    graphSerializable.getGraph().getDest(link));
                        } else {
                            graph.addEdge(link,
                                    graphSerializable.getGraph().getEndpoints(link).getFirst(),
                                    graphSerializable.getGraph().getEndpoints(link).getSecond());
                        }

                    }

                }

            }
        });

        JButton check = new JButton("Check");
        check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (jtp.getSelectedIndex() == 0) {
                    if (taskGraphEntity.isCycled()) {
                        JOptionPane.showMessageDialog(null, "Graph is CYCLED");
                    } else {
                        JOptionPane.showMessageDialog(null, "Graph is NOT cycled");
                    }
                } else {
                    if (systemGraphEntity.isConnected()) {
                        JOptionPane.showMessageDialog(null, "Graph is connected");
                    } else {
                        JOptionPane.showMessageDialog(null, "Graph is NOT connected");
                    }
                }


            }
        });

        JMenu menuPlaning = new JMenu("Planing");
        JMenuItem var1 = new JMenuItem("Variant 1");
        JMenuItem var5 = new JMenuItem("Variant 5");
        JMenuItem var16 = new JMenuItem("Variant 16");
        menuPlaning.add(var1);
        menuPlaning.add(var5);
        menuPlaning.add(var16);
        var1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!taskGraphEntity.isCycled()){
                    ArrayList<resultVertex> result = taskGraphEntity.labTwo();
                    String out = "";
                    for (int i = 0; i < result.size(); i++) {
                        if (i == result.size() - 1) {
                            out = out + result.get(i).getNode().getId();
                        }else{
                            out = out + result.get(i).getNode().getId() + ", ";
                        }
                    }
                    JOptionPane.showMessageDialog(null, out);
                    System.out.println(result);
                }else {
                    JOptionPane.showMessageDialog(null, "Graph is Cycled, it's impossible to do planing");
                }

            }
        });
        var5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!taskGraphEntity.isCycled()){
                    String out = "";
                    ArrayList<Entities.Node> tasks = taskGraphEntity.labThree();
                    for (int i = 0; i < tasks.size(); i++) {
                        if (i ==tasks.size() - 1) {
                            out = out + tasks.get(i).getId();
                        }else{
                            out = out + tasks.get(i).getId() + ", ";
                        }
                    }
                    JOptionPane.showMessageDialog(null, out);
                }else{
                    JOptionPane.showMessageDialog(null, "Graph is Cycled, it's impossible to do planing");
                }

            }
        });
        var16.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!taskGraphEntity.isCycled()){
                    String out = "";
                    ArrayList<Entities.Node> tasks = taskGraphEntity.labFour();
                    for (int i = 0; i < tasks.size(); i++) {
                        if (i ==tasks.size() - 1) {
                            out = out + tasks.get(i).getId();
                        }else{
                            out = out + tasks.get(i).getId() + ", ";
                        }
                    }
                    JOptionPane.showMessageDialog(null, out);
                }else{
                    JOptionPane.showMessageDialog(null, "Graph is Cycled, it's impossible to do planing");
                }
            }
        });
        menuBar.add(menuFile);
        menuBar.add(modeMenu);
        menuBar.add(check);
        menuBar.add(menuPlaning);
        taskGraphEntity.vv.setGraphMouse(editingTaskPlugin);
        systemGraphEntity.vv.setGraphMouse(editingSystemPlugin);
        setJMenuBar(menuBar);
        pack();
        setVisible(true);
    }

}
