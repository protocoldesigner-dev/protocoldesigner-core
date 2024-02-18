package dev.protocoldesigner.core.exec;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LoadTest
 */
public class BasicTest {
    private Logger log = LoggerFactory.getLogger(BasicTest.class);
    
    @Test
    public void load() throws Exception{
        String content;
        try(InputStream is = BasicTest.class.getClassLoader().getResourceAsStream("loadTest.json")){
            content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        ProtocolExecutor exec = new ProtocolExecutor(content);
        exec.init();
        exec.start();
        log.info(exec.getEvents().toString());
        exec.shutdown();
    }

    
}
