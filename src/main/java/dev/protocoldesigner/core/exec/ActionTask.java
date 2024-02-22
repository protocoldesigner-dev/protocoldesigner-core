package dev.protocoldesigner.core.exec;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This class represents a execution ,which is triggered 
 * by a {@link DefaultEvent} issued via {@link ProtocolExecutor#issueEvent(DefaultEvent)}
 * or {@link ProtocolExecutor#issueEventBlocking(DefaultEvent)}
 *
 */
public class ActionTask {
    private DefaultEvent event;
    /**
     * complete future
     */
    private CompletableFuture<Boolean> future       = new CompletableFuture<>();
    /**
     * accept future
     */
    private CompletableFuture<Boolean> acceptFuture = new CompletableFuture<>();

    /**
     * @return the related event
     */
    public DefaultEvent getEvent(){
        return event;
    }

    /**
     * @param event the event of this task
     */
    public ActionTask(DefaultEvent event){
        this.event = event;
    }

    /**
     * wait until the event is accepted or dropped. 
     * The event will then be processed after this method return true( accepted) 
     * @return if the event is accepted
     * @see #waitUntilProcessed()
     * @throws InterruptedException when interrupted
     */
    public boolean accepted() throws InterruptedException{
        try{
            return this.acceptFuture.get();
        }catch(ExecutionException e){
            throw new RuntimeException("Internal Error!");
        }
    }
    /**
     * wait until the event is processed
     * @throws InterruptedException when interrupted
     */
    public void waitUntilProcessed() throws  InterruptedException{
        try{
            this.future.get();
        }catch(ExecutionException e){
            throw new RuntimeException("Internal Error!");
        }
    }
    /**
     * drop the {@link acceptFuture}, which means the event was failed to issue. 
     * this may be caused by event not acceptable.
     */
    public void drop(){
        this.acceptFuture.complete(false);
    }
    /**
     * accept the event
     */
    public void accept(){
        this.acceptFuture.complete(true);
    }

    /**
     * the complele future result
     * finish()  => success
     * fail()    => failure
     */
    public void finish(){
        this.future.complete(true);
    }
    /**
     * only exception throwed from the related {@link Action} cause this to fail
     */
    public void fail(){
        this.future.complete(false);
    }
}
