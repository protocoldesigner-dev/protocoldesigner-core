package dev.protocoldesigner.core.exec.model;

import java.util.Map;

import lombok.Data;

/**
 * State
 */
@Data
public class State extends GraphElement{
    private Map<String, Jump> jumps;
}
