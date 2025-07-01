package org.reactome.server.diagram.converter.layout.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.reactome.server.diagram.converter.graph.output.Graph;
import org.reactome.server.diagram.converter.layout.output.Diagram;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is responsible for storing the diagram and the graph in JSON format.
 * For every file we create 2 files: one under the name of the pathway's DBID and a second
 * one having the name of the pathway's Stable Identifier. Please note that the second file
 * is only a symbolic link to the first one.
 * This functionality is used only when we create all diagrams (and graphs) during release.
 *
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class JsonWriter {

    @SuppressWarnings("FieldCanBeLocal")
    private static Boolean ENABLE_INDENT = true;

    private static ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        enableIndent(ENABLE_INDENT);
    }

    private static void enableIndent(boolean enableIndent) {
        if (enableIndent) {
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
        } else {
            mapper.disable(SerializationFeature.INDENT_OUTPUT);
        }
    }

    public static void serialiseDiagram(Diagram diagram, String outputDirectory){
        File outJSONFile = new File(outputDirectory + File.separator + diagram.getStableId() + ".json");
        File outLinkedFile = new File(outputDirectory + File.separator + diagram.getDbId() + ".json");
        try {
            serialise(diagram, outJSONFile, outLinkedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void serialiseGraph(Graph graph, String outputDirectory){
        File outGraphFile = new File(outputDirectory + File.separator + graph.getStId() + ".graph.json");
        File outLinkedFile = new File(outputDirectory + File.separator + graph.getDbId() + ".graph.json");
        try {
            serialise(graph, outGraphFile, outLinkedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void serialise(Object obj, File file, File linkedFile) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mapper.writeValue(byteArrayOutputStream, obj);

        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        byteArrayOutputStream.writeTo(fileOutputStream);

        //Create symbolicLink
        if (!Files.exists(Paths.get(linkedFile.getAbsolutePath()))) {
            Files.createSymbolicLink(Paths.get(linkedFile.getAbsolutePath()), Paths.get(file.getName()));
        }
    }
}
