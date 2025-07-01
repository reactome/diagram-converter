package org.reactome.server.diagram.converter.layout.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger("converter");

    public static void checkFileName(String fileName) {
        File file = new File(fileName);

        if (file.isDirectory()) {
            String msg = fileName + " is a folder. Please specify a valid file name.";
            System.err.println(msg);
            logger.error(msg);
            System.exit(1);
        }

        if (file.getParent() == null) {
            file = new File("./" + fileName);
        }
        Path parent = Paths.get(file.getParent());
        if (!Files.exists(parent)) {
            String msg = parent + " does not exist.";
            System.err.println(msg);
            logger.error(msg);
            System.exit(1);
        }

        if (!file.getParentFile().canWrite()) {
            String msg = "No write access in " + file.getParentFile();
            System.err.println(msg);
            logger.error(msg);
            System.exit(1);
        }

        logger.info(fileName + " is a valid file name");
    }

    public static boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists() && !file.isDirectory();
    }

    public static String checkFolderName(String folderName) {
        Path folder = Paths.get(folderName);

        if (!Files.exists(folder)) {
            String msg = folder + " does not exist. Please specify a valid directory name.";
            System.err.println(msg);
            logger.error(msg);
            System.exit(1);
        }

        logger.info(folderName + " is a valid directory name");

        if (folderName.endsWith(File.separator)) {
            return folderName.substring(0, folderName.length() - 1);
        } else {
            return folderName;
        }

    }

    public static int deleteAllSymbolicFiles(String folderToCheck) {
        File targetFolder = new File(folderToCheck);
        int count = 0;
        for (File file : targetFolder.listFiles()) {
            Path targetFile = file.toPath();
            if (Files.isSymbolicLink(targetFile)) {
                if (file.delete()) {
                    count++;
                }
            }
        }
        return count;
    }

    private static String getFileExtension(String fullPath) {
        int dot = fullPath.lastIndexOf(".");
        return fullPath.substring(dot + 1);
    }

    private static List<File> listAllFilesInFolder(String folderToCheck, String fileExtension) {
        return listAllFilesInFolder(new File(folderToCheck), fileExtension);
    }

    private static List<File> listAllFilesInFolder(File folderToCheck, String fileExtension) {
        List<File> filesListToreturn = new ArrayList<>();
        File[] listOfFiles = folderToCheck.listFiles();

        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    if (fileExtension != null && !fileExtension.isEmpty()) {
                        if (getFileExtension(listOfFile.getName()).equalsIgnoreCase(fileExtension)) {
                            filesListToreturn.add(listOfFile);
                        }
                    }
                } //else if (listOfFile.isDirectory()) {
                //ignore folders for now
                //}
            }
        }
        return filesListToreturn;
    }

    public static List<String> readTextFile(String fileLocation) throws IOException {
        Path path = Paths.get(fileLocation);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        if (lines != null) {
            return lines;
        } else {
            // to avoid any unexpected null lists
            return new ArrayList<String>();
        }
    }
}
