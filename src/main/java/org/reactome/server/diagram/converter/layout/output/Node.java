package org.reactome.server.diagram.converter.layout.output;

import org.reactome.server.diagram.converter.layout.input.model.NodeAttachments;
import org.reactome.server.diagram.converter.layout.input.model.OrgGkRenderRenderableFeature;
import org.reactome.server.diagram.converter.layout.util.ShapeBuilder;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@XmlRootElement
public class Node extends NodeCommon {

    public List<NodeAttachment> nodeAttachments;

    public SummaryItem interactorsSummary;

    public List<Long> componentIds;

    public List<Connector> connectors;

    public Boolean trivial = null;

    // This is used only in case of the Genes, to hold the arrow shape
    public Shape endShape;

    // This is used only in case of ChemicalDrug to add the Rx in a box placed at the bottom right corner
    public Shape shapeAttachment;

    public Node(Object obj) {
        super(obj);
        for (Method method : obj.getClass().getMethods()) {
            switch (method.getName()) {
                case "getNodeAttachments":
                    this.nodeAttachments = getNodeAttachments(method, obj);
                    break;
                case "getComponents":
                    this.componentIds = getComponents(method, obj);
                    break;
            }
        }
        connectors = new LinkedList<>();

        // Fix the Gene shape's height
        this.fixGeneHeight();

        // Calculate the arrow shape of the Gene
        this.setEndShape();

        //Set the inner box
        this.setInnerBox();

        //this.setChemicalDrugShapeAttachment();
        // Get rid of position as it simply points to the center of the node
        //position = null; //TODO Enable it
    }

    @Override
    public void translate(Coordinate panning){
        super.translate(panning);
        if (nodeAttachments != null) nodeAttachments.forEach(a -> a.translate(panning));
        if (interactorsSummary != null) interactorsSummary.translate(panning);
        if (connectors != null) connectors.forEach(c -> c.translate(panning));
        if (endShape != null) endShape.translate(panning);
        if (shapeAttachment != null) shapeAttachment.translate(panning);
    }

    private List<NodeAttachment> getNodeAttachments(Method method, Object object) {
        List<NodeAttachment> rtn = new LinkedList<>();
        try {
            NodeAttachments nodeAttachments = (NodeAttachments) method.invoke(object);
            if (nodeAttachments != null) {
                for (OrgGkRenderRenderableFeature item : nodeAttachments.getOrgGkRenderRenderableFeature()) {
                    if (item != null) {
                        rtn.add(new NodeAttachment(item, this));
                    }
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return rtn.isEmpty() ? null : rtn;
    }

    public void setBoundaries() {
        List<Integer> xx = new ArrayList<>();
        xx.add(this.prop.x);
        xx.add(this.prop.x + this.prop.width);

        List<Integer> yy = new ArrayList<>();
        yy.add(this.prop.y);
        yy.add(this.prop.y + this.prop.height);

        // Take into account the connectors
        for (Connector connector : this.connectors) {
            for (Segment segment : connector.segments) {
                xx.add(segment.from.x);
                xx.add(segment.to.x);
                yy.add(segment.from.y);
                yy.add(segment.to.y);
            }
            if (connector.endShape != null) {
                xx.add(connector.endShape.minX);
                xx.add(connector.endShape.maxX);
                yy.add(connector.endShape.minY);
                yy.add(connector.endShape.maxY);
            }
            // Also take into account the Stoichiometry boxes
            if (connector.stoichiometry != null && connector.stoichiometry.shape != null) {
                xx.add(connector.stoichiometry.shape.minX);
                xx.add(connector.stoichiometry.shape.maxX);
                yy.add(connector.stoichiometry.shape.minY);
                yy.add(connector.stoichiometry.shape.maxY);
            }
        }

        // Also take into account the NodeAttachment boxes
        if (nodeAttachments != null) {
            for (NodeAttachment nodeAttachment : nodeAttachments) {
                xx.add(nodeAttachment.shape.a.x);
                yy.add(nodeAttachment.shape.a.y);
                xx.add(nodeAttachment.shape.b.x);
                yy.add(nodeAttachment.shape.b.y);
            }
        }
        if (interactorsSummary != null) {
            xx.add(interactorsSummary.shape.minX);
            yy.add(interactorsSummary.shape.minY);
            xx.add(interactorsSummary.shape.maxX);
            yy.add(interactorsSummary.shape.maxY);
        }
        // In case of a gene also include the arrow
        //noinspection Duplicates
        if (endShape != null) {
            xx.add(endShape.minX);
            xx.add(endShape.maxX);
            yy.add(endShape.minY);
            yy.add(endShape.maxY);
        }

        this.minX = Collections.min(xx);
        this.maxX = Collections.max(xx);
        this.minY = Collections.min(yy);
        this.maxY = Collections.max(yy);
    }

    private void fixGeneHeight() {
        if (this.renderableClass.equals("Gene") && this.prop.height < 51) {
            this.prop.height = 51; //override the height to the default
        }
    }

    private void setEndShape() {
        if (this.renderableClass.equals("Gene")) {
            // Calculate the arrow shape of the Gene
            List<Coordinate> points = ShapeBuilder.createArrow(
                    this.prop.x + this.prop.width,
                    this.prop.y + 2,
                    this.prop.x + this.prop.width + ShapeBuilder.ARROW_LENGTH,
                    this.prop.y + 2);
            // Shape is a filled arrow by default
            this.endShape = new Shape(points.get(0), points.get(1), points.get(2), Boolean.FALSE, Shape.Type.ARROW);
        }
    }

    /***
     * Calculates the inner box of a Node taking into account
     * any NodeAttachements
     */
    private void setInnerBox() {
        if (nodeAttachments!=null){
            int topMargin = 0;
            int leftMargin = 0;
            int rightMargin = 0;
            int bottomMargin = 0;

            for (NodeAttachment attachment : nodeAttachments) {
                if(attachment.relativeY == 0) topMargin = 6;
                if(attachment.relativeY == 1) bottomMargin = 6;
                if(attachment.relativeX == 0) leftMargin = 6;
                if(attachment.relativeX == 1) rightMargin = 6;
            }

            if(topMargin != 0 || bottomMargin != 0 || leftMargin != 0 || rightMargin != 0) {
                innerProp = new NodeProperties();
                innerProp.x = prop.x + leftMargin;
                innerProp.y = prop.y + topMargin;
                innerProp.width = prop.width - (leftMargin + rightMargin);
                innerProp.height = prop.height - (topMargin + bottomMargin);
            }
        }
    }

    public void setSummaryItems() {
        switch (this.renderableClass) {
            case "Protein":
            case "Chemical":
            case "Complex":
                this.interactorsSummary = new SummaryItem(SummaryItem.Type.TR, this);
                break;
        }
    }

    private void setChemicalDrugShapeAttachment() {
        if (this.renderableClass.equals("ChemicalDrug")) {
            int offset = ShapeBuilder.EDGE_TYPE_WIDGET_WIDTH / 2;
            Coordinate c = new Coordinate(prop.x + prop.width - offset, prop.y + prop.height - offset);
            this.shapeAttachment = ShapeBuilder.createNodeAttachmentBox(c, "Rx");
        }
    }
}
