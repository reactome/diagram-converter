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
public class T106_SchemaClassMismatch extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "SchemaClass of a diagram entity does not match the one stored in the database.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.HIGH;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Entity,EntityName,WrongSchemaClass,RightSchemaClass,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    public static void add(String diagram, String diagramName, Long entity, String entityName, String wrongSchemaClass, String rightSchemaClass){
        lines.add(String.format("%s,\"%s\",%s,\"%s\",%s,%s,%s",
                diagram,
                diagramName,
                entity,
                entityName,
                wrongSchemaClass,
                rightSchemaClass,
                TestReportsHelper.getCreatedModified(entity)));
    }
}
