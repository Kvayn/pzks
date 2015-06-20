package com.grapheditor;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.Map;

public class MyDirectedSparseGraph extends DirectedSparseGraph {
    @Override
    public boolean addEdge(Object edge, Pair endpoints, EdgeType edgeType) {
        this.validateEdgeType(edgeType);
        Pair new_endpoints = this.getValidatedEndpoints(edge, endpoints);
        if(new_endpoints == null) {
            return false;
        } else {
            Object source = new_endpoints.getFirst();
            Object dest = new_endpoints.getSecond();
            if(this.findEdge(source, dest) != null || this.findEdge(dest, source) != null) {
                return false;
            } else {
                this.edges.put(edge, new_endpoints);
                if(!this.vertices.containsKey(source)) {
                    this.addVertex(source);
                }

                if(!this.vertices.containsKey(dest)) {
                    this.addVertex(dest);
                }

                ((Map)((Pair)this.vertices.get(source)).getSecond()).put(dest, edge);
                ((Map)((Pair)this.vertices.get(dest)).getFirst()).put(source, edge);
                return true;
            }
        }
    }
}
