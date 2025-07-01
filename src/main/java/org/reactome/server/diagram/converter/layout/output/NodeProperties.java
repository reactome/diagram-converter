package org.reactome.server.diagram.converter.layout.output;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class NodeProperties {

    public int x;
    public int y;
    public int width;
    public int height;

    // Returns true if the two represented rectangles overlap
    public boolean overlaps(NodeProperties o2, int offset) {
        return pointInRectangle(o2.x, o2.y, offset) ||
               pointInRectangle(o2.x, o2.y + o2.height, offset) ||
               pointInRectangle(o2.x + o2.width, o2.y, offset) ||
               pointInRectangle(o2.x + o2.width, o2.y + o2.height, offset);
    }

    private boolean pointInRectangle(int x, int y, int offset) {
        int minX = this.x + offset;
        int minY = this.y + offset;
        int maxX = this.x + this.width - offset;
        int maxY = this.y + this.height - offset;

        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public void translate(Coordinate panning){
        this.x += panning.x;
        this.y += panning.y;
    }
}
