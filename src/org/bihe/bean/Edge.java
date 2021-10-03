package org.bihe.bean;

/**
 * This class stores type of edge and destination node
 */
public class Edge {
    private final Node destination;
    private final EdgeType type;

    /**
     * Edge Constructor
     * @param destination Destination node of edge
     * @param type Type of edge
     */
    public Edge(Node destination, EdgeType type) {
        this.destination = destination;
        this.type = type;
    }

    public Node getDestination() {
        return destination;
    }

    public EdgeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Destination: " + destination + " Type: " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return this.destination.equals(edge.destination) && type == edge.type;
    }



}
