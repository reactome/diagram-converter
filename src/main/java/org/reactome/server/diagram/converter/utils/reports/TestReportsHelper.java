package org.reactome.server.diagram.converter.utils.reports;

import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class TestReportsHelper {

    private static AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    public static String getCreatedModified(Long dbId) {
        try {
            CurationDetails rtn = ads.getCustomQueryResult(CurationDetails.class, "" +
                            "MATCH (d:DatabaseObject{dbId:$dbId}) " +
                            "OPTIONAL MATCH (a)-[:created]->(d) " +
                            "OPTIONAL MATCH (m)-[:modified]->(d) " +
                            "RETURN a.displayName As created, m.displayName AS modified " +
                            "ORDER BY m.dateTime DESC LIMIT 1",
                    Map.of("dbId", dbId));
            return rtn.toString();
        } catch (NullPointerException e) {
            return "N/A, N/A";  //dbId is not in the database
        } catch (CustomQueryException e) {
            return "null,null";
        }
    }

    //An easy way for a single cache
    private static Long diagramId;
    private static Map<Long, Participant> cache = new HashMap<>();

    public static Map<Long, Participant> getDiagramParticipants(Long diagramDbId) {
        if (Objects.equals(TestReportsHelper.diagramId, diagramDbId)) return cache;

        TestReportsHelper.diagramId = diagramDbId;
        cache = new HashMap<>();

        String query = "" +
                "MATCH path=(p:Pathway{hasDiagram:True, dbId:$dbId})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                "WHERE SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                "WITH DISTINCT rle " +
                "MATCH (rle)-" +
                "[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator*]" +
                "->(pe:PhysicalEntity) " +
                "OPTIONAL MATCH (pe)-[:referenceEntity]->(re:ReferenceEntity) " +
                "RETURN DISTINCT pe.dbId AS dbId, pe.schemaClass AS schemaClass, " +
                "       CASE " +
                "         WHEN pe.schemaClass = 'OtherEntity'         THEN 'Entity' " +
                "         WHEN pe.schemaClass = 'Polymer'             THEN 'Entity' " +
                "         WHEN pe.schemaClass = 'GenomeEncodedEntity' THEN 'Entity' " +
                "         WHEN pe.schemaClass = 'SimpleEntity'        THEN 'Chemical' " +
                "         WHEN (pe:EntitySet)                         THEN 'EntitySet' " +
                "         WHEN (re:ReferenceRNASequence)              THEN 'RNA' " +
                "         WHEN (re:ReferenceDNASequence)              THEN 'Gene' " +
                "         WHEN (re:ReferenceSequence)                 THEN 'Protein' " +
                "         ELSE                                        pe.schemaClass " +
                "       END AS renderableClass " +
                "UNION " +
                "MATCH (p:Pathway{hasDiagram:True, dbId:$dbId})-[:hasEvent*]->(s:Pathway{hasDiagram:True}) " +
                "RETURN DISTINCT s.dbId AS dbId, 'Pathway' AS schemaClass, 'ProcessNode' AS renderableClass " +
                "UNION " +
                "MATCH (:Pathway{hasDiagram:True, dbId:$dbId})-[:normalPathway]->(p:Pathway), " +
                "      path=(p)-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                "WHERE SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                "WITH DISTINCT rle " +
                "MATCH (rle)-" +
                "[:input|output|catalystActivity|physicalEntity|entityFunctionalStatus|diseaseEntity|regulatedBy|regulator*]" +
                "->(pe:PhysicalEntity) " +
                "OPTIONAL MATCH (pe)-[:referenceEntity]->(re:ReferenceEntity) " +
                "RETURN DISTINCT pe.dbId AS dbId, pe.schemaClass AS schemaClass, " +
                "       CASE " +
                "         WHEN pe.schemaClass = 'OtherEntity'         THEN 'Entity' " +
                "         WHEN pe.schemaClass = 'Polymer'             THEN 'Entity' " +
                "         WHEN pe.schemaClass = 'GenomeEncodedEntity' THEN 'Entity' " +
                "         WHEN pe.schemaClass = 'SimpleEntity'        THEN 'Chemical' " +
                "         WHEN (pe:EntitySet)                         THEN 'EntitySet' " +
                "         WHEN (re:ReferenceRNASequence)              THEN 'RNA' " +
                "         WHEN (re:ReferenceDNASequence)              THEN 'Gene' " +
                "         WHEN (re:ReferenceSequence)                 THEN 'Protein' " +
                "         ELSE                                        pe.schemaClass " +
                "       END AS renderableClass ";
        Map<String, Object> params = new HashMap<>();
        params.put("dbId", diagramDbId);
        try {
            for (Participant participant : ads.getCustomQueryResults(Participant.class, query, params)) {
                cache.put(participant.getDbId(), participant);
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }
        return cache;
    }
}
