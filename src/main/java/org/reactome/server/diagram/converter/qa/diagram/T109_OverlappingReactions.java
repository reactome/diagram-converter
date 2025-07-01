package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.output.Edge;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@DiagramTest
public class T109_OverlappingReactions extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects pairs of reactions which main shapes physically overlap in the diagram.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.HIGH;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Reaction1,Reaction1_Name,Reaction2,Reaction2_Name,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    public void run(Diagram diagram) {
        for (Edge edge1 : diagram.getEdges()) {
            for (Edge edge2 : diagram.getEdges()) {
                boolean edge1Fadeout = edge1.isFadeOut != null && edge1.isFadeOut;
                boolean edge2Fadeout = edge2.isFadeOut != null && edge2.isFadeOut;
                if (edge1.reactomeId < edge2.reactomeId && !edge1Fadeout && !edge2Fadeout) {
                    if (edge1.reactionShape.overlaps(edge2.reactionShape)) {
                        lines.add(String.format("%s,\"%s\",%s,\"%s\",%s,\"%s\",%s",
                                diagram.getStableId(),
                                diagram.getDisplayName(),
                                edge1.reactomeId,
                                edge1.displayName,
                                edge2.reactomeId,
                                edge2.displayName,
                                TestReportsHelper.getCreatedModified(edge2.reactomeId)));
                    }
                }
            }
        }
    }
}
