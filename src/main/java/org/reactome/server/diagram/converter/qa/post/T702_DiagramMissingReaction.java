package org.reactome.server.diagram.converter.qa.post;

import org.reactome.server.diagram.converter.graph.output.EventNode;
import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.PostTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@PostTest
public class T702_DiagramMissingReaction extends AbstractConverterQA implements PostQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects diagrams with missing reactions compared to how they are annotated in the database.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.BLOCKER;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Reaction,ReactionName,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    public void run(Diagram diagram, Graph graph) {
        if (graph.getEdges() != null) {
            for (EventNode eventNode : graph.getEdges()) {
                if (!diagram.containsEdge(eventNode.dbId) || diagram.getEdges(eventNode.dbId).isEmpty()) {
                    lines.add(String.format("%s,\"%s\",%s,\"%s\",%s",
                            diagram.getStableId(),
                            diagram.getDisplayName(),
                            eventNode.getStId(),
                            eventNode.getDisplayName(),
                            TestReportsHelper.getCreatedModified(eventNode.getDbId())
                    ));
                }
            }
        }
    }
}
