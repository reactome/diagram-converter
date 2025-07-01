package org.reactome.server.diagram.converter.qa.diagram;

import org.reactome.server.diagram.converter.layout.output.Diagram;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface DiagramQA {

    default void run(Diagram diagram){}

}
