package org.bihe.bean;

/**
 * This class stores id, type and label of features
 */
public class Feature {
    private final int id;
    private final FeatureType type;
    private final String label;

    /**
     * Feature Constructor
     * @param id ID of feature
     * @param type Type of feature
     * @param label Label of feature
     */
    public Feature(int id, FeatureType type, String label) {
        this.id = id;
        this.type = type;
        this.label = label;
    }


    public int getId() {
        return id;
    }

    public FeatureType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "ID: " + id + " Label: " + label + " Type: " + type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return id == feature.id;
    }
}
