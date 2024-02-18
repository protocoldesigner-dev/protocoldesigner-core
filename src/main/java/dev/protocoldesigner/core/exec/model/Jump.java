package dev.protocoldesigner.core.exec.model;

import java.util.List;

import lombok.Data;

/**
 * Jump
 */
@Data
public class Jump extends GraphElement{
    private String fromStateName;
    private String toStateName;
    private Boolean positive;
    private List<Connector> peerJumps;
}
