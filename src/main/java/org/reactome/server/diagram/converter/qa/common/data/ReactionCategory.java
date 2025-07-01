package org.reactome.server.diagram.converter.qa.common.data;

import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ReactionCategory implements CustomQuery {

    //The following two fields are automatically populated
    private Long dbId;
    private String category;

    public Long getDbId() {
        return dbId;
    }

    public Category getCategory() {
        return Category.getShapeType(category);
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public CustomQuery build(Record r) {
        ReactionCategory rc = new ReactionCategory();
        rc.setDbId(r.get("dbId").asLong(0));
        rc.setCategory(r.get("category").asString(null));
        return rc;
    }
}
