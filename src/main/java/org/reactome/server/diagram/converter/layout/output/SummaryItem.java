package org.reactome.server.diagram.converter.layout.output;

import org.reactome.server.diagram.converter.layout.util.ShapeBuilder;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@XmlRootElement
public class SummaryItem {
    public enum Type {
        TL(0F, 0F),     //Top Left
        TR(1F, 0F),     //Top Right
        BR(1F, 1F),     //Bottom Right
        BL(0F, 1F);     //Bottom Left

        float relativeX;
        float relativeY;

        Type(float relativeX, float relativeY) {
            this.relativeX = relativeX;
            this.relativeY = relativeY;
        }
    }

    public Type type;
    public Shape shape;

    public SummaryItem(Type type, Node node) {
        this.type = type;
        int offset = node.schemaClass.equals("SimpleEntity") ? 5 : 0;

        Coordinate boxCentre = new Coordinate(
                Math.round(node.prop.x + node.prop.width * type.relativeX - offset),
                Math.round(node.prop.y + node.prop.height * type.relativeY + offset)
        );
        //Interactors number depends on the selected source.
        // Here we only create an empty place holder
        this.shape = ShapeBuilder.createNodeSummaryItem(boxCentre, null);
    }

    public void translate(Coordinate panning){
        if(shape!=null) shape.translate(panning);
    }
}
