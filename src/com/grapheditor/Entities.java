package com.grapheditor;

import java.io.Serializable;

public class Entities implements Serializable{

    public class Node implements Serializable{
        private double weight;
        private int id;
        public Node(double weight, int id){
            this.id = id;
            this.weight = weight;

        }

        public int getId() {
            return id;
        }

        public void setWeight(double weight){
            this.weight = weight;
        }
        public double getWeight(){
            return  weight;
        }
        public String toString(){
            return "V" + id + "/w" + weight;
        }
    }
    public class Link implements Serializable{
        private double weight;
        private int id;

        public Link(double weight, int id){
            this.id = id;
            this.weight = weight;
        }
        public void setWeight(double weight){
            this.weight = weight;
        }
        public double getWight(){
            return weight;
        }
        public String toString(){
            return "E" + id + "/w" + weight;
        }
    }
}


