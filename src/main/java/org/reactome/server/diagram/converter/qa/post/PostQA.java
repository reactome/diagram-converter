package org.reactome.server.diagram.converter.qa.post;

import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.output.Diagram;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public interface PostQA {

    default void run(Diagram diagram, Graph graph){}
    
}
