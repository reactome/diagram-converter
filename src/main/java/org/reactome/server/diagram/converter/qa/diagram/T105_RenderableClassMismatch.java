package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.ConverterReport;
import org.reactome.server.diagram.converter.utils.reports.TestReportsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("unused")
@ConverterReport
public class T105_RenderableClassMismatch extends AbstractConverterQA implements DiagramQA {

    private static final List<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "Detects diagrams containing entities with a mismatch in their annotated RenderableClass.";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.MEDIUM;
    }

    @Override
    protected String getHeader() {
        return "Diagram,DiagramName,Entity,EntitySchemaClass,EntityName,RenderableClass,SuggestedRenderableClass,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    public static void add(String diagramStId, String diagramName, Long entityId, String schemaClass, String entityName,
                           String wrongRC, String rightRC) {
        lines.add(String.format("%s,\"%s\",%d,%s,\"%s\",%s,%s,%s",
                diagramStId,
                diagramName,
                entityId,
                schemaClass,
                entityName,
                wrongRC,
                rightRC,
                TestReportsHelper.getCreatedModified(entityId)
        ));
    }

}
