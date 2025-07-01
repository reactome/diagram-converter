package org.reactome.server.diagram.converter.graph.query;

import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

public class QueryResult implements CustomQuery {

    protected Long dbId;
    protected String stId;
    protected String displayName;
    protected String schemaClass;
    protected Long speciesID;

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSchemaClass() {
        return schemaClass;
    }

    public void setSchemaClass(String schemaClass) {
        this.schemaClass = schemaClass;
    }

    public Long getSpeciesID() {
        return speciesID;
    }

    public void setSpeciesID(Long speciesID) {
        this.speciesID = speciesID;
    }

    @Override
    public CustomQuery build(Record r) {
        QueryResult queryResult = new QueryResult();
        queryResult.setDbId(r.get("dbId").asLong(0));
        queryResult.setDisplayName(r.get("displayName").asString(null));
        queryResult.setSchemaClass(r.get("schemaClass").asString(null));
        queryResult.setSpeciesID(r.get("speciesID").asLong(0));
        queryResult.setStId(r.get("stId").asString(null));
        return queryResult;
    }
}
