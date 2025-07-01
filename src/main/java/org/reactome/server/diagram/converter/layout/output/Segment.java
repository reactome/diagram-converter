package org.reactome.server.diagram.converter.layout.output;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class Segment {

    public Coordinate from;
    public Coordinate to;

    public Segment(Coordinate from, Coordinate to) {
        this.from = new Coordinate(from);
        this.to = new Coordinate(to);
    }

    public double length(){
        Coordinate diff = this.from.minus(this.to);
        return Math.sqrt(diff.x*diff.x + diff.y*diff.y);
    }

    @JsonIgnore
    public boolean isPoint(){
        return this.from.equals(this.to);
    }

    public void translate(Coordinate panning){
        from.translate(panning);
        to.translate(panning);
    }
}
