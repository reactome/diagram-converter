package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.output.Node;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.Participant;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@DiagramTest
public class T103_ExtraParticipantInDiagram extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    public String getDescription() {
        return "Participants seen in a diagram that shouldn't be there. " +
                "Mainly the reason is because the reaction where they participate belongs to a subpathway " +
                "that also has its own diagram.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.BLOCKER;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Entity,EntityName,Created,Modified";
    }

    @Override
    public void run(Diagram diagram) {
        Map<Long, Participant> participants = TestReportsHelper.getDiagramParticipants(diagram.getDbId());
        for (Node node : diagram.getNodes()) {
            Participant targetSchemaClass = participants.get(node.reactomeId);
            if (targetSchemaClass == null) {
                if (!"Pathway".equals(node.schemaClass) && !"ProcessNode".equals(node.renderableClass)) {
                    boolean isFadeOut = node.isFadeOut != null && node.isFadeOut;
                    boolean isCrossed = node.isCrossed != null && node.isCrossed;
                    if (!isFadeOut && !isCrossed){
                        add(diagram.getStableId(), diagram.getDisplayName(), node.reactomeId, node.displayName);
                    }
                }
            }
        }
    }

    private void add(String diagramStId, String diagramName, Long entityId, String entityName) {
        lines.add(String.format("%s,\"%s\",%d,\"%s\",%s",
                diagramStId,
                diagramName,
                entityId,
                entityName,
                TestReportsHelper.getCreatedModified(entityId)
        ));
    }
}
