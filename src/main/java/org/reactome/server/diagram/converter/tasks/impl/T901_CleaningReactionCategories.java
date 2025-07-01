package org.reactome.server.diagram.converter.tasks.impl;

import org.reactome.server.diagram.converter.tasks.common.AbstractConverterTask;
import org.reactome.server.diagram.converter.tasks.common.annotation.InitialTask;
import org.reactome.server.graph.domain.model.ReactionLikeEvent;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Removes the category field from all {@link ReactionLikeEvent} instances in the graph database. This field will be
 * populated again during the conversion process and by the {@link T902_RemainingReactionCategories} task.
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@InitialTask
public class T901_CleaningReactionCategories extends AbstractConverterTask {

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
            query = "MATCH (rle:ReactionLikeEvent) ";
        } else if (target instanceof Species) {
            query = "MATCH (rle:ReactionLikeEvent)-[:species]->(:Species{displayName:$speciesName}) ";
            params.put("speciesName", ((Species) target).getDisplayName());
        } else if (target instanceof Collection){
            query = "MATCH path=(p:Pathway{hasDiagram:true})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                    "WHERE p.stId IN $stIds AND SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                    "WITH DISTINCT rle ";
            params.put("stIds", target);
        } else {
            report = "No query could be created for the target";
            logger.error(report);
            return;
        }

        query += "" +
//              "MATCH (rle:ReactionLikeEvent) " +
                "REMOVE rle.category " +
                "RETURN COUNT(DISTINCT rle) AS rles";
        try {
            Integer c = ads.getCustomQueryResult(Integer.class, query, params);
            report = String.format("The 'category' field has been cleaned up for %,d reactions", c);
        } catch (CustomQueryException e) {
            String msg = String.format("Error while executing '%s': '%s'", this.getClass().getSimpleName(), e.getMessage());
            logger.error(msg, e);
            report = msg + ". Please see logs for more details.";
        }
    }

}
