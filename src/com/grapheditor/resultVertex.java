package com.grapheditor;

public class resultVertex implements Comparable<resultVertex>{
    private Entities.Node node;
    private double rate;

    public Entities.Node getNode() {
        return node;
    }

    public double getRate() {
        return rate;
    }

    public void setNode(Entities.Node node) {
        this.node = node;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public resultVertex(Entities.Node node, double rate) {
        this.node = node;
        this.rate = rate;
    }


    @Override
    public int compareTo(resultVertex o) {
        return rate > o.rate ? -1 : rate == o.rate ? 0 : 1;
    }

    @Override
    public String toString() {
        return "resultVertex{" +
                "node=" + node +
                ", rate=" + Math.rint(rate * 1000.0)/1000 +
                '}';
    }
}
