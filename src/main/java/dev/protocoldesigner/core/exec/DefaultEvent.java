package dev.protocoldesigner.core.exec;

import lombok.Data;

/**
 * AbstractEvent
 * The default event class , inherit to customize runtime event parameters
 *
 * <p>
 * Two fields are used to identify a event: node and action, which measns 
 * the event is used to trigger the {@code action} of {@code node}.
 * Notice: There is no such a level to represent state of the node here
 */
@Data
public class DefaultEvent {
    /**
     * event
     */
    protected String node;
    /**
     * action
     */
    protected String action;
    /**
     * @param node the node
     * @param action the action
     */
    public DefaultEvent(String node, String action){
        this.node = node;
        this.action = action;
    }

    //TODO pause and resume
    /**
     * Here are several internal stages of the executor 
     * INIT -> START -> STOP
     */
    /**
     * INIT event, triggered via {@link ProtocolExecutor#init()}
     * @see ProtocolExecutor#registerHook(DefaultEvent, TriFunction)
     */
    public static DefaultEvent INIT  = new DefaultEvent("ProtocolExecutorInternal", "init");
    /**
     * START event, triggered via {@link ProtocolExecutor#start()}
     * @see ProtocolExecutor#registerHook(DefaultEvent, TriFunction)
     */
    public static DefaultEvent START = new DefaultEvent("ProtocolExecutorInternal", "start");
    /**
     * STOP event, triggered by issueEvent(STOP), cause the {@link ProtocolExecutor} to halt down.
     * <p>
     * There are no stop hooks now. This could be changed.
     */
    public static DefaultEvent STOP  = new DefaultEvent("ProtocolExecutorInternal", "stop");

    @Override
    public String toString(){
        return node+ ":" + action;
    }
}
