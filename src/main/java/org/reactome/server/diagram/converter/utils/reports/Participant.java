package org.reactome.server.diagram.converter.utils.reports;

import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("WeakerAccess")
public class Participant implements CustomQuery {
    Long dbId;
    String schemaClass;
    String renderableClass;

    public Long getDbId() {
        return dbId;
    }

    public String getSchemaClass() {
        return schemaClass;
    }

    public String getRenderableClass() {
        return renderableClass;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public void setSchemaClass(String schemaClass) {
        this.schemaClass = schemaClass;
    }

    public void setRenderableClass(String renderableClass) {
        this.renderableClass = renderableClass;
    }

    @Override
    public CustomQuery build(Record r) {
        Participant p = new Participant();
        p.setDbId(r.get("dbId").asLong(0));
        p.setSchemaClass(r.get("schemaClass").asString(null));
        p.setRenderableClass(r.get("renderableClass").asString(null));
        return p;
    }
}
