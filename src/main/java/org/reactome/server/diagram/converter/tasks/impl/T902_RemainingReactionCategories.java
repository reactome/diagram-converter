package org.reactome.server.diagram.converter.tasks.impl;

import org.reactome.server.diagram.converter.qa.common.data.Category;
import org.reactome.server.diagram.converter.tasks.common.AbstractConverterTask;
import org.reactome.server.diagram.converter.tasks.common.annotation.FinalTask;
import org.reactome.server.graph.domain.model.Pathway;
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
 * Finds all {@link ReactionLikeEvent} instances where the category field does not exist and infers which category
 * should be assign in each case.
 * <p>
 * The reason why these instances do not have a category at this point is because they have not been found in any
 * diagram, meaning that these are orphan reactions (not present in the 'hasEvent' list of any {@link Pathway}.
 * <p>
 * Note: An orphan reaction instance is normally used to manually infer another reaction that will actually be
 * present in a diagram (and therefore will be found in the 'hasEvent' list of a {@link Pathway} instance).
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@FinalTask
public class T902_RemainingReactionCategories extends AbstractConverterTask {

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
            query = "MATCH (rle:ReactionLikeEvent) " +
                    "WHERE rle.category IS NULL ";
        } else if (target instanceof Species) {
            query = "MATCH (rle:ReactionLikeEvent)-[:species]->(:Species{displayName:$speciesName}) " +
                    "WHERE rle.category IS NULL ";
            params.put("speciesName", ((Species) target).getDisplayName());
        } else if (target instanceof Collection) {
            query = "MATCH path=(p:Pathway{hasDiagram:true})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                    "WHERE p.stId IN $stIds AND rle.category IS NULL AND SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                    "WITH DISTINCT rle ";
            params.put("stIds", target);
        } else {
            report = "No query could be created for the target";
            logger.error(report);
            return;
        }

        query += "" +
//              "MATCH (rle:ReactionLikeEvent) " +
//              "WHERE rle.category IS NULL " +
                "OPTIONAL MATCH (rle)-[i:input]->(:PhysicalEntity) " +
                "WITH DISTINCT rle, COLLECT(DISTINCT i) AS ii " +
                "OPTIONAL MATCH (rle)-[o:output]->(:PhysicalEntity) " +
                "WITH DISTINCT rle, REDUCE(n=0, i IN ii | n + i.stoichiometry) AS ni, COLLECT(DISTINCT o) AS oo " +
                "WITH DISTINCT rle, ni, REDUCE(n=0, o IN oo | n + o.stoichiometry) AS no " +
                "WITH rle, ni-no AS d " +
                "WITH rle, CASE " +
                "         WHEN (rle:BlackBoxEvent) THEN $omitted " +
                "         WHEN (rle:Polymerisation) OR (rle:Depolymerisation) THEN $transition " +
                "         WHEN (rle)-[:catalystActivity]->() THEN $transition " +
                "         WHEN d > 0 THEN CASE " +
                "                           WHEN (rle)-[:output]->(:Complex) THEN $binding " +
                "                           ELSE $transition " +
                "                         END " +
                "         WHEN d < 0 THEN CASE " +
                "                           WHEN (rle)-[:input]->(:Complex) THEN $dissociation " +
                "                           ELSE $transition " +
                "                         END " +
                "         ELSE $transition " +
                "       END AS category " +
                "SET rle.category = category " +
                "RETURN COUNT(DISTINCT rle) AS updated";
        params.put("transition", Category.TRANSITION.getName());
        params.put("binding", Category.BINDING.getName());
        params.put("dissociation", Category.DISSOCIATION.getName());
        params.put("omitted", Category.OMITTED.getName());
        try {
            Integer c = ads.getCustomQueryResult(Integer.class, query, params);
            report = String.format("The 'category' field has been filled up for %,d orphan reactions", c);
        } catch (CustomQueryException e) {
            String msg = String.format("Error while executing '%s': '%s'", this.getClass().getSimpleName(), e.getMessage());
            logger.error(msg, e);
            report = msg + ". Please see logs for more details.";
        }
    }

}
