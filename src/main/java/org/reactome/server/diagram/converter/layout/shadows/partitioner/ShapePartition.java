package org.reactome.server.diagram.converter.layout.shadows.partitioner;

import java.util.LinkedList;
import java.util.List;

public class ShapePartition {
    static XYNodeList polygon;
    static XYNodeSearchTree eventQ;
    static int rectCnt;

    public ShapePartition() {
    }

    public List<Box> decompose(int[] xCoords, int[] yCoords) {
        polygon = new XYNodeList();

        for (int i = 0; i < xCoords.length; i++) {
            polygon.insert(xCoords[i], yCoords[i]);
        }

        polygon.connectHeadAndRear();

        constructEventQueue(polygon);
        msg("Going Through eventQ ...");
        XYNodeSortedList statusStructT1 = new XYNodeSortedList();
        Horizontal horizontalStruct1 = new Horizontal();

        List<Box> boxes = new LinkedList<>();
        while (!eventQ.isEmpty()) {
            XYTNode t1 = eventQ.pop();
            msg("=> (" + t1.getX() + ", " + t1.getY() + ")");

            Box box = processIncidentEdges(statusStructT1, horizontalStruct1, t1.getXYnode());
            msg("--------------------");

            if (box != null) {
                boxes.add(box);
            }
        }

        return boxes;
    }

    public void constructEventQueue(XYNodeList polygon) {
        eventQ = new XYNodeSearchTree(polygon);
        polygon.setIterator();

        while (!polygon.isEmptyIterator()) {
            eventQ.insert(polygon.getIterator().getX(), polygon.getIterator().getY());
            polygon.nextIterator();
        }

        eventQ.insert(polygon.getIterator().getX(), polygon.getIterator().getY());
    }

    public Box processIncidentEdges(XYNodeSortedList statusStructT, Horizontal horizontalStruct, XYNode n) {
        Box box = null;

        int rightNeighbor;
        int edge1X;
        if (n.getX() == n.getPrev().getX() && n.getY() > n.getPrev().getY()) {
            rightNeighbor = n.getPrev().getX();
            edge1X = n.getPrev().getY();
            statusStructT.insert(rightNeighbor, edge1X);
        }

        if (n.getX() == n.getNext().getX() && n.getY() > n.getNext().getY()) {
            rightNeighbor = n.getX();
            edge1X = n.getY();
            statusStructT.insert(rightNeighbor, edge1X);
            XYNode edge1Y = statusStructT.getLeftNeighbor(polygon, n.getX(), n.getY());
            XYNode edge2X = statusStructT.getRightNeighbor(polygon, n.getX(), n.getY());
            if (edge1Y != null && edge2X != null) {
                boolean edge2Y = horizontalStruct.isConnected(edge1Y.getNext().getX(), edge1Y.getNext().getY(), edge2X.getX(), edge2X.getY());
                int isNewRect = edge1Y.getX();
                int edge1Y1 = edge1Y.getY();
                int edge2X1 = edge2X.getX();
                int edge2Y1 = edge2X.getY();
                if (edge2Y) {
                    //Box box = new Box();
                    box = new Box();
                    box.addPoint(new XYPoint(isNewRect, n.getY()));
                    box.addPoint(new XYPoint(isNewRect, edge2Y1));
                    box.addPoint(new XYPoint(edge2X1, edge2Y1));
                    box.addPoint(new XYPoint(edge2X1, n.getY()));
                    //ret.add(box);

                    ++rectCnt;
                    msg("==== 1 Rectangle: " + rectCnt + " ====");
                    msg("  " + isNewRect + ", " + n.getY());
                    msg("  " + isNewRect + ", " + edge2Y1);
                    msg("  " + edge2X1 + ", " + edge2Y1);
                    msg("  " + edge2X1 + ", " + n.getY());
                    msg("==== 1 EndRectangle ====");

                    horizontalStruct.insert(n.getY(), isNewRect, edge2X1);
                    if (n.getY() == edge1Y1 && n.getY() == edge2X.getNext().getY()) {
                        polygon.modify(
                                edge1Y.getPrev().getX(), edge1Y.getPrev().getY(),
                                edge2X.getNext().getNext().getX(), edge2X.getNext().getNext().getY()
                        );
                    } else if (n.getY() == edge1Y1 && n.getY() > edge2X.getNext().getY()) {
                        polygon.modify(
                                edge1Y.getPrev().getX(), edge1Y.getPrev().getY(),
                                edge2X1, n.getY(), edge2X.getNext().getX(), edge2X.getNext().getY());
                    } else if (n.getY() > edge1Y1 && n.getY() == edge2X.getNext().getY()) {
                        polygon.modify(
                                isNewRect, edge1Y1, isNewRect, n.getY(),
                                edge2X.getNext().getNext().getX(), edge2X.getNext().getNext().getY());
                    } else if (n.getY() > edge1Y1 && n.getY() > edge2X.getNext().getY()) {
                        polygon.modify(
                                isNewRect, edge1Y1, isNewRect, n.getY(),
                                edge2X1, n.getY(), edge2X.getNext().getX(), edge2X.getNext().getY());
                    }

                    statusStructT.remove(edge2X1, edge2Y1);
                    if (n.getY() > edge2X.getNext().getY()) {
                        statusStructT.insert(edge2X1, n.getY());
                    }

                    if (n.getY() == edge1Y.getY()) {
                        statusStructT.remove(isNewRect, edge1Y1);
                    }
                }
            }
        }

        int edge1Y2;
        if (n.getY() == n.getNext().getY() && n.getX() < n.getNext().getX()) {
            rightNeighbor = n.getY();
            edge1X = n.getX();
            edge1Y2 = n.getNext().getX();
            horizontalStruct.insert(rightNeighbor, edge1X, edge1Y2);
        }

        XYNode rightNeighbor1;
        int edge2X2;
        int edge2Y2;
        boolean isNewRect1;
        if (n.getX() == n.getPrev().getX() && n.getY() < n.getPrev().getY()) {
            rightNeighbor1 = statusStructT.getLeftNeighbor(polygon, n.getPrev().getX(), n.getPrev().getY());
            edge2X2 = n.getPrev().getX();
            edge2Y2 = n.getPrev().getY();
            if (rightNeighbor1 != null) {
                edge1X = rightNeighbor1.getX();
                edge1Y2 = rightNeighbor1.getY();
                isNewRect1 = horizontalStruct.isConnected(
                        rightNeighbor1.getNext().getX(),
                        rightNeighbor1.getNext().getY(),
                        n.getPrev().getX(),
                        n.getPrev().getY()
                );
                if (isNewRect1) {
                    //Box box = new Box();
                    box = new Box();
                    box.addPoint(new XYPoint(rightNeighbor1.getX(), n.getY()));
                    box.addPoint(new XYPoint(rightNeighbor1.getX(), n.getPrev().getY()));
                    box.addPoint(new XYPoint(n.getPrev().getX(), n.getPrev().getY()));
                    box.addPoint(new XYPoint(n.getX(), n.getY()));
                    //ret.add(box);

                    ++rectCnt;
                    msg("==== 2 Rectangle: " + rectCnt + " ====");
                    msg("  " + rightNeighbor1.getX() + ", " + n.getY());
                    msg("  " + rightNeighbor1.getX() + ", " + n.getPrev().getY());
                    msg("  " + n.getPrev().getX() + ", " + n.getPrev().getY());
                    msg("  " + n.getX() + ", " + n.getY());
                    msg("==== 2 EndRectangle ====");
                    horizontalStruct.insert(n.getY(), rightNeighbor1.getX(), n.getX());
                    if (rightNeighbor1.getY() < n.getY()) {
                        polygon.modify(rightNeighbor1.getX(), rightNeighbor1.getY(), rightNeighbor1.getX(), n.getY(), n.getX(), n.getY());
                    } else if (rightNeighbor1.getY() == n.getY()) {
                        statusStructT.remove(edge1X, edge1Y2);
                    }
                }
            }

            statusStructT.remove(edge2X2, edge2Y2);
        }

        if (n.getX() == n.getNext().getX() && n.getY() < n.getNext().getY()) {
            rightNeighbor1 = statusStructT.getRightNeighbor(polygon, n.getX(), n.getY());
            edge1X = n.getX();
            edge1Y2 = n.getY();
            if (rightNeighbor1 != null) {
                edge2X2 = rightNeighbor1.getX();
                edge2Y2 = rightNeighbor1.getY();
                isNewRect1 = horizontalStruct.isConnected(n.getNext().getX(), n.getNext().getY(), edge2X2, edge2Y2);
                if (isNewRect1) {
                    ++rectCnt;
                    msg("==== 3 Rectangle: " + rectCnt + " ====");
                    msg("  " + edge1X + ", " + edge1Y2);
                    msg("  " + edge1X + ", " + edge2Y2);
                    msg("  " + edge2X2 + ", " + edge2Y2);
                    msg("  " + edge2X2 + ", " + edge1Y2);
                    msg("==== 3 EndRectangle ====");


                    box = new Box();
                    box.addPoint(new XYPoint(edge1X, edge1Y2));
                    box.addPoint(new XYPoint(edge1X, edge2Y2));
                    box.addPoint(new XYPoint(edge2X2, edge2Y2));
                    box.addPoint(new XYPoint(edge2X2, edge1Y2));


                    horizontalStruct.insert(edge1Y2, edge1X, edge2X2);
                    if (rightNeighbor1.getNext().getY() < edge1Y2) {
                        polygon.modify(edge1X, edge1Y2, edge2X2, edge1Y2, rightNeighbor1.getNext().getX(), rightNeighbor1.getNext().getY());
                        statusStructT.remove(edge2X2, edge2Y2);
                        statusStructT.insert(edge2X2, edge1Y2);
                    } else if (rightNeighbor1.getNext().getY() == edge1Y2) {
                        polygon.modify(edge1X, edge1Y2, edge2X2, edge1Y2);
                        statusStructT.remove(edge2X2, edge2Y2);
                    }
                }
            }

            statusStructT.remove(edge1X, edge1Y2);
        }

        return box;

    }

    public void msg(Object o) {
        o = new Object();
        o.toString();
        //System.out.println(o);
    }

}
