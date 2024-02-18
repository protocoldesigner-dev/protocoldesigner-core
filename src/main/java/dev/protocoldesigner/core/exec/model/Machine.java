package dev.protocoldesigner.core.exec.model;

import java.util.Map;

import lombok.Data;

/**
 * Machine
 */
@Data
public class Machine {
    private Map<String, Subject> subjects;
}
