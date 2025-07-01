package org.reactome.server.diagram.converter.graph.query;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.util.List;

public class SubpathwaysQueryResult implements CustomQuery {

    private Long dbId;
    private String stId;
    private String displayName;
    private List<Long> events;
    private Integer level;

    public Long getDbId() {
        return dbId;
    }

    public String getStId() {
        return stId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<Long> getEvents() {
        return events;
    }

    public Integer getLevel() {
        return level;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEvents(List<Long> events) {
        this.events = events;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public CustomQuery build(Record r) {
        SubpathwaysQueryResult subpathwaysQueryResult = new SubpathwaysQueryResult();
        subpathwaysQueryResult.setDbId(r.get("dbId").asLong(0));
        subpathwaysQueryResult.setStId(r.get("stId").asString(null));
        subpathwaysQueryResult.setDisplayName(r.get("displayName").asString(null));
        subpathwaysQueryResult.setLevel(r.get("level").asInt(0));
        subpathwaysQueryResult.setEvents(r.get("events").asList(Value::asLong));
        return subpathwaysQueryResult;
    }
}
