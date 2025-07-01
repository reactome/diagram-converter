package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.Connector;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.output.Edge;
import org.reactome.server.diagram.converter.layout.output.Node;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;
import org.reactome.server.graph.domain.model.ReactionLikeEvent;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.service.helper.StoichiometryObject;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.*;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@DiagramTest
public class T116_StoichiometryMismatch extends AbstractConverterQA implements DiagramQA {

    private static final AdvancedDatabaseObjectService ados = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Reaction participants where stoichiometry is different to the annotated in the database.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.HIGH;
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Reaction,ReactionName,EntityId,EntityName,Role,CurrentStoichiometry,ExpectedStoichiometry,Created,Modified";
    }

    @Override
    public void run(Diagram diagram) {
        if (diagram.getNodes() == null) return; //This is reported in T101_DiagramEmpty

        Map<Long, Map<Connector.Type, Map<Long, Integer>>> rxns = getReactionParticipans(diagram);

        for (Node node : diagram.getNodes()) {
            if (node.isFadeOut != null && node.isFadeOut || node.isCrossed != null && node.isCrossed) continue;

            for (Connector connector : node.connectors) {
                Edge edge = (Edge) diagram.getDiagramObjectByDiagramId(connector.edgeId);
                if (edge.isFadeOut != null && edge.isFadeOut) continue;

                int expected = 0;
                switch (connector.type) {
                    case INPUT:
                    case OUTPUT:
                        Map<Connector.Type, Map<Long, Integer>> rle = rxns.get(edge.reactomeId);
                        if(rle == null) continue; //This is reported in T702_DiagramMissingReaction
                        Map<Long, Integer> participants = rle.get(connector.type);
                        try {
                            expected = participants.get(node.reactomeId);
                        } catch (NullPointerException e) {
                            T117_ParticipantWrongRole.add(diagram, edge, node, connector.type);
                            continue;
                        }
                        break;
                    case CATALYST:
                    case ACTIVATOR:
                    case INHIBITOR:
                        expected = 1;
                        break;
                }
                int current = connector.stoichiometry == null ? 1 : connector.stoichiometry.value;
                if (!Objects.equals(current, expected)) {
                    report(diagram, edge, node, connector.type, current, expected);
                }
            }
        }
    }


    //Map<Reaction, Map<Type, Map<PhysicalEntity, Stoichiometry>>>
    private Map<Long, Map<Connector.Type, Map<Long, Integer>>> getReactionParticipans(Diagram diagram) {
        Map<Long, Map<Connector.Type, Map<Long, Integer>>> map = new HashMap<>();
        try {
            String query = "" +
                    "MATCH path=(p:Pathway{stId:$stId})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                    "WHERE SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                    "WITH DISTINCT rle " +
                    "MATCH part=(rle)-[s:input|output]->(pe:PhysicalEntity) " +
                    "RETURN DISTINCT rle, collect(s), collect(pe)";

            Map<String, Object> params = new HashMap<>();
            params.put("stId", diagram.getStableId());
            for (ReactionLikeEvent rle : ados.getCustomQueryResults(ReactionLikeEvent.class, query, params)) {
                Map<Long, Integer> inputs = new HashMap<>();
                for (StoichiometryObject input : rle.fetchInput()) {
                    inputs.put(input.getObject().getDbId(), input.getStoichiometry());
                }

                Map<Long, Integer> outputs = new HashMap<>();
                for (StoichiometryObject output : rle.fetchOutput()) {
                    outputs.put(output.getObject().getDbId(), output.getStoichiometry());
                }

                Map<Connector.Type, Map<Long, Integer>> participants = new HashMap<>();
                if (!outputs.isEmpty()) participants.put(Connector.Type.OUTPUT, outputs);
                if (!inputs.isEmpty()) participants.put(Connector.Type.INPUT, inputs);
                map.put(rle.getDbId(), participants);
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return map;
    }


    private void report(Diagram diagram, Edge edge, Node node, Connector.Type role, int current, int expected) {
        lines.add(String.format("%s,\"%s\",%d,\"%s\",%d,\"%s\",%s,%d,%d,%s",
                diagram.getStableId(),
                diagram.getDisplayName(),
                edge.reactomeId,
                edge.displayName,
                node.reactomeId,
                node.displayName,
                role,
                current,
                expected,
                TestReportsHelper.getCreatedModified(edge.reactomeId)));
    }

}
