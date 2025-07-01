package org.reactome.server.diagram.converter.layout.output;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class Bound {
    public Integer x;
    public Integer y;
    public Integer width;
    public Integer height;

    public Bound(List<Integer> points) {
        this.x = points.get(0);
        this.y = points.get(1);
        this.width = points.get(2);
        this.height = points.get(3);
    }

    public void translate(Coordinate panning){
        this.x += panning.x;
        this.y += panning.y;
    }
}
