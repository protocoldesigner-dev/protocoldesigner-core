package dev.protocoldesigner.core.exec;

/**
 * The {@code TriFunction} class is mainly used as a placeholder for the 
 * hook in {@code Action}
 */
@FunctionalInterface
public interface TriFunction<A, B, C> {

    /**
     * the function prototype to be registered
     *
     * @param a  arg0
     * @param b1 arg1
     * @param b2 arg2
     *
     * @see ProtocolExecutor#registerHook(DefaultEvent, TriFunction)
     */
    void apply(A a, B b1, C b2);
    
}
