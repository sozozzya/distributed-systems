package org.dotsandboxessoap.game.model;

import java.util.Objects;

public class Edge {
    public int x;
    public int y;
    public boolean horizontal;

    public Edge() {
        this(0, 0, false);
    }

    public Edge(int x, int y, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
    }

    public Edge(Edge other) {
        this.x = other.x;
        this.y = other.y;
        this.horizontal = other.horizontal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge e = (Edge) o;
        return x == e.x && y == e.y && horizontal == e.horizontal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, horizontal);
    }

    @Override
    public String toString() {
        return "Edge{" + "x=" + x + ", y=" + y + ", h=" + horizontal + '}';
    }
}
