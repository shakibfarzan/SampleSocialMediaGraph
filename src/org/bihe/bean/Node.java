package org.bihe.bean;

public class Node {
    private final String id;
    private final String label;
    private final NodeType type;

    /**
     * Node Constructor
     * @param id ID of node
     * @param label Label of node
     * @param type Type of node
     */
    public Node(String id, String label, NodeType type) {
        this.id = id;
        this.label = label;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Label: " + label + " Type: " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return this.id.equals(node.id);
    }


}
