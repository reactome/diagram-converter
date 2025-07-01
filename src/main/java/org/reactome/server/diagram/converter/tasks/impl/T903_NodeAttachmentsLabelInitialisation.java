package org.reactome.server.diagram.converter.tasks.impl;

import org.reactome.server.diagram.converter.tasks.common.AbstractConverterTask;
import org.reactome.server.diagram.converter.tasks.common.annotation.InitialTask;
import org.reactome.server.graph.domain.model.AbstractModifiedResidue;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Sets label fields for all {@link AbstractModifiedResidue} instances based on the PSI-MOD label.
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@InitialTask
public class T903_NodeAttachmentsLabelInitialisation extends AbstractConverterTask {

    // List of psiMod dbIds that correspond to glycans when GroupModifiedResidue instances have a modification
    // Provided by Bijay Jassal
    private static final List<Long> glycans = Arrays.asList(
            1467184L, 1467185L, 1467292L, 2022987L, 913651L, 913631L,
            2243916L, 2022865L, 3238099L, 448181L, 5615587L, 2063975L
    );

    private static final Logger logger = LoggerFactory.getLogger("converter");

    private static final AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    private String report = "Not executed";

    @Override
    public String getReportSummary() {
        return report;
    }

    @Override
    public void run(Object target) {
        String query;
        Map<String, Object> params = new HashMap<>();
        if (target instanceof String && target.equals("all")) {
            query = "MATCH (tm:TranslationalModification) ";
        } else if (target instanceof Species) {
            query = "MATCH (tm:TranslationalModification)<-[:hasModifiedResidue]-(:PhysicalEntity)" +
                    "-[:species]->(:Species{displayName:$speciesName}) " +
                    "WITH DISTINCT tm ";
            params.put("speciesName", ((Species) target).getDisplayName());
        } else if (target instanceof Collection) {
            query = "MATCH path=(p:Pathway{hasDiagram:true})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                    "WHERE p.stId IN $stIds AND SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                    "WITH DISTINCT rle " +
                    "MATCH (rle)-" +
                    "[:input|output|catalystActivity|physicalEntity|regulatedBy|" +
                    "regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]" +
                    "->(:PhysicalEntity)-[:hasModifiedResidue]->(tm:TranslationalModification) " +
                    "WITH DISTINCT tm ";
            params.put("stIds", target);
        } else {
            report = "No query could be created for the target";
            logger.error(report);
            return;
        }

        query += "" +
//              "MATCH (tm:TranslationalModification) " +
                "OPTIONAL MATCH (tm)-[:psiMod]->(psi) " +
                "OPTIONAL MATCH (tm)-[:modification]->(m) " +
                "WITH DISTINCT tm, " +
                "     CASE WHEN ((tm:GroupModifiedResidue) AND NOT m IS NULL AND psi.dbId IN $glycans) THEN 'G' " +
                "          WHEN ((tm:CrosslinkedResidue) AND NOT m IS NULL) THEN 'CL' " +
                "          ELSE psi.label END AS label " +
                "WHERE NOT label IS NULL " +
                "SET tm.label = label " +
                "RETURN COUNT(DISTINCT tm) AS tms";
        try {
            params.put("glycans", glycans);
            Integer c = ads.getCustomQueryResult(Integer.class, query, params);
            report = String.format("Node attachment 'label' has been set for %,d TranslationalModification instances", c);
        } catch (CustomQueryException e) {
            String msg = String.format("Error while executing '%s': '%s'", this.getClass().getSimpleName(), e.getMessage());
            logger.error(msg, e);
            report = msg + ". Please see logs for more details.";
        }
    }

}
