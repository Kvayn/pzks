package com.grapheditor;

/**
 * Created by serhii on 16.06.15.
 */
public class Task {
    int idVertex;
    double priority;

    public Task(int idVertex, double priority){
        this.idVertex = idVertex;
        this.priority = priority;
    }

    public int getIdVertex() {
        return idVertex;
    }

    public double getPriority() {
        return priority;
    }

    public void setIdVertex(int idVertex) {
        this.idVertex = idVertex;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Task{" +
                "idVertex=" + idVertex +
                ", priority=" + priority +
                '}';
    }
}
