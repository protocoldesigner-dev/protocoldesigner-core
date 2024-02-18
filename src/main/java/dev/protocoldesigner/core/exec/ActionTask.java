package dev.protocoldesigner.core.exec;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The task of action to be processed
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

    public DefaultEvent getEvent(){
        return event;
    }

    public ActionTask(DefaultEvent event){
        this.event = event;
    }

    public boolean accepted() throws InterruptedException{
        try{
            return this.acceptFuture.get();
        }catch(ExecutionException e){
            throw new RuntimeException("Internal Error!");
        }
    }
    public void waitUntilProcessed() throws  InterruptedException{
        try{
            this.future.get();
        }catch(ExecutionException e){
            throw new RuntimeException("Internal Error!");
        }
    }
    public void drop(){
        this.acceptFuture.complete(false);
    }
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
