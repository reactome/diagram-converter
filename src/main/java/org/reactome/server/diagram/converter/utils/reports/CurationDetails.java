package org.reactome.server.diagram.converter.utils.reports;

import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("WeakerAccess")
public class CurationDetails implements CustomQuery {

    String created;
    String modified;

    @Override
    public String toString() {
        return String.format("\"%s\",\"%s\"", created, modified);
    }

    @Override
    public CustomQuery build(Record r) {
        CurationDetails cd = new CurationDetails();
        cd.created = r.get("created").asString(null);
        cd.modified = r.get("modified").asString(null);
        return cd;
    }
}
