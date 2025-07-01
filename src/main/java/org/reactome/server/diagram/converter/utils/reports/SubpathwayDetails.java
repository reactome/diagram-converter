package org.reactome.server.diagram.converter.utils.reports;

import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("WeakerAccess")
public class SubpathwayDetails implements CustomQuery {

    String stId;
    String displayName;
    String created;
    String modified;

    public String getStId() {
        return stId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    @Override
    public CustomQuery build(Record r) {
        SubpathwayDetails spd = new SubpathwayDetails();
        spd.setStId(r.get("stId").asString(null));
        spd.setDisplayName(r.get("displayName").asString(null));
        spd.setModified(r.get("modified").asString(null));
        spd.setCreated(r.get("created").asString(null));
        return spd;
    }
}
