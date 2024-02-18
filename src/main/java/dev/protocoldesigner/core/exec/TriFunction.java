package dev.protocoldesigner.core.exec;

/**
 * TriFunction
 */
@FunctionalInterface
public interface TriFunction<A, B, C> {

    void apply(A a, B b1, C b2);
    
}
