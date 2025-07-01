package org.reactome.server.diagram.converter.qa.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public abstract class AbstractConverterQA implements ConverterQA {

    @Override
    public final String getName(){
        return getClass().getSimpleName().split("_", 2)[1];
    }

    @Override
    public final String getNumeratedName() {
        return getPrefix() + getName();
    }

    private String getOrder() {
        return getClass().getSimpleName().split("_", 2)[0].replaceAll("\\D+", "");
    }

    protected abstract String getHeader();

    public List<String> getReport(Collection<String> lines) {
        if (!lines.isEmpty()) {
            List<String> rtn = new ArrayList<>(lines);
            rtn.add(0, getHeader());
            return rtn;
        }
        return Collections.emptyList();
    }

    private String getPrefix() {
        return "DT" + getOrder() + "-";
    }
}
