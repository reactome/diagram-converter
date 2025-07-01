package org.reactome.server.diagram.converter.layout.output;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class Color {

    public Integer r;
    public Integer g;
    public Integer b;

    public Color(List<Integer> color) {
        this.r = color.get(0);
        this.g = color.get(1);
        this.b = color.get(2);
    }

    @JsonIgnore
    public boolean isReddish(){
       return r>248 && g<60 && b<60;
    }
}
