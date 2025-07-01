package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.ConverterReport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@ConverterReport
public class T102_MissingSchemaClass extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects diagrams containing entities without a SchemaClass.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.BLOCKER;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Missing";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    public static void add(String diagramStId, String diagramName, Long entityId){
        lines.add(String.format("%s,\"%s\",%d",
                diagramStId,
                diagramName,
                entityId
        ));
    }

}
