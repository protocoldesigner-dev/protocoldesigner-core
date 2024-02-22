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


    /**
     * get the action named <em>actionName</em>
     * @param actionName name of the action
     * @return the action
     */
    public Action getAction(String actionName){
        return actions.get(actionName);
    }
    /**
     * @return sets of all action names
     */
    public Set<String> getActionNames(){
        return actions.keySet();
    }
    /**
     * @return collection of all actions 
     */
    public Collection<Action> getActions(){
        return actions.values();
    }
    /**
     * @param name          node name
     * @param initialState  initial state name
     * @param states        state names
     *
     * @throws IllegalArgumentException when initialState is not in states
     */
    public Node(String name, String initialState, Set<String> states){
        this.name = name;
        if(!states.contains(initialState))
            throw new IllegalArgumentException("initial state not contained in the states");
        this.currentState = initialState;
        setStates(states);
    }
    /**
     * set the states
     * @param states the states
     */
    public void setStates(Set<String> states){
        this.states = states;
    }

    /**
     * add an action to node
     * @param a action
     */
    public void addAction(Action a){
        if(!this.states.contains(a.getStart()))
            throw new IllegalArgumentException("node " + name + " does not has state named " + a.getStart());
        this.actions.put(a.getName(), a);

    } 
    /**
     * @return state of the node
     */
    public String currentState(){
        return currentState;
    }
    /**
     * @param newState new state
     */
    public void transferTo(String newState){
        this.currentState = newState;
    }
}
