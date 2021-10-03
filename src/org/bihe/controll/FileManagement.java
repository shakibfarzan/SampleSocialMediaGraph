package org.bihe.controll;

import org.bihe.bean.*;
import org.bihe.exception.ExistElementException;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Class for read and write to files
 */
public class FileManagement {
    public static final String NODES_PATH = "files/nodes.csv";
    public static final String EDGES_PATH = "files/edges.csv";
    public static final String FEATURES_PATH = "files/features.csv";
    public static final String FEATURES_MAP_PATH = "files/feature_map.csv";

    public static void initNodes(Graph graph, String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String row;
            while ((row = reader.readLine()) != null) {
                String[] data = row.split(",");
                try {
                    graph.addNode(data[0], data[1], data[2]);
                } catch (ExistElementException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToNodes(Graph graph) {
        Collection<Node> nodes = graph.getNodes().values();
        StringBuilder sb = new StringBuilder();
        for (var node : nodes) {
            sb.append(node.getId()).append(",").append(node.getLabel()).append(",").append(node.getType().name()).append("\n");
        }
        writeToFile(sb, NODES_PATH);
    }

    public static void initEdges(Graph graph, String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String row;
            while ((row = reader.readLine()) != null) {
                String[] data = row.split(",");
                graph.addEdge(data[0], data[1], data[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToEdges(Graph graph) {
        HashMap<Node, List<Edge>> adj = graph.getAdjacencyList();
        StringBuilder sb = new StringBuilder();
        for (var node : adj.keySet()) {
            if (adj.get(node).isEmpty()) continue;
            for (var edge : adj.get(node)) {
                sb.append(node.getId()).append(",").append(edge.getDestination().getId())
                        .append(",").append(edge.getType().name()).append("\n");
            }
        }
        writeToFile(sb, EDGES_PATH);
    }

    public static void initFeatures(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String row;
            while ((row = reader.readLine()) != null) {
                String[] data = row.split(",");
                FeatureType type = (data[1].charAt(0) == '#') ? FeatureType.HASHTAG : FeatureType.MENTION;
                try {
                    MainController.addFeature(Integer.parseInt(data[0]), type, data[1]);
                } catch (ExistElementException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFeatures(){
        StringBuilder sb = new StringBuilder();
        for (var feature: MainController.features.values()){
            sb.append(feature.getId()).append(",").append(feature.getLabel()).append("\n");
        }
        writeToFile(sb, FEATURES_PATH);
    }

    public static void initFeatureMap(Graph graph, String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String row;
            while ((row = reader.readLine()) != null) {
                String[] data = row.split(",");
                String nodeID = data[0];
                for (int i = 1; i < data.length; i++) {
                    if (data[i].equals("1")) {
                        try {
                            graph.addFeature(nodeID, i);
                        } catch (ExistElementException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFeatureMap(Graph graph){
        HashMap<Node, List<Feature>> features = graph.getFeatures();
        StringBuilder sb = new StringBuilder();
        for (var node: features.keySet()){
            List<Feature> featureList = features.get(node);
            sb.append(node.getId()).append(",");
            for (var f: MainController.features.values()){
                if (featureList.contains(f)){
                    sb.append("1");
                }else{
                    sb.append("0");
                }
                sb.append(",");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append("\n");
        }
        writeToFile(sb, FEATURES_MAP_PATH);
    }

    private static void writeToFile(StringBuilder sb, String path){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path, false));
            out.write(sb.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
