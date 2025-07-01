package org.reactome.server.diagram.converter.graph;

import org.reactome.server.diagram.converter.graph.output.EntityNode;
import org.reactome.server.diagram.converter.graph.output.EventNode;
import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.graph.output.SubpathwayNode;
import org.reactome.server.diagram.converter.graph.query.EdgesQueryResult;
import org.reactome.server.diagram.converter.graph.query.NodesQueryResult;
import org.reactome.server.diagram.converter.graph.query.QueryResult;
import org.reactome.server.diagram.converter.graph.query.SubpathwaysQueryResult;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.output.Node;
import org.reactome.server.diagram.converter.qa.diagram.T105_RenderableClassMismatch;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.*;

/**
 * For a given list of nodes contained in a diagram, this class produces a graph with
 * the underlying physical entities and their children. This information will be sent
 * to the client in a second batch so the graph can be kept
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramGraphFactory {

    private static final AdvancedDatabaseObjectService aos = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    public Graph getGraph(Diagram diagram) {
        return new Graph(diagram.getDbId(),
                diagram.getStableId(),
                diagram.getDisplayName(),
                diagram.getSpeciesName(),
                getGraphNodes(diagram),
                getGraphEdges(diagram),
                getSubpathways(diagram));
    }

    //The buffer is used in building time to avoid querying/decomposition of those previously processed
    private Map<Long, EntityNode> entityNodeMap = new HashMap<>();

    public Map<Long, EntityNode> getEntityNodeMap() {
        return entityNodeMap;
    }

    private Set<EntityNode> getGraphNodes(Diagram diagram) {
        entityNodeMap.clear();
        Set<EntityNode> rtn = new HashSet<>();

        Collection<Long> processNodes = getProcessNodes(diagram.getDbId());
        Collection<Long> structuresDrug = getStructuresDrug(diagram.getDbId()); //So far these are Complexes or EntitySets
        List<Long> greenboxes = new ArrayList<>();
        for (Node node : diagram.getNodes()) {
            if (node.renderableClass.equals("ProcessNode")) {
                greenboxes.add(node.reactomeId);
                // Changing the renderableClass here won't alter the RenderableClassMismatch test result since
                // it has already been executed at this point
                if (!processNodes.contains(node.reactomeId)) node.renderableClass = "EncapsulatedNode";
            }

            if (structuresDrug.contains(node.reactomeId) && !node.renderableClass.endsWith("Drug")) {
                String rightRC = node.renderableClass + "Drug";
                T105_RenderableClassMismatch.add(
                        diagram.getStableId(), diagram.getDisplayName(),
                        node.reactomeId, node.schemaClass, node.displayName, node.renderableClass,
                        rightRC
                );
                node.renderableClass = rightRC;
            }
        }

        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("list", greenboxes);
        String query = "" +
                "MATCH (p:Pathway)-[:species]->(s:Species) " +
                "WHERE p.dbId IN $list " +
                "RETURN DISTINCT p.dbId AS dbId, " +
                "                p.stId AS stId, " +
                "                p.displayName AS displayName, " +
                "                p.schemaClass AS schemaClass, " +
                "                s.dbId AS speciesID";
        try {
            Collection<QueryResult> nodesQueryResults = aos.getCustomQueryResults(QueryResult.class, query, parametersMap);
            for (QueryResult result : nodesQueryResults) {
                rtn.add(new EntityNode(result, diagram.getDiagramIds(result.getDbId())));
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }

        //language=Cypher
        query = "" +
                "MATCH path=(p:Pathway{dbId:$dbId})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                "WHERE single(x IN nodes(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                "MATCH (rle)-" +
                "[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|" +
                "regulatedBy|regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]" +
                "->(pe:PhysicalEntity) " +
                "WITH collect(DISTINCT pe) AS pes " +
                "UNWIND pes AS pe " +
                "OPTIONAL MATCH (pe)-[:hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker]->(children:PhysicalEntity) " +
                "OPTIONAL MATCH (parent:PhysicalEntity)-[:hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker]->(pe) " +
                "WHERE parent IN pes " +
                "OPTIONAL MATCH (pe)-[:referenceEntity]->(re:ReferenceEntity) " +
                "OPTIONAL MATCH (pe)-[:species]->(s:Species) " +
                "RETURN pe.dbId AS dbId, pe.stId AS stId, pe.displayName AS displayName, pe.schemaClass AS schemaClass, " +
                "       pe.referenceType AS referenceType, s.dbId AS speciesID, " +
                "       collect(DISTINCT children.dbId) AS children, " +
                "       collect(DISTINCT parent.dbId) AS parents, " +
                "       CASE WHEN re.variantIdentifier IS NULL THEN re.identifier ELSE re.variantIdentifier END AS identifier, " +
                "       re.geneName AS geneNames";

        parametersMap.put("dbId", diagram.getDbId());
        try {
            Collection<NodesQueryResult> nodesQueryResults = aos.getCustomQueryResults(NodesQueryResult.class, query, parametersMap);
            for (NodesQueryResult result : nodesQueryResults) {
                EntityNode en = new EntityNode(result, diagram.getDiagramIds(result.getDbId()));
                rtn.add(en);
                // The following map is used to annotate the trivial molecules.
                entityNodeMap.put(en.dbId, en);
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return rtn.isEmpty() ? null : rtn;
    }

    private Collection<EventNode> getGraphEdges(Diagram diagram) {
        List<EventNode> rtn = new ArrayList<>();
        String query = "" +
                "MATCH path=(p:Pathway{dbId:$dbId})-[:hasEvent*]->(:ReactionLikeEvent) " +
                "WHERE SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                "WITH DISTINCT p, LAST(NODES(path)) AS rle " +
                "OPTIONAL MATCH (rle)-[:input]->(i:PhysicalEntity) " +
                "OPTIONAL MATCH (rle)-[:output]->(o:PhysicalEntity) " +
                "OPTIONAL MATCH (rle)-[:catalystActivity|physicalEntity*]->(c:PhysicalEntity) " +
                "OPTIONAL MATCH (rle)-[:entityFunctionalStatus|diseaseEntity*]->(e:PhysicalEntity) " +
                "OPTIONAL MATCH (rle)-[:regulatedBy]->(reg:Regulation)-[:regulator]->(r:PhysicalEntity) " +
                "OPTIONAL MATCH prep=(p)-[:hasEvent*]->(pre:ReactionLikeEvent)<-[:precedingEvent]-(rle) " +
                "WHERE SINGLE(x IN NODES(prep) WHERE (x:Pathway) AND x.hasDiagram) " +
                "OPTIONAL MATCH folp=(p)-[:hasEvent*]->(fol:ReactionLikeEvent)-[:precedingEvent]->(rle) " +
                "WHERE SINGLE(x IN NODES(folp) WHERE (x:Pathway) AND x.hasDiagram) " +
                "RETURN  rle.dbId AS dbId, rle.stId as stId, rle.displayName AS displayName, rle.schemaClass AS schemaClass, " +
                "        COLLECT(DISTINCT i.dbId) AS inputs, " +
                "        COLLECT(DISTINCT o.dbId) AS outputs, " +
                "        COLLECT(DISTINCT c.dbId) AS catalysts, " +
                "        COLLECT(DISTINCT e.dbId) AS efs, " +
                "        CASE WHEN reg IS NULL THEN [] ELSE COLLECT(DISTINCT {type: reg.schemaClass, dbId: r.dbId}) END AS regulation, " +
                "        COLLECT(DISTINCT pre.dbId) AS preceding, " +
                "        COLLECT(DISTINCT fol.dbId) AS following";
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("dbId", diagram.getDbId());
        try {
            Collection<EdgesQueryResult> edgesQueryResults = aos.getCustomQueryResults(EdgesQueryResult.class, query, parametersMap);
            for (EdgesQueryResult result : edgesQueryResults) {
                rtn.add(new EventNode(result, diagram.getDiagramIds(result.getDbId())));
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return rtn.isEmpty() ? null : rtn;
    }

    private Collection<SubpathwayNode> getSubpathways(Diagram diagram) {
        Set<SubpathwayNode> rtn = new HashSet<>();

        String query = "" +
                "MATCH path=(p:Pathway{dbId:$dbId})-[:hasEvent*]->(s:Event) " +
                "WHERE NONE(x IN NODES(path) WHERE (x:ReactionLikeEvent)) AND NONE(x IN TAIL(NODES(path)) WHERE x.hasDiagram) " +
                "WITH DISTINCT s, SIZE(TAIL(NODES(path))) AS level " +
                "MATCH path=(s)-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                "WHERE NONE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                "RETURN DISTINCT s.dbId AS dbId," +
                "                s.stId AS stId, " +
                "                s.displayName AS displayName, " +
                "                COLLECT(rle.dbId) AS events, " +
                "                level";
        Map<String, Object> parametersMap = new HashMap<>();
        parametersMap.put("dbId", diagram.getDbId());
        try {
            var subpathwaysQueryResults = aos.getCustomQueryResults(SubpathwaysQueryResult.class, query, parametersMap);
            for (SubpathwaysQueryResult result : subpathwaysQueryResults) {
                rtn.add(new SubpathwayNode(result));
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return rtn.isEmpty() ? null : rtn;
    }

    private Collection<Long> getProcessNodes(Long dbId) {
        String query = "" +
                "MATCH path=(p:Pathway{dbId:$dbId})-[:hasEvent*]->(sp:Pathway{hasDiagram:True}) " +
                "WHERE SINGLE(x IN TAIL(NODES(path)) WHERE NOT x.hasDiagram IS NULL AND x.hasDiagram) " +
                "RETURN DISTINCT sp.dbId";
        Map<String, Object> params = new HashMap<>();
        params.put("dbId", dbId);
        try {
            return aos.getCustomQueryResults(Long.class, query, params);
        } catch (CustomQueryException e) {
            return Collections.emptyList();
        }
    }

    private Collection<Long> getStructuresDrug(Long dbId) {
        String query = "" +
                "MATCH path=(p:Pathway{dbId:$dbId})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                "WHERE SINGLE(x IN NODES(path) WHERE NOT x.hasDiagram IS NULL AND x.hasDiagram) " +
                "WITH DISTINCT rle " +
                "MATCH (rle)-[:input|output|catalystActivity|physicalEntity|regulatedBy|regulator*]->(pe:PhysicalEntity)" +
                "-[:hasComponent|hasMember|hasCandidate|proteinMarker|RNAMarker*]->(p:Drug) " +
                "RETURN DISTINCT pe.dbId";
        Map<String, Object> params = new HashMap<>();
        params.put("dbId", dbId);
        try {
            return aos.getCustomQueryResults(Long.class, query, params);
        } catch (CustomQueryException e) {
            return Collections.emptyList();
        }
    }
}
