package org.reactome.server.diagram.converter;

import org.gk.model.GKInstance;
import org.gk.persistence.MySQLAdaptor;
import org.reactome.server.diagram.converter.graph.DiagramGraphFactory;
import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.DiagramFetcher;
import org.reactome.server.diagram.converter.layout.LayoutFactory;
import org.reactome.server.diagram.converter.layout.exceptions.DiagramNotFoundException;
import org.reactome.server.diagram.converter.layout.input.ProcessFactory;
import org.reactome.server.diagram.converter.layout.input.model.Process;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.layout.util.JsonWriter;
import org.reactome.server.diagram.converter.layout.util.TrivialChemicals;
import org.reactome.server.diagram.converter.qa.QATests;
import org.reactome.server.diagram.converter.qa.conversion.T001_FailedPathways;
import org.reactome.server.diagram.converter.tasks.ConverterTasks;
import org.reactome.server.diagram.converter.utils.ProgressBar;
import org.reactome.server.graph.domain.result.SimpleDatabaseObject;
import org.reactome.server.graph.service.GeneralService;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
class Converter {

    private static final Logger logger = LoggerFactory.getLogger("converter");

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

    private static DiagramFetcher diagramFetcher;
    private static DiagramGraphFactory graphFactory;
    private static ProcessFactory processFactory;

    private static TrivialChemicals trivialChemicals;

    static void run(Collection<SimpleDatabaseObject> pathways, MySQLAdaptor dba, String output) {
        outputCheck(output);
        diagramFetcher = new DiagramFetcher(dba);
        graphFactory = new DiagramGraphFactory();
        processFactory = new ProcessFactory("/process_schema.xsd");
        trivialChemicals = new TrivialChemicals();

        long start = System.currentTimeMillis();
        ConverterTasks.runInitialTasks();
        int i = 0; int tot = pathways.size();
        int version = ReactomeGraphCore.getService(GeneralService.class).getDBInfo().getVersion();

        System.out.printf("\r· Diagram converter for version %d started:\n\t> Targeting %s pathways.\n%n", version, numberFormat.format(tot));
        for (SimpleDatabaseObject pathway : pathways) {
            ProgressBar.updateProgressBar(pathway.getStId(), i++, tot);
            try {
                if (!convert(pathway, output)) T001_FailedPathways.add(pathway);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Failed to convert pathway " + pathway.getStId(), e);
                T001_FailedPathways.add(pathway, e.getMessage());
            }
        }
        ProgressBar.done(tot);
        ConverterTasks.runFinalTasks();
        Long time = System.currentTimeMillis() - start;

        QATests.writeReports();
        String conv = numberFormat.format(tot - T001_FailedPathways.size());
        System.out.printf("· Conversion finished: %s pathway diagrams have been successfully converted (%s)\n%n", conv, getTimeFormatted(time));
    }

    private static boolean convert(SimpleDatabaseObject pathway, String outputDir) throws DiagramNotFoundException {
        Diagram diagram = getDiagram(diagramFetcher.getInstance(pathway.getDbId() + ""));
        if (diagram == null) return false;

        QATests.performDiagramTests(diagram);

        Graph graph = graphFactory.getGraph(diagram);
        QATests.performGraphTests(graph);

        diagram.createShadows(graph.getSubpathways());
        if (trivialChemicals != null) {
            diagram = trivialChemicals.annotateTrivialChemicals(diagram, graphFactory.getEntityNodeMap());
        }

        QATests.performFinalTests(diagram, graph);

        JsonWriter.serialiseGraph(graph, outputDir);
        JsonWriter.serialiseDiagram(diagram, outputDir);

        return true;
    }

    private static Diagram getDiagram(GKInstance pathway) {
        String stId = null;
        try {
            stId = diagramFetcher.getPathwayStableId(pathway);
            String xml = diagramFetcher.getPathwayDiagramXML(pathway);
            if (xml != null) {
                Process process = processFactory.createProcess(xml, stId);
                return LayoutFactory.getDiagramFromProcess(process, pathway, stId);
            }
        } catch (Exception e) {
            logger.error("[" + stId + "] conversion failed. The following error occurred while converting pathway diagram:", e);
        }
        return null;
    }

    private static void outputCheck(String output) {
        File folder = new File(output);
        if (!folder.exists() && !folder.mkdir()) {
            System.err.println(folder.getAbsolutePath() + " does not exist and cannot be created. Please check the path and try again");
            System.exit(1);
        }
    }

    private static String getTimeFormatted(Long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}
