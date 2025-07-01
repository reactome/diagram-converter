package org.reactome.server.diagram.converter.layout.output;

import org.reactome.server.diagram.converter.layout.util.ShapeBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EdgeCommon extends DiagramObject {
    public String reactionType;
    public String interactionType;

    public List<Segment> segments;
    public List<Coordinate> points;
    public Shape endShape;
    public Shape reactionShape;

    public List<ReactionPart> inputs;
    public List<ReactionPart> outputs;
    public List<ReactionPart> catalysts;
    public List<ReactionPart> inhibitors;
    public List<ReactionPart> activators;

    public EdgeCommon(Object obj) {
        super(obj);
        for (Method method : obj.getClass().getMethods()) {
            switch (method.getName()){
                case "getReactionType":
                    this.reactionType = getString(method, obj);
                    break;
                case "getInteractionType":
                    this.interactionType = getString(method, obj);
                    break;
                case "getPoints":
                    String points = getString(method, obj);
                    this.points = extractIntegerPairsListFromString(points, ",", " ");
                    this.segments = getSegments(this.points);
                    break;
                case "getInputs":
                    this.inputs = this.extractReactionPart(method, obj, "getInput");
                    break;
                case "getOutputs":
                    this.outputs = this.extractReactionPart(method, obj, "getOutput");
                    break;
                case "getCatalysts":
                    this.catalysts = this.extractReactionPart(method, obj, "getCatalyst");
                    break;
                case "getInhibitors":
                    this.inhibitors = this.extractReactionPart(method, obj, "getInhibitor");
                    break;
                case "getActivators":
                    this.activators = this.extractReactionPart(method, obj, "getActivator");
                    break;
            }
        }

        // Set the shape of the reaction
        this.setReactionShape();
        // Set the boundaries of the edge
        this.setBoundaries();

        // Get rid of the points after setting the segments and
        // do all the necessary calculations. The segments have all
        // the information of the points
        this.points = null;
    }

    @Override
    public void translate(Coordinate panning){
        super.translate(panning);
        if (points != null) points.forEach(p -> p.translate(panning));
        if (segments != null) segments.forEach(s -> s.translate(panning));
        if (endShape != null) endShape.translate(panning);
        if (reactionShape != null) reactionShape.translate(panning);
        if (inputs != null) inputs.forEach(i -> i.translate(panning));
        if (outputs != null) outputs.forEach(o -> o.translate(panning));
        if (catalysts != null) catalysts.forEach(c -> c.translate(panning));
        if (inhibitors != null) inhibitors.forEach(i -> i.translate(panning));
        if (activators != null) activators.forEach(a -> a.translate(panning));
    }

    private void setBoundaries(){
        List<Integer> xx = new LinkedList<>();
        List<Integer> yy = new LinkedList<>();
        if (points != null) {
            for (Coordinate point : points) {
                xx.add(point.x);
                yy.add(point.y);
            }
        }
        if (endShape != null) {
            xx.add(endShape.minX);
            xx.add(endShape.maxX);
            yy.add(endShape.minY);
            yy.add(endShape.maxY);
        }
        if (reactionShape != null) {
            xx.add(reactionShape.minX);
            xx.add(reactionShape.maxX);
            yy.add(reactionShape.minY);
            yy.add(reactionShape.maxY);
        }

        this.minX = Collections.min(xx);
        this.maxX = Collections.max(xx);
        this.minY = Collections.min(yy);
        this.maxY = Collections.max(yy);
    }

    protected void setReactionShape() {
        if (reactionType == null) {
            // If no type specified then
            // DRAW BOX
            this.reactionShape = ShapeBuilder.createReactionBox(position, null);
        } else if (reactionType.equals("Transition")) {
            //DRAW BOX
            this.reactionShape = ShapeBuilder.createReactionBox(position, null);
        } else if (reactionType.equals("Association")) {
            //DRAW CIRCLE FILLED
            this.reactionShape = ShapeBuilder.createReactionCircle(position);
        } else if (reactionType.equals("Dissociation")) {
            //DRAW DOUBLE CIRCLE
            this.reactionShape = ShapeBuilder.createReactionDoubleCircle(position);
        } else if (reactionType.equals("Omitted Process")) {
            //DRAW BOX WITH \\ as a symbol
            this.reactionShape = ShapeBuilder.createReactionBox(position, "\\\\");
        } else if (reactionType.equals("Uncertain Process")) {
            //DRAW BOX WITH ? as a symbols
            this.reactionShape = ShapeBuilder.createReactionBox(position, "?");
        }
    }

    private List<ReactionPart> extractReactionPart(Method method, Object object, String partMethod) {
        try {
            List<ReactionPart> rtn = new LinkedList<>();
            Object type = method.invoke(object);
            if (type == null) return null;
            for (Method parts : type.getClass().getMethods()) {
                if (parts != null) {
                    if (parts.getName().equals(partMethod)) {
                        List partList = (List) parts.invoke(type);
                        if (partList != null) {
                            for (Object part : partList) {
                                rtn.add(new ReactionPart(part));
                            }
                        }
                    }
                }
            }
            return rtn;
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
