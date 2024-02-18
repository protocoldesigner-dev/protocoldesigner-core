package dev.protocoldesigner.core.exec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Node
 * 
 */
public class Node {
    private String name;
    
    private Set<String> states;
    
    private String currentState;
    
    /*
     * the structure at runtime is a little different to the json: the action is mounted on 
     * the node(subject)
     */
    private Map<String, Action> actions = new HashMap<>();


    public Action getAction(String actionName){
        return actions.get(actionName);
    }
    public Set<String> getActionNames(){
        return actions.keySet();
    }
    public Collection<Action> getActions(){
        return actions.values();
    }
    /**
     * @param name          node name
     * @param initialState  initial state name
     * @parm  states        state names
     */
    public Node(String name, String initialState, Set<String> states){
        this.name = name;
        if(!states.contains(initialState))
            throw new IllegalArgumentException("initial state not contained in the states");
        this.currentState = initialState;
        setStates(states);
    }
    public void setStates(Set<String> states){
        this.states = states;
    }

    public void addAction(Action a){
        if(!this.states.contains(a.getStart()))
            throw new IllegalArgumentException("node " + name + " does not has state named " + a.getStart());
        this.actions.put(a.getName(), a);

    } 
    public String currentState(){
        return currentState;
    }
    public void transferTo(String newState){
        this.currentState = newState;
    }
}
