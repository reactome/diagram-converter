package org.reactome.server.diagram.converter.layout.shadows;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class that holds the reactomeId and its intersection
 * These attributes will be used to get the available area inside the rectangle.
 * Once we get all intersections, we will be able to subtract them properly
 *
 * @author Guilherme S Viteri (gviteri@ebi.ac.uk)
 */
public class ReactomeShadow extends Rectangle {

    /**
     * Shadow reactomeId
     */
    private Long reactomeId;

    /**
     * Indicate whether a shadow is completely inside another shadow
     */
    private boolean inside = false;

    /**
     * List of other shadows which this rectangle intersects to.
     */
    private List<ReactomeShadow> intersections = null;

    public ReactomeShadow(ReactomeShadow r) {
        this(r.x, r.y, r.width, r.height);

        this.setReactomeId(r.getReactomeId());
    }

    public ReactomeShadow(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }

    public ReactomeShadow(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public ReactomeShadow(Point point, Long reactomeId) {
        super(point);
        this.reactomeId = reactomeId;
    }

    public Long getReactomeId() {
        return reactomeId;
    }

    public void setReactomeId(Long reactomeId) {
        this.reactomeId = reactomeId;
    }

    public List<ReactomeShadow> getIntersections() {
        return intersections;
    }

    public void setIntersections(List<ReactomeShadow> intersections) {
        this.intersections = intersections;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public void addIntersection(ReactomeShadow rInsetersection) {
        if (intersections == null) {
            intersections = new LinkedList<>();
        }
        intersections.add(rInsetersection);
    }

    @Override
    public ReactomeShadow intersection(Rectangle rect) {
        Rectangle rectIntersect = super.intersection(rect);

        ReactomeShadow ret = new ReactomeShadow(rectIntersect);

        return ret;
    }

    @Override
    public String toString() {
        String out = "ReactomeShadow: [ID: " + reactomeId + ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height;
        if (intersections != null) {
            out = out.concat(" Intersections=" + intersections);
        }

        out = out.concat("]");

        return out;
    }
}

