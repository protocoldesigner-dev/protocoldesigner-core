package dev.protocoldesigner.core.exec.model;

import lombok.Data;

/**
 * This class represnts a graph element 
 * when editing the graph at protocoldesigner.dev.
 * 
 */
@Data
public class GraphElement {
    /**
     *  id of the graph element
     */
    protected String id;
    /**
     * name of the graph element
     */
    protected String name;
}
