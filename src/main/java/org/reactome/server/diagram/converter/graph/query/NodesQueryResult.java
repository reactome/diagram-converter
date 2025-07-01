package org.reactome.server.diagram.converter.graph.query;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.util.List;

@SuppressWarnings("unused")
public class NodesQueryResult extends QueryResult implements CustomQuery {

    private List<Long> children;
    private List<Long> parents;
    private String identifier;
    private String referenceType;
    private List<String> geneNames;

    public List<Long> getChildren() {
        return children;
    }

    public void setChildren(List<Long> children) {
        this.children = children;
    }

    public List<Long> getParents() {
        return parents;
    }

    public void setParents(List<Long> parents) {
        this.parents = parents;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getGeneNames() {
        return geneNames;
    }

    public void setGeneNames(List<String> geneNames) {
        this.geneNames = geneNames;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    @Override
    public CustomQuery build(Record r) {
        NodesQueryResult nodesQueryResult = new NodesQueryResult();
        nodesQueryResult.setDbId(r.get("dbId").asLong(0));
        nodesQueryResult.setDisplayName(r.get("displayName").asString(null));
        nodesQueryResult.setSchemaClass(r.get("schemaClass").asString(null));
        nodesQueryResult.setSpeciesID(r.get("speciesID").asLong(0));
        nodesQueryResult.setStId(r.get("stId").asString(null));
        nodesQueryResult.setIdentifier(r.get("identifier").asString(null));
        nodesQueryResult.setReferenceType(r.get("referenceType").asString(null));

        if (!r.get("children").isNull()) nodesQueryResult.setChildren(r.get("children").asList(Value::asLong));
        if (!r.get("parents").isNull()) nodesQueryResult.setParents(r.get("parents").asList(Value::asLong));
        if (!r.get("geneNames").isNull()) nodesQueryResult.setGeneNames(r.get("geneNames").asList(Value::asString));

        return nodesQueryResult;
    }
}
