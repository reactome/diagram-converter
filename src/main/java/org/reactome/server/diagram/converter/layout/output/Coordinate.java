package org.reactome.server.diagram.converter.layout.output;

import java.util.List;
import java.util.Objects;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("WeakerAccess")
public class Coordinate {

    public int x;
    public int y;

    public Coordinate(Coordinate c) {
        this.x = c.x;
        this.y = c.y;
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(List<Integer> points) {
        this.x = points.get(0);
        this.y = points.get(1);
    }

    public Coordinate add(Coordinate value){
        return new Coordinate(x+value.x, y+value.y);
    }

    public Coordinate minus(Coordinate value){
        return new Coordinate(x-value.x, y-value.y);
    }

    public void translate(Coordinate panning){
        this.x += panning.x;
        this.y += panning.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
