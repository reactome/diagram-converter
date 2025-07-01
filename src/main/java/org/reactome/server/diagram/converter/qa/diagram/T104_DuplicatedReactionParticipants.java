package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.ConverterReport;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@ConverterReport
public class T104_DuplicatedReactionParticipants extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects diagram reactions with duplicate inputs, outputs, catalysts, activators, inhibitors pointing to the same diagram entity.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.BLOCKER;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Reaction,Participant,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    public static void add(String diagramStId, String diagramName, Long reaction, Long participant) {
        lines.add(String.format("%s,\"%s\",%d,%d,%s",
                diagramStId,
                diagramName,
                reaction,
                participant,
                TestReportsHelper.getCreatedModified(reaction)
        ));
    }
}
