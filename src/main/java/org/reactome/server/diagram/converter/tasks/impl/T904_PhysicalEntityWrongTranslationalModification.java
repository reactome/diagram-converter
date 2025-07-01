package org.reactome.server.diagram.converter.tasks.impl;

import org.reactome.server.diagram.converter.qa.common.AbstractConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.ConverterReport;
import org.reactome.server.diagram.converter.tasks.common.ConverterTask;
import org.reactome.server.diagram.converter.tasks.common.annotation.FinalTask;
import org.reactome.server.graph.domain.model.Species;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * This task is executed once at the end of the conversion {@link FinalTask} and the results are included in the main
 * reports {@link ConverterReport}
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
@SuppressWarnings("unused")
@ConverterReport
@FinalTask
public class T904_PhysicalEntityWrongTranslationalModification extends AbstractConverterQA implements ConverterTask {

    private static final Logger logger = LoggerFactory.getLogger("converter");

    private static final AdvancedDatabaseObjectService ads = ReactomeGraphCore.getService(AdvancedDatabaseObjectService.class);

    private String report = "Not executed";

    //Needs to be static based on the behaviour of ConverterReport classes
    private static Collection<String> lines = new ArrayList<>();

    @Override
    public String getDescription() {
        return "PhysicalEntity instances with a potentially wrongly annotated translational modification";
    }

    @Override
    public QAPriority getPriority() {
        return QAPriority.MEDIUM;
    }

    @Override
    protected String getHeader() {
        return "EWAS_Id,EWAS_Name,Modification,ModificationType,PSI_Id,PSIMod,Created,Modified";
    }

    @Override
    public List<String> getReport() {
        return getReport(lines);
    }

    @Override
    public String getReportSummary() {
        return report;
    }

    @Override
    public void run(Object target) {
        String query;
        Map<String, Object> params = new HashMap<>();
        if (target instanceof String && target.equals("all")) {
            query = "MATCH (psi:PsiMod)<-[:psiMod]-(tm:TranslationalModification)<-[:hasModifiedResidue]-(pe:PhysicalEntity) " +
                    "WITH DISTINCT pe, tm, psi ";
        } else if (target instanceof Species) {
            query = "MATCH (psi:PsiMod)<-[:psiMod]-(tm:TranslationalModification)<-[:hasModifiedResidue]-(pe:PhysicalEntity)" +
                    "-[:species]->(:Species{displayName:$speciesName}) " +
                    "WITH DISTINCT pe, tm, psi ";
            params.put("speciesName", ((Species) target).getDisplayName());
        } else if (target instanceof Collection) {
            query = "MATCH path=(p:Pathway{hasDiagram:true})-[:hasEvent*]->(rle:ReactionLikeEvent) " +
                    "WHERE p.stId IN $stIds AND SINGLE(x IN NODES(path) WHERE (x:Pathway) AND x.hasDiagram) " +
                    "WITH DISTINCT rle " +
                    "MATCH (rle)-" +
                    "[:input|output|catalystActivity|physicalEntity|regulatedBy|" +
                    "regulator|hasComponent|hasMember|hasCandidate|repeatedUnit|proteinMarker|RNAMarker*]" +
                    "->(pe:PhysicalEntity)-[:hasModifiedResidue]->(tm:TranslationalModification)-[:psiMod]->(psi:PsiMod) " +
                    "WITH DISTINCT pe, tm, psi ";
            params.put("stIds", target);
        } else {
            report = "No query could be created for the target";
            logger.error(report);
            return;
        }

        query += "" +
 // "MATCH (psi:PsiMod)<-[:psiMod]-(tm:TranslationalModification)<-[:hasModifiedResidue]-(pe:PhysicalEntity{speciesName:{speciesName}}) " +
                "WHERE psi.label IS NULL " +
                "OPTIONAL MATCH (a)-[:created]->(pe) " +
                "OPTIONAL MATCH (m)-[:modified]->(pe) " +
                "WITH DISTINCT pe, tm, psi, CASE WHEN a IS NULL THEN 'null' ELSE a.displayName END AS created, " +
                "   CASE WHEN m IS NULL THEN 'null' ELSE m.displayName END AS modified " +
                "ORDER BY modified, created, pe.stId, tm.displayName " +
                "RETURN DISTINCT pe.stId + ',\"' + pe.displayName + '\",\"' + tm.displayName+ '\",' + tm.schemaClass + '," +
                "\"' + psi.dbId + ',\"' + psi.displayName + '\",\"' + created + '\",\"' + modified + '\"' AS line";
        try {
            Collection<String> res = ads.getCustomQueryResults(String.class, query, params);
            if (res.size() > 0) {
                lines = new ArrayList<>(res);
                report = String.format("%,d physical entities with a potentially wrongly annotated translational modification", res.size());
            } else {
                report = "No physical entities found with wrong AbstractModification annotated";
            }

        } catch (CustomQueryException e) {
            String msg = String.format("Error while executing '%s': '%s'", this.getClass().getSimpleName(), e.getMessage());
            logger.error(msg, e);
            report = msg + ". Please see logs for more details.";
        }
    }

}
