package org.reactome.server.diagram.converter.graph.output;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Just a container in order to serialise an object containing a collection rather
 * than directly the collection
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Graph {

    private Long dbId;
    private String stId;
    private transient String displayName;
    private String speciesName;

    private Map<Long, EntityNode> nodes = new HashMap<>();
    private Map<Long, EventNode> edges = new HashMap<>();

    private Collection<SubpathwayNode> subpathways;

    public Graph(
            Long dbId, String stId, String displayName, String speciesName,
            Collection<EntityNode> nodes, Collection<EventNode> edges, Collection<SubpathwayNode> subpathways
    ) {
        this.dbId = dbId;
        this.stId = stId;
        this.displayName = displayName;
        this.speciesName = speciesName;
        if (nodes != null) {
            for (EntityNode node : nodes) {
                this.nodes.put(node.dbId, node);
            }
        }
        if (edges != null) {
            for (EventNode edge : edges) {
                this.edges.put(edge.dbId, edge);
            }
        }
        this.subpathways = subpathways;
    }

    public Long getDbId() {
        return dbId;
    }

    public String getStId() {
        return stId;
    }

    @JsonIgnore
    public String getDisplayName() {
        return displayName;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    @JsonIgnore
    public EntityNode getNode(Long dbId) {
        return nodes.get(dbId);
    }

    public Collection<EntityNode> getNodes() {
        return nodes.values();
    }

    public boolean containsEdge(Long dbId) {
        return edges.containsKey(dbId);
    }

    public Collection<EventNode> getEdges() {
        return edges.values();
    }

    public Collection<SubpathwayNode> getSubpathways() {
        return subpathways;
    }
}
