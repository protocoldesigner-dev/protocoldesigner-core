package dev.protocoldesigner.core.exec;

import lombok.Data;

/**
 * AbstractEvent
 * The default event class , inherit to customize runtime event parameters
 * Notice: There is no such a level to represent state of the node here
 */
@Data
public class DefaultEvent {
    protected String node;
    protected String action;
    public DefaultEvent(String node, String action){
        this.node = node;
        this.action = action;
    }

    //TODO pause and resume
    public static DefaultEvent INIT  = new DefaultEvent("ProtocolDesignerInternal", "init");
    public static DefaultEvent START = new DefaultEvent("ProtocolDesignerInternal", "start");
    public static DefaultEvent STOP  = new DefaultEvent("ProtocolDesignerInternal", "stop");

    @Override
    public String toString(){
        return node+ ":" + action;
    }
}
