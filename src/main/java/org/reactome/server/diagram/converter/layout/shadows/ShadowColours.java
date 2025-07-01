package org.reactome.server.diagram.converter.layout.shadows;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class ShadowColours {

    private static final List<String> shadows;

    static {
        shadows = new ArrayList<>();
        shadows.add("#4684ee");
        shadows.add("#dc3912");
//        shadows.add("#ff9900");
        shadows.add("#008000");
        shadows.add("#666666");
        shadows.add("#4942cc");
        shadows.add("#cb4ac5");
        shadows.add("#d6ae00");
        shadows.add("#336699");
        shadows.add("#dd4477");
        shadows.add("#aaaa11");
        shadows.add("#66aa00");
        shadows.add("#888888");
        shadows.add("#994499");
        shadows.add("#dd5511");
        shadows.add("#22aa99");
        shadows.add("#999999");
        shadows.add("#705770");
        shadows.add("#109618");
        shadows.add("#a32929");
    }

    public static String getShadow(int pos){
        return shadows.get(pos % shadows.size());
    }
}
