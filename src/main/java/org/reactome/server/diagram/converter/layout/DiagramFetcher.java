package org.reactome.server.diagram.converter.layout;

import org.gk.model.GKInstance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.pathwaylayout.*;
import org.gk.persistence.DiagramGKBReader;
import org.gk.persistence.MySQLAdaptor;
import org.gk.render.Node;
import org.gk.render.RenderablePathway;
import org.reactome.server.diagram.converter.layout.exceptions.DiagramNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class DiagramFetcher {

    private static Logger logger = LoggerFactory.getLogger("converter");

    public MySQLAdaptor dba;

    public DiagramFetcher(MySQLAdaptor dba) {
        this.dba = dba;
    }

    public GKInstance getInstance(String identifier) throws DiagramNotFoundException {
        identifier = identifier.trim().split("\\.")[0];
        try {
            if (identifier.startsWith("REACT")) {
                return getInstance(dba.fetchInstanceByAttribute(
                        ReactomeJavaConstants.StableIdentifier,
                        "oldIdentifier",
                        "=",
                        identifier
                ));
            } else if (identifier.startsWith("R-")) {
                return getInstance(dba.fetchInstanceByAttribute(
                        ReactomeJavaConstants.StableIdentifier,
                        ReactomeJavaConstants.identifier,
                        "=",
                        identifier
                ));
            } else {
                return dba.fetchInstance(Long.parseLong(identifier));
            }
        } catch (Exception e) {
            throw new DiagramNotFoundException("No diagram found for " + identifier);
        }
    }

    private GKInstance getInstance(Collection<GKInstance> target) throws Exception {
        if (target == null || target.size() != 1)
            throw new Exception("Many options have been found fot the specified identifier");
        GKInstance stId = target.iterator().next();
        return (GKInstance) dba.fetchInstanceByAttribute(
                ReactomeJavaConstants.DatabaseObject,
                ReactomeJavaConstants.stableIdentifier,
                "=",
                stId
        ).iterator().next();
    }

    public String getPathwayStableId(GKInstance instance) throws Exception {
        if (instance.getSchemClass().isValidAttribute(ReactomeJavaConstants.stableIdentifier)) {
            GKInstance stId = (GKInstance) instance.getAttributeValue(ReactomeJavaConstants.stableIdentifier);
            return (String) stId.getAttributeValue(ReactomeJavaConstants.identifier);
        }
        String msg = "No stable identifier found for pathway " + instance.getDBID();
        logger.error(msg);
        throw new Exception(msg);
    }

    @SuppressWarnings("unused")
    public String getPathwayDiagramXML(Long pathwayId) throws Exception {
        return getPathwayDiagramXML(dba.fetchInstance(pathwayId));
    }

    public String getPathwayDiagramXML(GKInstance pathway) throws Exception {
        DiagramGeneratorFromDB diagramHelper = new DiagramGeneratorFromDB();
        diagramHelper.setMySQLAdaptor(dba);
        // Find PathwayDiagram
        GKInstance diagram = diagramHelper.getPathwayDiagram(pathway);
        if (diagram != null) {
            PathwayDiagramXMLGenerator xmlGenerator = new PathwayDiagramXMLGenerator();
            xmlGenerator.setTightNodes(false);
            return xmlGenerator.generateXMLForPathwayDiagram(diagram, pathway);
        }
        return null;
    }
}
