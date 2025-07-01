package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.*;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DiagramTest
public class T108_IsolatedGlyphs extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Iterates over all Edges and Links and identifies the isolated glyphs. " +
                "The method can also apply a fix by removing this glyph from the list of Nodes.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.LOW;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,PhysicalEntity,PhysicalEntityName,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    public void run(Diagram diagram) {
        // proceed only if nodes and edges are not null
        if (diagram.getNodes() == null || diagram.getEdges() == null) {
            return;
        }

        // generate a map of all nodes excluding the ProcessNodes (ugly green boxes)
        HashMap<Long, Node> nodesMap = new HashMap<>();
        for (NodeCommon node : diagram.getNodes()) {
            if (!node.renderableClass.equals("ProcessNode") && node.isFadeOut == null) {
                nodesMap.put(node.id, (Node) node);
            }
        }

        // iterate over all Edges and check their ReactionParts
        for (Edge edge : diagram.getEdges()) {
            filterNodes(nodesMap, edge.inputs);
            filterNodes(nodesMap, edge.outputs);
            filterNodes(nodesMap, edge.catalysts);
            filterNodes(nodesMap, edge.activators);
            filterNodes(nodesMap, edge.inhibitors);
        }

        // iterate over all Links and check their ReactionParts
        for (Link link : diagram.getLinks()) {
            filterNodes(nodesMap, link.inputs);
            filterNodes(nodesMap, link.outputs);
        }

        if (nodesMap.size() > 0) {
            for (Node node : nodesMap.values()) {
                diagram.removeNode(node);
                lines.add(String.format("%s,\"%s\",%s,\"%s\",%s",
                        diagram.getStableId(),
                        diagram.getDisplayName(),
                        node.reactomeId,
                        node.displayName,
                        TestReportsHelper.getCreatedModified(diagram.getDbId())));
            }
        }
    }


    private void filterNodes(HashMap<Long, Node> nodesMap, List<ReactionPart> reactionPartList) {
        if (reactionPartList != null) {
            for (ReactionPart reactionPart : reactionPartList) {
                nodesMap.remove(reactionPart.id);
            }
        }
    }

}
