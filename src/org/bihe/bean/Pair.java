package org.bihe.bean;

import java.util.Objects;

/**
 * Stores pair of data of any type
 * @param <F>
 * @param <S>
 */
public class Pair<F, S> {
    private final F first;
    private final S second;

    /**
     * Pair constructor
     * @param first First element
     * @param second Second element
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return (Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second)) || (Objects.equals(first, pair.second) && Objects.equals(second, pair.first));
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "( "+first+", "+second+" )";
    }
}

