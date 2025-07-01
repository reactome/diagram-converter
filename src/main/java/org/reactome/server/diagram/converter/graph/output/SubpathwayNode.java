package org.reactome.server.diagram.converter.graph.output;

import org.reactome.server.diagram.converter.graph.query.SubpathwaysQueryResult;

import java.util.Collection;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class SubpathwayNode implements Comparable<SubpathwayNode> {

    public Long dbId;
    public String stId;
    public String displayName;
    public Collection<Long> events; //These are events dbid

    //Level will be used for the shadowing calculation ONLY on the server side
    //That's the reason why it doesn't get serialised to json but is kept here
    public transient int level;

    public SubpathwayNode(SubpathwaysQueryResult node) {
        this.dbId = node.getDbId();
        this.stId = node.getStId();
        this.displayName = node.getDisplayName();
        this.events = node.getEvents();
        this.level = node.getLevel();
    }

    @Override
    public int compareTo(SubpathwayNode o) {
        return this.dbId.compareTo(o.dbId);
    }
}
