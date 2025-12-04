package org.dotsandboxestcp.game;

import java.util.Objects;

public class Edge {
    public final int x;
    public final int y;
    public final boolean horizontal;

    public Edge(int x, int y, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.horizontal = horizontal;
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
