package org.reactome.server.diagram.converter.layout.shadows;


import org.reactome.server.diagram.converter.layout.output.Shadow;
import org.reactome.server.diagram.converter.layout.shadows.partitioner.Box;
import org.reactome.server.diagram.converter.layout.shadows.partitioner.ShapePartition;
import org.reactome.server.diagram.converter.layout.shadows.partitioner.XYPoint;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.*;
import java.util.List;


/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 * @author Guilherme Viteri (gviteri@ebi.ac.uk)
 */
public class ShadowsUtil {

    /**
     * This minimum height avoids thin area to be placed and then leave the text in the center
     */
    private final int MIN_HEIGHT_THRESHOLD = 42;

    /**
     * This minimum width avoids thin area to be placed and then leave the text in the center
     */
    private final int MIN_WIDTH_THRESHOLD = 130;

    private Set<Shadow> shadows = new HashSet<>();

    public ShadowsUtil(Set<Shadow> shadows) {
        this.shadows = shadows;
    }

    public Set<Shadow> getShadows() {
        process();
        return shadows;
    }

    /**
     * 1- Detect overlaps of each shadow and then subtract each intersection,
     * 2- Having the available area, partition it in rectangle strip.
     * 3- Get the largest and widest rectangle.
     * 4- Position the text
     */
    private void process() {
        /** Prepare reactome shadows list which will be further used with reactomeId after checking overlapping **/
        List<ReactomeShadow> reactomeShadowList = getReactomeShadowsList();

        /** Map that holds a reactomeId and its available area (rectangle) as a ReactomeShadow instance **/
        Map<Long, ReactomeShadow> availableAreas = getAvailableArea(reactomeShadowList);

        /** Having the available areas for each shadow, set each shadow.prop properly **/
        setShadowsProperties(availableAreas);

        /** Cover minor cases where shadows have same size **/
        checkOverlappingTexts(shadows);
    }

    /**
     * For each shadow, create a ReactomeShadow and set the reactomeId on it.
     *
     * @return list of ReactomeShadow
     */
    private List<ReactomeShadow> getReactomeShadowsList() {
        List<ReactomeShadow> reactomeShadowList = new ArrayList<>();
        for (Shadow shadow : shadows) {
            ReactomeShadow newShadow = new ReactomeShadow(new Point(shadow.minX, shadow.minY), shadow.reactomeId);
            newShadow.add(new Point(shadow.maxX, shadow.maxY));

            reactomeShadowList.add(newShadow);
        }

        /** Brute force: Check one against others and detect intersection **/
        for (int i = 0; i < reactomeShadowList.size(); i++) {
            /** Changing the object directly in the list. Using the same reference **/
            ReactomeShadow mainRect = reactomeShadowList.get(i);

            for (int j = 0; j < reactomeShadowList.size(); j++) {
                ReactomeShadow otherRect = reactomeShadowList.get(j);

                if (mainRect.getReactomeId() != otherRect.getReactomeId()) {
                    if (otherRect.contains(mainRect)) { // check mainRect is fully inside otherRect
                        mainRect.setInside(true);
                        continue;
                    }

                    /**
                     * if mainRect is fully inside otherRect, do not consider it as an intersection,
                     * otherwise it will be subtracted in the next step.
                     */
                    if (mainRect.intersects(otherRect)) {
                        ReactomeShadow newIntersectRect = new ReactomeShadow(otherRect);
                        mainRect.addIntersection(newIntersectRect);
                    }
                }
            }
        }

        return reactomeShadowList;
    }

    /**
     * Get the available rectangle after subtract, match the one with largest width
     * and store in map. Afterwards set the shadow properties
     * @param reactomeShadowList reactome shadows
     * @return Map reactomeId and Largest Rectangle
     */
    private Map<Long, ReactomeShadow> getAvailableArea(List<ReactomeShadow> reactomeShadowList) {
        Map<Long, ReactomeShadow> availableAreas = new HashMap<>();
        for (ReactomeShadow shape : reactomeShadowList) {
            List<ReactomeShadow> intersections = shape.getIntersections();
            if (intersections != null) {
                Area main = new Area(shape);

                /** Subtract all the intersections **/
                for (ReactomeShadow intersection : intersections) {
                    main.subtract(new Area(intersection));
                }

                ReactomeShadow availableArea;
                if (main.isPolygonal()) {
                    PathIterator pathIterator = main.getPathIterator(null);
                    Polygon polygon = getPolygonFromPathIterator(pathIterator);

                    /** Get x and y coordinates **/
                    int[] xCoords = polygon.xpoints;
                    int[] yCoords = polygon.ypoints;


                    /** Partitioning the Polygon in rectangle strips **/
                    ShapePartition shapePartition = new ShapePartition();

                    /** Return a list of Boxes which contains the Coordinates of each rectangle **/
                    List<Box> boxes = shapePartition.decompose(xCoords, yCoords);

                    /** Get the largest Rectangle. This is generic and return Java Rectangles **/
                    Rectangle bound = getLargestRectangleSimple(boxes);

                    /** Convert the rectangle to ReactomeShadow **/
                    availableArea = new ReactomeShadow(bound);
                    availableArea.setInside(shape.isInside());
                    availableArea.setReactomeId(shape.getReactomeId());
                    availableArea.setIntersections(shape.getIntersections());

                } else {
                    /** It is a rectangle, don't need to use the PathIterator **/
                    Rectangle bound = main.getBounds();

                    /** Convert the rectangle to ReactomeShadow **/
                    availableArea = new ReactomeShadow(bound);
                    availableArea.setInside(shape.isInside());
                    availableArea.setReactomeId(shape.getReactomeId());
                    availableArea.setIntersections(shape.getIntersections());
                }

                availableAreas.put(shape.getReactomeId(), availableArea);

            }
        }

        return availableAreas;
    }

    /**
     * Of a given areas set the shadow properties
     * @param availableAreas Map reactomeId and Largest Rectangle
     */
    private void setShadowsProperties(Map<Long, ReactomeShadow> availableAreas) {
        /** Having the available areas for each shadow, set each shadow.prop properly **/
        for (Shadow shadow : shadows) {
            for (Long aLong : availableAreas.keySet()) {
                if (aLong == shadow.reactomeId) {
                    ReactomeShadow reactomeShadow = availableAreas.get(aLong);
                    if(!isZeroSizeRectangle(reactomeShadow)) {
                        shadow.prop.width = reactomeShadow.width;
                        shadow.prop.height = reactomeShadow.height;
                        shadow.prop.x = reactomeShadow.x;
                        shadow.prop.y = reactomeShadow.y;
                    }
                }
            }
        }
    }

    /**
     * If we do not have any largest box, a zero sized rectangle will be returned.
     * It means we will keep the text in the center
     *
     * @param r Rectangle to be tested
     * @return true if zero sized rectangle
     */
    private boolean isZeroSizeRectangle(ReactomeShadow r){
        return  r.x == 0 && r.y == 0 && r.width == 0 && r.height == 0;
    }

    /**
     * This method covers a few pathways that have the same dimensions or they are very close in dimension. When it
     * happens the one Pathway name overlaps the other. This method identifies these case and rearrange the text.
     *
     * @param shadows
     */
    private void checkOverlappingTexts(Set<Shadow> shadows) {
        final int DIFF = 10;

        Set<Shadow> sameShadows = new HashSet<>();
        for (Shadow shadow : shadows) {
            for (Shadow nextShadow : shadows) {
                if (shadow.reactomeId != nextShadow.reactomeId) {

                    /** Check if shadows are in the same position and have the same size **/
                    if ((shadow.prop.x == nextShadow.prop.x || Math.abs(shadow.prop.x - nextShadow.prop.x) <= DIFF) &&
                            (shadow.prop.y == nextShadow.prop.y || Math.abs(shadow.prop.y - nextShadow.prop.y) <= DIFF) &&
                            (shadow.prop.width == nextShadow.prop.width || Math.abs(shadow.prop.width - nextShadow.prop.width) <= DIFF) &&
                            (shadow.prop.height == nextShadow.prop.height || Math.abs(shadow.prop.height - nextShadow.prop.height) <= DIFF)) {

                        sameShadows.add(shadow);
                        sameShadows.add(nextShadow);
                    }
                }
            }
        }

        if (sameShadows.size() > 1) {
            int divideBy = sameShadows.size();
            int index = 0;

            for (Shadow sameShadow : sameShadows) {
                int maxY = sameShadow.prop.y + sameShadow.prop.height;
                int piece = Math.round(maxY / divideBy);

                if (index == 0) {
                    sameShadow.prop.height = piece;
                } else {
                    sameShadow.prop.y = piece;
                    sameShadow.prop.height = piece;
                }

                index++;

            }
        }
    }

    /**
     * Based on a list of Box coordinates, create rectangles,
     * calculate the area and retrieve the one with largest area.
     *
     * @param boxes strips
     * @return largest rectangle
     */
    private Rectangle getLargestRectangleSimple(List<Box> boxes) {
        /** The noSizeRectangle will be used in case the Largest Rectangle does not fit our minimum requirements W and H **/
        Rectangle noSizeRectangle = new Rectangle(0, 0, 0, 0);;

        /** Hold the rectangles after creating them from the Boxes coordinates **/
        List<Rectangle> rectangles = new LinkedList<>();

        /** Get the boxes with coordinates and create rectangles **/
        for (Box box : boxes) {
            List<XYPoint> xyPoints = box.getXYPoints();

            /** Build the Rectangle based on the Points **/
            Rectangle rect = new Rectangle(new Point(xyPoints.get(0).getX(), xyPoints.get(0).getY()));
            for (int i = 1; i < xyPoints.size(); i++) {
                rect.add(new Point(xyPoints.get(i).getX(), xyPoints.get(i).getY()));
            }

            rectangles.add(rect);
        }

        /** No rectangle is the largest - having 0-sized Rectangle the Text will be placed in the center as is **/
        if (rectangles.size() == 0) {
            return noSizeRectangle;
        }

        /** If there is only one Rectangle this is the one we take **/
        if (rectangles.size() == 1) {
            Rectangle rectangle = rectangles.get(0);
            if(rectangle.height >= MIN_HEIGHT_THRESHOLD && rectangle.width >= MIN_WIDTH_THRESHOLD) {
                return rectangle;
            }
            return noSizeRectangle;
        }

        /** Keep the areas in a map **/
        TreeMap<Integer, Rectangle> areas = new TreeMap<>();

        /** Iterate the rectangles and get the wider one **/
        for (Rectangle currentRectangle : rectangles) {
            /** getting the wider one **/
            int width = currentRectangle.width;
            int height = currentRectangle.height;

            if (height >= MIN_HEIGHT_THRESHOLD && width >= MIN_WIDTH_THRESHOLD) { // ok, apply threshold
                int area = width * height;

                if(areas.containsKey(area)){
                    Rectangle rec = areas.get(area);
                    if(currentRectangle.width > rec.width) {
                        areas.put(area, currentRectangle);
                    }
                }else {
                    areas.put(area, currentRectangle);
                }
            }
        }


        double FACTOR = 1.5;

        if(areas.size() > 0) {

            /** Put the map in reverse order - First one has the largest area **/
            NavigableMap<Integer, Rectangle> reverseMap = areas.descendingMap();

            /** First has possible largest area **/
            Integer area = reverseMap.firstEntry().getKey();
            Rectangle possibleLargestRectangle = reverseMap.firstEntry().getValue();

            /** remove the one with largest area, then we don't need to compare with the same one. **/
            reverseMap.remove(area);

            if(possibleLargestRectangle.width >= possibleLargestRectangle.height){
                /** ok, larger area and wider **/
                return  possibleLargestRectangle;
            }else {
                /** check the in the following area the wider one **/
                for (Integer anotherArea : reverseMap.keySet()) {
                    Rectangle anotherRect = reverseMap.get(anotherArea);
                    /**
                     * ok, wider than the possible largest rectangle.
                     * However we can't proceed with the wider only, otherwise it may retrieve smaller places in the
                     * rectangle just because of its width. Let's apply a FACTOR SCALE just to mock the area a litte bit
                     * if the new area is greater than the other - ok, we have a new wider one having a considerable
                     * free place as well.
                     */
                    if(anotherRect.width > possibleLargestRectangle.width){
                        int newWidth = (int)(anotherRect.width * FACTOR);
                        if((newWidth * anotherRect.height) >= (possibleLargestRectangle.width * possibleLargestRectangle.height)){
                            return anotherRect;
                        }
                    }
                }
            }

            /** ok, applied the FACTOR but the areas are very distinct from each other. Go ahead with the largest area. **/
            return possibleLargestRectangle;
        }

        /** rectangles out of the threshold, we can't touch it **/
        return noSizeRectangle;
    }

    /**
     * Retrieve a Polygon of a given PathIterator
     *
     * @param it segments
     * @return a Polygon
     */
    public static Polygon getPolygonFromPathIterator(PathIterator it) {
        Polygon p = new Polygon();
        float[] points = new float[6];
        while (!it.isDone()) {
            int op = it.currentSegment(points);
            switch (op) {
                case PathIterator.SEG_MOVETO:
                    p.addPoint(Math.round(points[0]), Math.round(points[1]));
                    break;
                case PathIterator.SEG_LINETO:
                    p.addPoint(Math.round(points[0]), Math.round(points[1]));
                    break;
                case PathIterator.SEG_QUADTO:
                    p.addPoint(Math.round(points[0]), Math.round(points[1]));
                    p.addPoint(Math.round(points[2]), Math.round(points[3]));
                    break;
                case PathIterator.SEG_CUBICTO:
                    p.addPoint(Math.round(points[0]), Math.round(points[1]));
                    p.addPoint(Math.round(points[2]), Math.round(points[3]));
                    p.addPoint(Math.round(points[4]), Math.round(points[5]));
                    break;
                case PathIterator.SEG_CLOSE:
                    p.addPoint(Math.round(points[0]), Math.round(points[1]));
                    break;
            }
            it.next();
        }

        return p;
    }

}