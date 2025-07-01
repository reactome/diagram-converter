package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@DiagramTest
public class T101_DiagramEmpty extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects diagrams without any nodes inside them.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.BLOCKER;
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Created,Modified";
    }

    @Override
    public void run(Diagram diagram) {
        if (diagram.getNodes() == null || diagram.getNodes().isEmpty()) {
            lines.add(String.format("%s,\"%s\",%s",
                    diagram.getStableId(),
                    diagram.getDisplayName(),
                    TestReportsHelper.getCreatedModified(diagram.getDbId())));
        }
    }
}
