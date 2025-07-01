package org.reactome.server.diagram.converter.graph.output;

import org.reactome.server.diagram.converter.graph.query.QueryResult;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class GraphNode {

    public Long dbId;
    public String stId;
    public String displayName;
    public String schemaClass;
    public Long speciesId;

    public GraphNode(QueryResult node){
        this.dbId = node.getDbId();
        this.stId = node.getStId();
        this.displayName = node.getDisplayName();
        this.schemaClass = node.getSchemaClass();
        this.speciesId = node.getSpeciesID();
    }
}
