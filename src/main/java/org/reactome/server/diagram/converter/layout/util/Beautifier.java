package org.reactome.server.diagram.converter.layout.util;

import org.gk.model.GKInstance;
import org.reactome.server.diagram.converter.layout.output.DiagramObject;

import java.util.regex.Pattern;

/**
 * @author Kostas Sidiropoulos (ksidiro@ebi.ac.uk)
 */
public abstract class Beautifier {

    private static Pattern NAME_COPIED = Pattern.compile("(\\(name copied from entity in)[\\s\\S]*?[\\)]");
    private static Pattern COORDINATES_COPIED = Pattern.compile("(\\(the coordinates are copied over from)[\\s\\S]*?[\\)]");

    public static <T> T  processName(T input) {
        if(input instanceof DiagramObject) {
            DiagramObject object = (DiagramObject) input;
            String aux = NAME_COPIED.matcher(object.displayName).replaceFirst("");
            aux = COORDINATES_COPIED.matcher(aux).replaceFirst("");
            object.displayName = aux.trim();
            return (T) object;
        } else if(input instanceof GKInstance) {
            GKInstance object = (GKInstance) input;
            String aux = NAME_COPIED.matcher(object.getDisplayName()).replaceFirst("");
            aux = COORDINATES_COPIED.matcher(aux).replaceFirst("");
            object.setDisplayName(aux.trim());
            return (T) object;
        } else {
            return input;
        }
    }
}
