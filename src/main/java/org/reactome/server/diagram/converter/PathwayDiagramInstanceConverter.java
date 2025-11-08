package org.reactome.server.diagram.converter;

import java.util.Collection;

import org.gk.model.GKInstance;
import org.gk.model.ReactomeJavaConstants;
import org.gk.pathwaylayout.PathwayDiagramXMLGenerator;
import org.gk.persistence.DiagramGKBReader;
import org.gk.persistence.MySQLAdaptor;
import org.gk.render.RenderablePathway;
import org.reactome.server.diagram.converter.graph.DiagramGraphFactory;
import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.LayoutFactory;
import org.reactome.server.diagram.converter.layout.input.ProcessFactory;
import org.reactome.server.diagram.converter.layout.input.model.Process;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.util.JsonWriter;
import org.reactome.server.diagram.converter.layout.util.TrivialChemicals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This specific class is used to generate JSON from PathwayDiagram instances' storedATXML directly so
 * that these JSON text can be used for editing related to disease pathways that are overlaid onto the normal
 * pathway diagrams. There is no need to run this conversion for the production server. 
 * Note: This class is modified from Converter.java.
 */
public class PathwayDiagramInstanceConverter {
    private final static Logger logger = LoggerFactory.getLogger(PathwayDiagramInstanceConverter.class);

    private String outputDir;
    private MySQLAdaptor dba;
    private DiagramGraphFactory graphFactory;
    private ProcessFactory processFactory;
    private TrivialChemicals trivialChemicals;

    public PathwayDiagramInstanceConverter(MySQLAdaptor dba) {
        this.dba = dba;
        graphFactory = new DiagramGraphFactory();
        processFactory = new ProcessFactory("/process_schema.xsd");
        trivialChemicals = new TrivialChemicals();
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
        // Check if output directory exists, if not create it
        java.io.File outDir = new java.io.File(outputDir);
        if (!outDir.exists()) { 
            outDir.mkdirs();
        }
    }

    @SuppressWarnings("unchecked")
    public void convert() {
        try {
            // Conversion logic to be implemented here
            logger.info("Conversion started. Output directory: " + outputDir);
            // Step 1: Fetch PathwayDiagram instances
            Collection<GKInstance> pathwayDiagrams = this.dba.fetchInstancesByClass(ReactomeJavaConstants.PathwayDiagram);
            // Step 2: Process each instance to generate JSON
            PathwayDiagramXMLGenerator xmlGenerator = new PathwayDiagramXMLGenerator();
            xmlGenerator.setTightNodes(false);
            DiagramGKBReader diagramReader = new DiagramGKBReader();
            for (GKInstance diagramInstance : pathwayDiagrams) {
                logger.info("Processing PathwayDiagram instance DBID: " + diagramInstance.getDBID());
                // Skip it if no representedPathway
                GKInstance representedPathway = (GKInstance) diagramInstance.getAttributeValue(ReactomeJavaConstants.representedPathway);
                if (representedPathway == null) {   
                    logger.warn("No representedPathway found for PathwayDiagram instance DBID: " + diagramInstance.getDBID());
                    continue;
                }
                // Fetch storedATXML and convert to JSON
                String atxml = (String) diagramInstance.getAttributeValue(ReactomeJavaConstants.storedATXML);
                if (atxml == null || atxml.isEmpty()) {
                    logger.warn("No storedATXML found for PathwayDiagram instance DBID: " + diagramInstance.getDBID());
                    continue;
                }
                // Check if there is anything to convert
                RenderablePathway pathway = diagramReader.openDiagram(atxml);
                if (pathway.getComponents() == null || pathway.getComponents().isEmpty()) {
                    logger.warn("No components found in storedATXML for PathwayDiagram instance DBID: " + diagramInstance.getDBID());
                    continue;
                }
                // Generate XML first: This is the native XML contains both normal and disease layout information
                String xml = xmlGenerator.generateXMLForPathwayDiagram(diagramInstance);
                String dbId = diagramInstance.getDBID().toString();
                Process process = processFactory.createProcess(xml, dbId);
                // This is a hack to use PathwayDiagram instead of Pathway.
                Diagram diagram = LayoutFactory.getDiagramFromProcess(process, 
                        diagramInstance, 
                        dbId);
                // Bypass all QA checks here and generate the json text
                Graph graph = graphFactory.getGraph(diagram);
                diagram.createShadows(graph.getSubpathways());
                diagram = trivialChemicals.annotateTrivialChemicals(diagram, graphFactory.getEntityNodeMap());
                // Step 3: Write JSON to outputDir  
                JsonWriter.serialiseGraph(graph, outputDir);
                JsonWriter.serialiseDiagram(diagram, outputDir);
            }
            logger.info("Conversion completed.");
        }
        catch (Exception e) {
            logger.error("Conversion failed: " + e.getMessage(), e);
        }
    }

}
