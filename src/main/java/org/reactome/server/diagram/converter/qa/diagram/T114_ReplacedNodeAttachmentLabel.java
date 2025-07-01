package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.output.Node;
import org.reactome.server.diagram.converter.layout.output.NodeAttachment;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;
import org.reactome.server.graph.domain.model.AbstractModifiedResidue;
import org.reactome.server.graph.service.DatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@DiagramTest
public class T114_ReplacedNodeAttachmentLabel extends AbstractConverterQA implements DiagramQA {

    private static final DatabaseObjectService dos = ReactomeGraphCore.getService(DatabaseObjectService.class);

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Fixes the node attachments labels based on the database annotation and reports the changes.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.LOW;
    }

    @Override
    public List<String> getReport() {
        Collections.sort(lines);
        return getReport(lines);
    }

    @Override
    protected String getHeader() {
        return "Status,Diagram,DiagramName,EntityId,EntityName,AttachmentId,AttachmentName,CurrentLabel,SuggestedLabel,Created,Modified";
    }

    @Override
    public void run(Diagram diagram) {
        if (diagram.getNodes() == null) return; //This scenario is reported in T101_DiagramEmpty

        for (Node node : diagram.getNodes()) {
            if (node.nodeAttachments != null) {
                for (NodeAttachment attach : node.nodeAttachments) {
                    if (attach.reactomeId != null) {
                        AbstractModifiedResidue tm = dos.findByIdNoRelations(attach.reactomeId);
                        if (tm != null) {
                            attach.description = tm.getDisplayName();
                            String label = tm.fetchSingleValue("getLabel");
                            checkAndReport(diagram, node, attach, label);
                        }
                    }
                }
            }
        }
    }

    private void checkAndReport(Diagram diagram, Node node, NodeAttachment attach, String label) {
        //We treat the 'nulls' below
        if (attach.label != null && label != null && Objects.equals(attach.label, label)) return;

        boolean hadLabel = attach.label != null && !attach.label.isEmpty();
        boolean hasLabel = label != null && !label.isEmpty();

        String status;
        if(hadLabel){
            status = hasLabel ? "Replaced" : "Removed";
        } else {
            status = hasLabel ? "Missing" : "Blank";
        }

        lines.add(String.format("%s,%s,\"%s\",%d,\"%s\",%d,\"%s\",%s,%s,%s",
                status,
                diagram.getStableId(),
                diagram.getDisplayName(),
                node.reactomeId,
                node.displayName,
                attach.reactomeId,
                attach.description,
                attach.label,
                label,
                TestReportsHelper.getCreatedModified(diagram.getDbId())));
        attach.label = label;
    }
}
