package org.reactome.server.diagram.converter.qa.graph;

import org.reactome.server.diagram.converter.graph.output.Graph;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface GraphQA {

    default void run(Graph graph){};

}
