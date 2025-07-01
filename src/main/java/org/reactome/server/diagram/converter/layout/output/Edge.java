package org.reactome.server.diagram.converter.layout.output;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@XmlRootElement
public class Edge extends EdgeCommon {

    public Edge(Object obj) {
        super(obj);
        centerReaction();
    }

    private void centerReaction() {
        if (this.segments.size() == 1) {
            Coordinate p1 = this.segments.get(0).from;
            Coordinate p2 = this.segments.get(0).to;
            Coordinate midPoint = new Coordinate((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
            this.points = List.of(p1, midPoint, p2);
            this.position = midPoint;
            this.segments = getSegments(this.points);
            this.setReactionShape();
            this.points = null;
        }
    }
}