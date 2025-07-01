package org.reactome.server.diagram.converter.layout.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("WeakerAccess")
public class Shape {
    public enum Type { ARROW, BOX, CIRCLE, DOUBLE_CIRCLE, STOP }

    public Coordinate a;
    public Coordinate b;
    public Coordinate c;
    public Integer r;
    public Integer r1;
    public String s;        //symbol e.g. ?, \\, 0-9
    public Type type;
    public Boolean empty = null;

    public Shape(Coordinate a, Coordinate b, Boolean empty, Type type) {
        if(!type.equals(Type.BOX)){
            throw new RuntimeException("This constructor can only be used for box");
        }
        this.a = new Coordinate(a);
        this.b = new Coordinate(b);
        if(empty){
            this.empty = true;
        }
        this.type = type;
        setBoundaries();
    }

    public Shape(Coordinate a, Coordinate b, Coordinate c, Boolean empty, Type type) {
        if(!type.equals(Type.ARROW) && !type.equals(Type.STOP)){
            throw new RuntimeException("This constructor can only be used for arrow or stop");
        }
        this.a = new Coordinate(a);
        this.b = new Coordinate(b);
        this.c = new Coordinate(c);
        if(empty){
            this.empty = true;
        }
        this.type = type;
        setBoundaries();
    }

    public Shape(Coordinate c, Integer r, Boolean empty, Type type) {
        if(!type.equals(Type.CIRCLE) && !type.equals(Type.DOUBLE_CIRCLE)){
            throw new RuntimeException("This constructor can only be used for circles");
        }
        this.c = new Coordinate(c);
        this.r = r;
        if(empty){ //Otherwise is left 'null' so it does NOT appear in the serialisation
            this.empty = true;
        }
        this.type = type;
        setBoundaries();
    }

    transient Integer minX; transient Integer maxX;
    transient Integer minY; transient Integer maxY;

    public Coordinate getCentre(){
        if(c!=null) return new Coordinate(c);
        Coordinate d = b.minus(a);
        return new Coordinate(a.x + d.x / 2, a.y + d.y / 2);
    }

    public void translate(Coordinate panning){
        if (a != null) a.translate(panning);
        if (b != null) b.translate(panning);
        if (c != null) c.translate(panning);
        this.minX += panning.x;
        this.maxX += panning.x;
        this.minY += panning.y;
        this.maxY += panning.y;
    }

    protected void setBoundaries() {
        List<Integer> xx = new ArrayList<>();
        List<Integer> yy = new ArrayList<>();
        switch (type) {
            case CIRCLE:
            case DOUBLE_CIRCLE:
                xx.add(c.x + r); yy.add(c.y + r);
                xx.add(c.x - r); yy.add(c.y - r);
                break;
            case ARROW:
                xx.add(c.x); yy.add(c.y);
            default:
                xx.add(a.x); yy.add(a.y);
                xx.add(b.x); yy.add(b.y);
        }

        this.minX = Collections.min(xx);
        this.maxX = Collections.max(xx);
        this.minY = Collections.min(yy);
        this.maxY = Collections.max(yy);
    }

    public boolean overlaps(Shape o2) {
        return pointInRectangle(o2.minX, o2.minY) ||
               pointInRectangle(o2.minX, o2.maxY) ||
               pointInRectangle(o2.maxX, o2.minY) ||
               pointInRectangle(o2.maxX, o2.maxY);
    }

    public boolean touches(Segment s) {
        return pointInRectangle(s.from.x, s.from.y) || pointInRectangle(s.to.x, s.to.y);
    }

    private boolean pointInRectangle(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
