package org.reactome.server.diagram.converter.layout.util;

import org.reactome.server.diagram.converter.layout.output.Coordinate;
import org.reactome.server.diagram.converter.layout.output.Shape;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

/**
 * Calculates the points of the different shapes (circle, arrow, stop).
 *
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
@SuppressWarnings("WeakerAccess")
public abstract class ShapeBuilder {
    public static final double ARROW_ANGLE = Math.PI / 6;
    public static final int ARROW_LENGTH = 8;
    public static final int EDGE_TYPE_WIDGET_WIDTH = 12;
    public static final int CIRCLE_WIDGET_CORRECTION = 1;
    public static final int EDGE_MODULATION_WIDGET_WIDTH = 8;
    //public static double GENE_SYMBOL_PAD = 4;
    //public static double GENE_SYMBOL_WIDTH = 50;

    public static List<Coordinate> createArrow(
            double arrowPositionX,
            double arrowPositionY,
            double controlX,
            double controlY){
        // IMPORTANT!!! Segments start from the node and point to the backbone
        // Point used to calculate the angle of the arrow
        // Point used to place the  arrow

        double arrowLength = ARROW_LENGTH;
        double arrowAngle = ARROW_ANGLE;

        List<Coordinate> rtn = new LinkedList<>();

        // Get the angle of the line segment
        double alpha = Math.atan((arrowPositionY - controlY) / (arrowPositionX - controlX) );
        if (controlX > arrowPositionX)
            alpha += Math.PI;
        double angle = arrowAngle - alpha;
        float x1 = (float)(arrowPositionX - arrowLength * Math.cos(angle));
        float y1 = (float)(arrowPositionY + arrowLength * Math.sin(angle));
        rtn.add( new Coordinate(Math.round(x1), Math.round(y1)));
        // The tip of the arrow is the end of the segment
        rtn.add( new Coordinate(Math.round((float)arrowPositionX), Math.round((float)arrowPositionY)));

        angle = arrowAngle + alpha;
        float x2 = (float)(arrowPositionX - arrowLength * Math.cos(angle));
        float y2 = (float)(arrowPositionY - arrowLength * Math.sin(angle));
        rtn.add( new Coordinate(Math.round( x2), Math.round( y2)));
        return rtn;
    }

    public static List<Coordinate> createStop(
            double anchorX,
            double anchorY,
            double controlX,
            double controlY){

        List<Coordinate> rtn = new LinkedList<>();

        double deltaY = anchorY - controlY;
        double deltaX = controlX - anchorX;

        double angle = deltaY == 0 ? Math.PI / 2 : Math.atan(-deltaX/deltaY);
        double x1 = anchorX - (Math.cos(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        double x2 = anchorX + (Math.cos(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        double y1 = anchorY + (Math.sin(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);
        double y2 = anchorY - (Math.sin(angle) * EDGE_MODULATION_WIDGET_WIDTH / 2.0d);

        rtn.add( new Coordinate(Math.round((float)x1), Math.round( (float)y1)));
        rtn.add( new Coordinate(Math.round((float)x2), Math.round( (float)y2)));
        rtn.add( new Coordinate(Math.round((float)anchorX), Math.round( (float)anchorY)));

        return rtn;
    }

    public static Shape createReactionBox(Coordinate boxCentre, String symbol){
        Coordinate topLeft = new Coordinate(
                Math.round( (float) (boxCentre.x - EDGE_TYPE_WIDGET_WIDTH / 2.0)),
                Math.round( (float) (boxCentre.y - EDGE_TYPE_WIDGET_WIDTH / 2.0))
        );
        Coordinate bottomRight = new Coordinate(
                Math.round( (float) (topLeft.x + EDGE_TYPE_WIDGET_WIDTH)),
                Math.round( (float) (topLeft.y + EDGE_TYPE_WIDGET_WIDTH))
        );
        Shape rtn = new Shape(topLeft, bottomRight, Boolean.TRUE, Shape.Type.BOX);
        if (symbol != null) rtn.s = symbol;
        return rtn;
    }

    public static Shape createReactionCircle(Coordinate centre) {
        Integer radius = Math.round( (float) (EDGE_TYPE_WIDGET_WIDTH / 2.0d - CIRCLE_WIDGET_CORRECTION) );
        return new Shape(centre, radius, Boolean.FALSE, Shape.Type.CIRCLE);
    }

    public static Shape createReactionDoubleCircle(Coordinate centre) {
        Integer radius = Math.round( (float) (EDGE_TYPE_WIDGET_WIDTH / 2.0d - CIRCLE_WIDGET_CORRECTION) );
        Shape rtn = new Shape(centre, radius, Boolean.TRUE, Shape.Type.DOUBLE_CIRCLE);
        rtn.r1 = Math.round( (float) (ShapeBuilder.EDGE_TYPE_WIDGET_WIDTH / 2.0d - 2 - CIRCLE_WIDGET_CORRECTION) );
        return rtn;
    }

    public static Shape createStoichiometryBox(Coordinate boxCentre, String symbol){
        double width = -1;
        if(symbol!=null) { width = measureText(symbol).getWidth(); }
        if(width<EDGE_TYPE_WIDGET_WIDTH){ width = EDGE_TYPE_WIDGET_WIDTH; }
        Coordinate topLeft = new Coordinate(
                Math.round( (float) (boxCentre.x - width / 2.0)),
                Math.round( (float) (boxCentre.y - width / 2.0))
        );
        Coordinate bottomRight = new Coordinate(
                Math.round( (float) (topLeft.x + width)),
                Math.round( (float) (topLeft.y + width))
        );
        return new Shape(topLeft, bottomRight, Boolean.TRUE, Shape.Type.BOX);
    }

    public static Shape createNodeAttachmentBox(Coordinate boxCentre, String label){
        double width = -1;
        if(label!=null) { width = measureText(label).getWidth(); }
        if(width<EDGE_TYPE_WIDGET_WIDTH){ width = EDGE_TYPE_WIDGET_WIDTH; }
        Coordinate topLeft = new Coordinate(
                Math.round( (float) (boxCentre.x - width / 2.0)),
                Math.round( (float) (boxCentre.y - width / 2.0))
        );
        Coordinate bottomRight = new Coordinate(
                Math.round( (float) (topLeft.x + width)),
                Math.round( (float) (topLeft.y + width))
        );
        Shape shape = new Shape(topLeft, bottomRight, Boolean.TRUE, Shape.Type.BOX);
        if(label!=null) {shape.s = label;}
        return shape;
    }

    public static Shape createNodeSummaryItem(Coordinate boxCentre, String label) {
        double width = -1;
        if (label != null) width = measureText(label).getWidth();
        if (width < EDGE_TYPE_WIDGET_WIDTH) width = EDGE_TYPE_WIDGET_WIDTH;
        Shape shape = new Shape(boxCentre, (int) width / 2, Boolean.TRUE, Shape.Type.CIRCLE);
        if (label != null) shape.s = label;
        return shape;
    }

    //TODO requires a bit work on the accuracy
    private static Rectangle2D measureText(String text){
        //Create a dummy BufferedImage to get the Graphincs object
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        //Set the used font
        g2.setFont(new Font("Arial", Font.PLAIN, 8));
        Font font = g2.getFont();
        // get context from the graphics
        FontRenderContext context = g2.getFontRenderContext();
        return font.getStringBounds(text, context);
    }
}
