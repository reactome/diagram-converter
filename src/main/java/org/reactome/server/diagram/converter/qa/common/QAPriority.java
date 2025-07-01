package org.reactome.server.diagram.converter.qa.common;

import java.util.Arrays;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public enum QAPriority {

    ALARM   (0, "Converter failed and MUST be fixed",   "\033[0;34m"), //BLUE
    BLOCKER (1, "ALWAYS cause ERRORS when accessed",    "\033[1;31m"), //RED_BOLD
    HIGH    (2, "Cause ERRORS in certain scenarios",    "\033[0;31m"), //RED
    MEDIUM  (3, "Source of an UNDEFINED behaviour",     "\033[0;35m"), //PURPLE
    LOW     (4, "INCONSISTENT with the guidelines",     "\033[0;33m"); //YELLOW

    private static final String RESET_COLOUR = "\033[0m";

    public final Integer order;
    public final String name;
    public final String meaning;
    public final String colour;

    QAPriority(Integer order, String meaning, String colour) {
        this.order = order;
        this.name = toString();
        this.meaning = meaning;
        this.colour = colour;
    }

    public static List<QAPriority> list() {
        return Arrays.asList(values());
    }

    public static void printColoured(QAPriority p) {
        String line = String.format("\t%7s: %s", p, p.meaning);
        System.out.println(p.colour + line + RESET_COLOUR);
    }
}
