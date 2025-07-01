package org.reactome.server.diagram.converter.qa;

import org.apache.commons.io.FileUtils;
import org.reactome.server.diagram.converter.Main;
import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.output.Diagram;
import org.reactome.server.diagram.converter.qa.common.ConverterQA;
import org.reactome.server.diagram.converter.qa.common.QAPriority;
import org.reactome.server.diagram.converter.qa.common.annotation.ConverterReport;
import org.reactome.server.diagram.converter.qa.common.annotation.DiagramTest;
import org.reactome.server.diagram.converter.qa.common.annotation.GraphTest;
import org.reactome.server.diagram.converter.qa.common.annotation.PostTest;
import org.reactome.server.diagram.converter.qa.diagram.DiagramQA;
import org.reactome.server.diagram.converter.qa.graph.GraphQA;
import org.reactome.server.diagram.converter.qa.post.PostQA;
import org.reactome.server.diagram.converter.utils.reports.Report;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class QATests {

    private static final List<Class<?>> diagramQATests = new ArrayList<>();
    private static final List<Class<?>> graphQATests = new ArrayList<>();
    private static final List<Class<?>> finalQATests = new ArrayList<>();

    private static Integer version;
    private static String path = "./reports";

    public static void initialise(Integer version) {
        QATests.version = version;

        System.out.println("· Diagram converter QA test initialisation:");
        System.out.print("\t>Initialising tests infrastructure...");
        Reflections reflections = new Reflections(Main.class.getPackage().getName());

        int d = 0, converterReports = 0;
        Set<Class<? extends ConverterQA>> tests = reflections.getSubTypesOf(ConverterQA.class);
        for (Class<?> test : tests) {
            if (test.getAnnotation(Deprecated.class) != null) d++;
            else {
                for (Annotation annotation : test.getAnnotations()) {
                    if (annotation instanceof DiagramTest) {
                        diagramQATests.add(test);
                    } else if (annotation instanceof GraphTest) {
                        graphQATests.add(test);
                    } else if (annotation instanceof PostTest) {
                        finalQATests.add(test);
                    } else if (annotation instanceof ConverterReport) {
                        converterReports++;
                    }
                }
            }
        }
        int a = diagramQATests.size() + graphQATests.size() + finalQATests.size() + converterReports;
        int t = a + d;
        System.out.println(String.format("\r\t>%3d test%s found:", t, t == 1 ? "" : "s"));
        System.out.println(String.format("\t\t-%3d test%s active", a, a == 1 ? "" : "s"));
        System.out.println(String.format("\t\t-%3d test%s excluded ('@Deprecated')", d, d == 1 ? "" : "s"));

        //Cleans up previous reports
        try {
            FileUtils.cleanDirectory(new File(path));
            System.out.println("\t> Reports folder cleanup -> (done)");
        } catch (Exception e) {
            System.out.println("\t> No reports folder found -> it will be created.");
        }
        System.out.println();
    }

    public static void performDiagramTests(Diagram diagram) {
        for (Class test : diagramQATests) {
            try {
                DiagramQA qualityAssessment = (DiagramQA) test.newInstance();
                qualityAssessment.run(diagram);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void performGraphTests(Graph graph) {
        for (Class test : graphQATests) {
            try {
                GraphQA qualityAssessment = (GraphQA) test.newInstance();
                qualityAssessment.run(graph);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void performFinalTests(Diagram diagram, Graph graph) {
        for (Class test : finalQATests) {
            try {
                PostQA qualityAssessment = (PostQA) test.newInstance();
                qualityAssessment.run(diagram, graph);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeReports() {
        Reflections reflections = new Reflections(Main.class.getPackage().getName());
        Set<Class<? extends ConverterQA>> tests = reflections.getSubTypesOf(ConverterQA.class);

        List<Report> reports = new ArrayList<>();
        for (Class<?> test : tests) {
            if (test.getAnnotation(Deprecated.class) != null) continue;
            try {
                ConverterQA qa = (ConverterQA) test.newInstance();
                storeCSV(qa.getNumeratedName(), qa.getReport());
                reports.add( new Report(qa.getPriority(), qa.getNumeratedName(), qa.getDescription(), qa.getReport()));
            } catch (InstantiationException | IllegalAccessException e) { /*Nothing here*/}
        }

        //Printing the reports sorted by name
        System.out.println("· Reports:");
        reports.stream().sorted().forEach(Report::printColoured);
        System.out.println("\n· Priorities meaning:");
        QAPriority.list().forEach(QAPriority::printColoured);
        long c = reports.stream().filter(Report::hasEntries).count();
        System.out.println(String.format("\n· Reports summary: %d QA test%s generated reports.\n", c, c == 1 ? "" : "s"));
        //Storing the report in a CSV file
        storeReport(reports);
    }

    private static void storeCSV(String fileName, List<String> lines){
        if(!lines.isEmpty()){
            try {
                //Store the corresponding CSV file when content has been generated
                Files.write(getFilePath(fileName), lines, Charset.forName("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void storeReport(List<Report> reports) {
        if (reports.isEmpty()) return;
        List<String> lines = new ArrayList<>();
        lines.add(Report.getCSVHeader());
        lines.addAll(reports.stream()
                .filter(Report::toReport)
                .sorted()
                .map(Report::getCSV)
                .collect(Collectors.toList()));
        try {
            Files.write(getFilePath("DiagramQA_Summary_v" + version), lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getFilePath(String fileName) throws IOException {
        Path p = Paths.get(path + "/" + fileName + ".csv");
        Files.deleteIfExists(p);
        if (!Files.isSymbolicLink(p.getParent())) Files.createDirectories(p.getParent());
        Files.createFile(p);
        return p;
    }
}
