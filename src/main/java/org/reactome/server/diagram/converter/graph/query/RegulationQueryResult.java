package org.reactome.server.diagram.converter.graph.query;

import org.neo4j.driver.Value;

@SuppressWarnings("unused")
public class RegulationQueryResult {

    private Long dbId;
    private String type;

    public Long getDbId() {
        return dbId;
    }

    public String getType() {
        return type;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static RegulationQueryResult build(Value v) {
        RegulationQueryResult regulationQueryResult = new RegulationQueryResult();
        regulationQueryResult.setDbId(v.get("dbId").asLong());
        regulationQueryResult.setType(v.get("type").asString(null));
        return regulationQueryResult;
    }
}
