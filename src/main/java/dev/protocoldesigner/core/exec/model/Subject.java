
package dev.protocoldesigner.core.exec.model;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;

/**
 * Subject
 * 
 * TODO check unique for state names
 */
@Data
public class Subject extends GraphElement{
    private String initial;
    private Map<String, State> states;

    /**
     * @return set of state names
     */
    public Set<String> stateNames(){
        return this.states.values().stream().map(s -> s.getName()).collect(Collectors.toSet());
    }

    /**
     * get the name of state by state id
     * a conversion  id => name
     * TODO refactor this
     * @param id of state
     * @return state name
     */
    public String getStateNameById(String id){
        return states.get(id).getName();
    }
}
