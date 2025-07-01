package org.reactome.server.diagram.converter.graph.output;


import org.reactome.server.diagram.converter.graph.query.NodesQueryResult;
import org.reactome.server.diagram.converter.graph.query.QueryResult;

import java.util.List;

/**
 * Projecting PhysicalEntityNodes to GraphNodes
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EntityNode extends GraphNode {

    public List<Long> diagramIds;

    //Next variable will NOT contain value for Complexes and EntitySets because they
    //do not have main resources (members or components are treated separately).
    public String identifier = null;
    public String referenceType = null;
    public List<String> geneNames = null;

    public List<Long> parents = null;
    public List<Long> children = null;


    public EntityNode(QueryResult node, List<Long> diagramIds){
        super(node);
        this.diagramIds = diagramIds;
    }

    public EntityNode(NodesQueryResult node, List<Long> diagramIds){
        super(node);
        this.diagramIds = diagramIds;
        this.referenceType = node.getReferenceType();
        this.identifier = node.getIdentifier();
        this.geneNames = node.getGeneNames();
        this.children = node.getChildren();
        this.parents = node.getParents();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityNode entityNode = (EntityNode) o;

        return !(dbId != null ? !dbId.equals(entityNode.dbId) : entityNode.dbId != null);
    }

    @Override
    public int hashCode() {
        return dbId != null ? dbId.hashCode() : 0;
    }
}
