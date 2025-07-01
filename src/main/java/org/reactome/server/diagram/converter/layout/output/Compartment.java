package org.reactome.server.diagram.converter.layout.output;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class Compartment extends NodeCommon {

    public List<Long> componentIds;

    public Compartment(Object obj) {
        super(obj);
        for (Method method : obj.getClass().getMethods()) {
            switch (method.getName()){
                case "getComponents":
                    this.componentIds = getComponents(method, obj);
                    break;
            }
        }
    }
}
