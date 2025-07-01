package org.reactome.server.diagram.converter.layout.shadows.partitioner;

/**
 * This is just a helper POJO for returning an appropriate object
 * The decompose method has the x and y coordinates.
 * So, instead of creating a List containing string arrays,
 * this helper class model a XYPoint properly.
 *
 * @author Guilherme S Viteri (gviteri@ebi.ac.uk)
 */
public class XYPoint {

    private int x;
    private int y;

    public XYPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
