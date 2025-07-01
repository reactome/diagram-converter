package org.reactome.server.diagram.converter.layout.util;

import java.io.Serializable;
import java.util.*;

/**
 * @author Antonio Fabregat (fabregat@ebi.ac.uk)
 */
public class MapSet<S,T> implements Serializable {

    protected Map<S, Set<T>> map = new HashMap<>();

    public void add(S identifier, T elem){
        map.computeIfAbsent(identifier, k -> new HashSet<>()).add(elem);
    }

    public void add(S identifier, Collection<T> collection){
        map.computeIfAbsent(identifier, k -> new HashSet<>()).addAll(collection);
    }

    public Set<T> getElements(S identifier){
        return map.get(identifier);
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }

    public Collection<T> values(){
        Set<T> rtn = new HashSet<>();
        for (S s : map.keySet()) {
            rtn.addAll(map.get(s));
        }
        return rtn;
    }
}
