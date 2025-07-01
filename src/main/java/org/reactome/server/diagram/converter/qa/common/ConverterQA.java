package org.reactome.server.diagram.converter.qa.common;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface ConverterQA {

    String getName();

    String getNumeratedName();

    String getDescription();

    QAPriority getPriority();

    List<String> getReport();
}
