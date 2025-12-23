package org.reactome.server.diagram.converter.verifier;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.reactome.release.verifier.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Weiser (joel.weiser@oicr.on.ca)
 * Created 1/28/2025
 */
public class DiagramConverterVerifier implements Verifier {
    private DefaultVerificationLogic defaultVerificationLogic;
    private int expectedFileCount;

    public static void main(String[] args) throws IOException {
        Verifier verifier = new DiagramConverterVerifier();
        verifier.parseCommandLineArgs(args);
        verifier.run();
    }

    public DiagramConverterVerifier() {
        this.defaultVerificationLogic = new DefaultVerificationLogic(getStepName());
    }

    @Override
    public ParsedArguments parseCommandLineArgs(String[] args) {
        ParsedArguments config = this.defaultVerificationLogic.parseCommandLineArgs(args, getCommandLineParameters());
        this.expectedFileCount = config.getInt("expectedFileCount");
        return config;
    }

    @Override
    public List<CommandLineParameter> getCommandLineParameters() {
        List<CommandLineParameter> commandLineParameters = new ArrayList<>(this.defaultVerificationLogic.defaultParameters());
        commandLineParameters.add(
            CommandLineParameter.create(
                "expectedFileCount",
                OptionType.INTEGER,
                "",
                CommandLineParameter.IS_REQUIRED,
                'c',
                "expectedFileCount",
                "The number of files expected in the diagram tarball file"
            )
        );
        return commandLineParameters;
    }

    @Override
    public Results verifyStepRanCorrectly() throws IOException {
        Results finalResults = this.defaultVerificationLogic.verify();
        if (!finalResults.hasErrors()) {
            finalResults.mergeResults(verifyDiagramFolderHasCorrectNumberOfFiles());
        }
        return finalResults;
    }

    @Override
    public String getStepName() {
        return "diagram_converter";
    }

    private Results verifyDiagramFolderHasCorrectNumberOfFiles() {
        Results results = new Results();

        String diagramsFilePath = getDiagramsFilePath();
        if (hasExpectedFileCount(diagramsFilePath)) {
            results.addInfoMessage(diagramsFilePath + " has " + getFileCount(diagramsFilePath) + " contained files");
        } else {
            results.addErrorMessage(
                diagramsFilePath + " has less than expected file count of " + getExpectedFileCount() + ": " +
                    "only " + getFileCount(diagramsFilePath) + " files"
            );
        }

        return results;
    }

    private String getDiagramsFilePath() {
        return Paths.get(getOutputDirectory(), "diagrams.tgz").toString();
    }

    private String getOutputDirectory() {
        return defaultVerificationLogic.getOutputDirectory();
    }

    private boolean hasExpectedFileCount(String diagramsFilePath) {
        return getFileCount(diagramsFilePath) >= getExpectedFileCount();
    }

    private int getFileCount(String diagramsFilePath) {
        int fileCount = 0;

        try (FileInputStream fis = new FileInputStream(diagramsFilePath);
             GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {

            TarArchiveEntry entry;
            while ((entry = tarIn.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    fileCount++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to read tar file " + diagramsFilePath + " or its entries", e);
        }

        return fileCount;
    }

    private int getExpectedFileCount() {
        return this.expectedFileCount;
    }
}
