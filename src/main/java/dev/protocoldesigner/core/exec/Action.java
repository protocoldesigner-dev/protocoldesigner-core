package dev.protocoldesigner.core.exec;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

/**
 * Do we have to add dynamic evaluated assertions?
 */
@Data
public class Action {
    private String start, end;
    /**
     * name
     */
    private String name;
    /**
     * the registered hook
     */
    // private ActionHook hook;
    private TriFunction<DefaultEvent, String, String> hook;
    // private Function<, B> h;

    
    // private hook.apply();
    /**
     * belonged node
     */
    private Node node;
    
    /**
     * if positive action
     */
    private boolean positive;
    
    /**
     * cascades
     */
    private List<Action> cascades = new LinkedList<>();

    /**
     * names
     */
    private List<String> cascadeNames = null;
    
    /**
     * @return whether this action has cascaded actions
     */
    protected boolean hasCascades(){
        return cascadeNames!=null && cascadeNames.size()>0; 
    }

    /**
     * if the action cannot be operated , do nothing
     * @param event current positive event
     */
    public void doAction(DefaultEvent event){
        //Unaccecptable event at the present
        if(!node.currentState().equals(this.start))
            return;
        node.transferTo(this.end);

        if(this.hook!=null)
            this.hook.apply(event, start, end);

        for(Action a : cascades){
            a.doAction(event);
        }
    }

    /**
     * If the state of node this action belongs to isn't equal 
     * to start state of this action, this action is not acceptable.
     * @return whether then this action is acceptable. 
     */
    public boolean acceptable(){
        return node.currentState().equals(this.start) ;
    }

    
}
