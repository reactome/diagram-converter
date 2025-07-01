package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.*;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;
import org.reactome.server.graph.domain.model.AbstractModifiedResidue;
import org.reactome.server.graph.domain.model.EntityWithAccessionedSequence;
import org.reactome.server.graph.domain.model.TranslationalModification;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.*;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@DiagramTest
public class T115_NodeAttachmentMismatch extends AbstractConverterQA implements DiagramQA {

    private static final AdvancedDatabaseObjectService ados = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Entities in the diagram that contain different node attachments to those annotated in the database.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.HIGH;
    }

    @Override
    public List<String> getReport() {
        Collections.sort(lines);
        return getReport(lines);
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Reactions,EntityId,EntityName,EntityDiagramId,Status,AttachmentId,AttachmentName,Created,Modified";
    }

    @Override
    public void run(Diagram diagram) {
        if (diagram.getNodes() == null) return; //This scenario is reported in T101_DiagramEmpty

        Map<Long, EntityWithAccessionedSequence> modifiedEwass = getModifiedEntities(diagram);
        for (Node node : diagram.getNodes()) {
            if (node.isFadeOut != null && node.isFadeOut) continue;

            //For every node, the map is filled up with the whole TM so removing is fine since next time will be filled again
            Map<Long, List<TranslationalModification>> expected = getTranslationalModifications(modifiedEwass.get(node.reactomeId));

            List<NodeAttachment> attachExtraList = new ArrayList<>();
            if (node.nodeAttachments != null) {
                Iterator<NodeAttachment> it = node.nodeAttachments.iterator();
                while (it.hasNext()) { //Iterating while possibly removing from the iterated list
                    NodeAttachment attach = it.next();
                    if (attach.reactomeId == null) {
                        attachExtraList.add(attach);
                        it.remove();
                    } else {
                        List<TranslationalModification> l = expected.get(attach.reactomeId);
                        if (l == null || l.remove(0) == null) {
                            attachExtraList.add(attach);
                            it.remove();
                        }
                    }
                }
            }

            for (NodeAttachment attachExtra : attachExtraList)
                report(diagram, node, "EXTRA", attachExtra.reactomeId, attachExtra.description);

            for (List<TranslationalModification> list : expected.values()) {
                for (TranslationalModification attachMissing : list) {
                    report(diagram, node, "MISSING", attachMissing.getDbId(), attachMissing.getDisplayName());
                }
            }
        }
    }

    private Map<Long, List<TranslationalModification>> getTranslationalModifications(EntityWithAccessionedSequence ewas) {
        Map<Long, List<TranslationalModification>> map = new HashMap<>();
        if (ewas != null) {
            for (AbstractModifiedResidue amr : ewas.getHasModifiedResidue()) {
                if (amr instanceof TranslationalModification) {
                    List<TranslationalModification> l = map.getOrDefault(amr.getDbId(), new ArrayList<>());
                    l.add((TranslationalModification) amr);
                    map.put(amr.getDbId(), l);
                }
            }
        }
        return map;
    }

    private Map<Long, EntityWithAccessionedSequence> getModifiedEntities(Diagram diagram) {
        Map<Long, EntityWithAccessionedSequence> rtn = new HashMap<>();
        try {
            String query = "" +
                    "MATCH path=(p:Pathway{stId:$stId})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                    "WHERE SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                    "MATCH (rle)-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator*]->(pe:EntityWithAccessionedSequence) " +
                    "WITH DISTINCT pe " +
                    "MATCH (pe)-[hm:hasModifiedResidue]->(tm:TranslationalModification) " +
                    "RETURN DISTINCT pe, collect(hm), collect(tm) ";
            Map<String, Object> params = new HashMap<>();
            params.put("stId", diagram.getStableId());
            Collection<EntityWithAccessionedSequence> ewass = ados.getCustomQueryResults(EntityWithAccessionedSequence.class, query, params);
            for (EntityWithAccessionedSequence ewas : ewass) {
                rtn.put(ewas.getDbId(), ewas);
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    private String getReactions(Diagram diagram, Node node) {
        Collection<String> rtn = new HashSet<>();
        for (Connector connector : node.connectors) {
            DiagramObject obj = diagram.getDiagramObjectByDiagramId(connector.edgeId);
            if (obj != null) rtn.add(obj.reactomeId.toString());
        }
        return String.join("|", rtn);
    }

    private void report(Diagram diagram, Node node, String status, Long dbId, String displayName) {
        lines.add(String.format("%s,\"%s\",%s,%d,\"%s\",%d,%s,%s,\"%s\",%s",
                diagram.getStableId(),
                diagram.getDisplayName(),
                getReactions(diagram, node),
                node.reactomeId,
                node.displayName,
                node.id,
                status,
                dbId == null ? "undefined" : dbId,
                displayName == null ? "undefined" : displayName,
                TestReportsHelper.getCreatedModified(node.reactomeId)));
    }

}
