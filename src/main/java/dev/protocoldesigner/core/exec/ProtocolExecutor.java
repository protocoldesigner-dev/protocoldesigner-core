package dev.protocoldesigner.core.exec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import dev.protocoldesigner.core.exec.model.Jump;
import dev.protocoldesigner.core.exec.model.Machine;
import dev.protocoldesigner.core.exec.model.State;
import dev.protocoldesigner.core.exec.model.Subject;

/**
 * ProtocolExecutor
 * The executor of the protocol , which is implemented by a lbq at the present.
 * There are two phases, wait for accept and wait for complete.
 *
 * Name based, id is not used.
 */
public class ProtocolExecutor {
    private Logger log = LoggerFactory.getLogger(ProtocolExecutor.class);



    private BlockingQueue<ActionTask> events = new LinkedBlockingQueue<>();

    private ExecutorService executor         = Executors.newSingleThreadExecutor();
    private volatile boolean  shutdown       = false;
    
    private Map<String, Node>    nodes       = new HashMap<>();
    private static ObjectMapper mapper       = JsonMapper.builder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();

    /**
     * registered hooks for internal init stage
     * TODO remove this shit
     */
    private List<TriFunction<DefaultEvent, String, String>> initHooks=new LinkedList<>();
    /**
     * registered hooks for internal start stage
     * TODO remove this shit
     */
    private List<TriFunction<DefaultEvent, String, String>> startHooks=new LinkedList<>();
    // private ProtocolExecutor(){}
    private void load(Machine protocol){
        Map<String, Action> allActions  = new HashMap<>();
        List<Action> uncompletedActions  = new LinkedList<>();

        for(Subject subject : protocol.getSubjects().values()){
            Node n = new Node(subject.getName() ,subject.getStateNameById(subject.getInitial()), subject.stateNames());
            for(State state : subject.getStates().values()){
                for (Jump jump : state.getJumps().values()) {
                    Action a = new Action();
                    a.setName(jump.getName());
                    a.setStart(jump.getFromStateName());
                    a.setEnd(jump.getToStateName());
                    a.setPositive(jump.getPositive());
                    //binding
                    a.setNode(n);
                    n.addAction(a);

                    allActions.put(subject.getName() + ":" + a.getName(), a);
                    if(jump.getPeerJumps()!=null){
                        List<String> peers = jump.getPeerJumps().stream().map(c -> c.getSubject() + ":" + c.getJump()).toList();
                        if(peers.size()==0) continue;
                        a.setCascadeNames(peers);
                        uncompletedActions.add(a);
                    }
                }
            }
            nodes.put(subject.getName(), n);
        }

        //the cascades
        for(Action a: uncompletedActions){
            List<Action> cascades = a.getCascadeNames().stream().map( n -> allActions.get(n)).toList();
            a.setCascades(cascades);
        }
    }

    /**
     * @param content the protocol content, which is in json format
     */
    public ProtocolExecutor(String content){
        Machine m;
        try{
            m = mapper.readValue(content, Machine.class);
        }catch(JsonProcessingException e){
            throw new RuntimeException("invalid json format file", e);
        }
        this.load(m); 
    }
    
    /**
     * @param protocol the protocol to execute
     */
    public ProtocolExecutor(Machine protocol){
        this.load(protocol);
    }

    
    /**
     * get the event action of the event
     */
    private Action getEvent(DefaultEvent event){
        String node = event.getNode();
        String action = event.getAction();
        Node n = nodes.get(node);
        if(n==null)
            throw new IllegalArgumentException("No such node :" + node);
        Action a = n.getAction(action);
        if(a==null)
            throw new IllegalArgumentException(String.format("No such action : %s:%s", node, action));
        return a;
    }

    /**
     * return all the positive events that can be issued from outside.
     * format 
     *    node:action
     * TODO action name duplication
     */
    public List<String> getEvents(){
        return this.nodes.entrySet().stream().map(e->{
            return e.getValue().getActions().stream().map(action -> 
                    e.getKey() + ":" + action.getName() + (action.isPositive() ? ":+" : ":-")).toList();
        }).flatMap(l -> l.stream()).collect(Collectors.toList());
    }
    
    /**
     * add event and wait until event is <b>accepted</b>
     * TODO javadoc documentation
     */
    public boolean issueEvent(DefaultEvent event) throws IllegalArgumentException, InterruptedException{
        if(shutdown) return false;
        if(event==null) throw new NullPointerException("event is null!");
        if(event!=DefaultEvent.STOP) getEvent(event);   //get the related action to check if the event is valid
        
        ActionTask a = new ActionTask(event);
        events.add(a);
        if(!a.accepted()){
            log.debug("event dropped: {}", event);
            return false;
        }
        return true;
    }

    
    /**
     * {@code #issueEvent(String)} and wait until event is <b>processed</b>
     * TODO duplicated code
     */
    public boolean issueEventBlocking(DefaultEvent event) throws InterruptedException{
        if(shutdown) return false;
        if(event==null) throw new NullPointerException("event is null!");
        if(event!=DefaultEvent.STOP) getEvent(event);   //get the related action to check if the event is valid
        ActionTask a =  new ActionTask(event);
        events.add(a);

        if(!a.accepted()){
            log.debug("event dropped: {}", event);
            return false;
        }
        a.waitUntilProcessed();
        return true;
    }

    /**
     * register a hook of {@link ProtocolExecutor}  lifecycle  
     * supported :
     *  1. init 
     *  2. start
     * TODO extend
     */
    public void registerHook(DefaultEvent event, TriFunction<DefaultEvent, String, String> hook){
        if(event==DefaultEvent.INIT){
            this.initHooks.add(hook);
        }else if(event==DefaultEvent.START){
            this.startHooks.add(hook);
        }else{
            Action a =this.getEvent(event);
            if(a==null) throw new NullPointerException("cannot find event : "+ event);
            a.setHook(hook);
        }
    }
    /**
     * reuse this protocol executor?
     */
    private int clear(){
        int size= events.size();
        events.clear();
        return size;
    }

    /**
     * the init stage , the init hooks will be triggered
     */
    public void init(){
        for(TriFunction<DefaultEvent, String, String> hook: this.initHooks){
            hook.apply(DefaultEvent.INIT, "null", "inited");
        }
    }


    /**
     * the start stage , start the event process core and trigger the start hooks
     */
    public void start(){
        this.executor.submit(() -> {
            while(true){
                ActionTask event;
                try{
                    event = events.take();
                }catch(InterruptedException e){
                    log.info("protocol executor interrupted, {} events dropped .", clear());
                    return;
                }
                if(event.getEvent()==DefaultEvent.STOP){
                    log.info(" halting down protocol executor , {} events dropped", clear());
                    event.accept();
                    return;
                }
                
                Action currentAction;
                try{
                    currentAction = getEvent(event.getEvent());
                }catch(IllegalArgumentException e){
                    //TODO we have checked before, barely get here
                    log.error("error getting action of event", e);
                    event.drop();
                    continue;
                }
                if(!currentAction.isPositive()) throw new IllegalArgumentException("event not positive :" + event.getEvent());
                if(!currentAction.acceptable()){
                    event.drop();
                }else{
                    //complete first future
                    event.accept();
                    DefaultEvent e = event.getEvent();
                    try{
                        currentAction.doAction(event.getEvent());
                        //complete second future
                        event.finish();
                    }catch(Exception exception){
                        //fail the event and go on 
                        log.error("error doing action {}", event.getEvent(), exception);
                        event.fail();
                    }
                }
            }
        });
        for(TriFunction<DefaultEvent, String, String> hook: this.startHooks){
            hook.apply(DefaultEvent.START, "inited", "started");
        }
    }
    public void shutdown()throws InterruptedException{
        issueEvent(DefaultEvent.STOP);
        shutdown=true;
        executor.shutdown();
        log.info("protocol executor halted.");
    }

    public void shutdownNow(){
        log.info("halting down protocol executor, {} events dropped", clear());
        executor.shutdownNow();
    }
    

    
}

