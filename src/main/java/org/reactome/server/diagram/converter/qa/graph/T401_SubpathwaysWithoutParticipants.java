package org.reactome.server.diagram.converter.qa.graph;

import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.GraphTest;
import org.reactome.server.diagram.converter.utils.reports.SubpathwayDetails;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@GraphTest
public class T401_SubpathwaysWithoutParticipants extends AbstractConverterQA implements GraphQA {

    private static final AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects subpathways that include no diagram participants.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.BLOCKER;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Subpathway,SubpathwayName,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    public void run(Graph graph) {
        String query = "" +
                "MATCH path=(p:Pathway{stId:$stId})-[:hasEvent*]->(s:Event) " +
                "WHERE NONE(x IN NODES(path) WHERE (x:ReactionLikeEvent)) AND NONE(x IN TAIL(NODES(path)) WHERE x.hasDiagram) " +
                "WITH DISTINCT s " +
                "OPTIONAL MATCH path=(s)-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                "WHERE NONE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                "WITH DISTINCT s, SIZE(COLLECT(DISTINCT rle)) AS rles " +
                "WHERE rles = 0 " +
                "OPTIONAL MATCH (a)-[:created]->(s) " +
                "OPTIONAL MATCH (m)-[:modified]->(s) " +
                "RETURN DISTINCT s.stId AS stId, s.displayName AS displayName, a.displayName AS created, m.displayName AS modified";
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("stId", graph.getStId());
        try {
            for (SubpathwayDetails sp : ads.getCustomQueryResults(SubpathwayDetails.class, query, parametersMap)) {
                lines.add(String.format("%s,\"%s\",%s,\"%s\",\"%s\",\"%s\"",
                        graph.getStId(),
                        graph.getDisplayName(),
                        sp.getStId(),
                        sp.getDisplayName(),
                        sp.getCreated(),
                        sp.getModified()
                ));
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
    }
}
