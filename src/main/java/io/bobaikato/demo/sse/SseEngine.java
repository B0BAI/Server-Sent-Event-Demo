package io.bobaikato.demo.sse;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

@Data
@NoArgsConstructor
@Component
public class SseEngine {

    private final Map<Long, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    private final static Log LOGGER = LogFactory.getLog(SseEngine.class);

    void sendMessage(Long id, Message message) {
        var emitterList = emittersMap.get(id);
        try {
            if (!emitterList.isEmpty()) {
                emitterList.parallelStream().forEach(emitter -> {
                    try {
                        emitter.send(message, MediaType.APPLICATION_JSON);
                    } catch (IOException e) {
                        emitter.complete();
                        emitterList.remove(emitter);
                    }
                });
            }
        } catch (ConcurrentModificationException e) {
            LOGGER.info(format("Sending Msg... Concurrent Modification Exception: %s", e));
        } catch (NullPointerException e) {
            /* *
             * Recommendation: Log Exception and since there isn't
             * any ID to accept msg save in DB or msg will be lost
             * */
            LOGGER.warn("No Stream instance, data should be stored in DB");
        }
    }

    private void stream(Long id, SseEmitter sseEmitter) {
        LOGGER.info(format("Creating new Stream Instance with Emitter list for ID: %d", id));
        emittersMap.put(id, new Vector<>() {{
            add(sseEmitter);
        }});

        var emitterList = emittersMap.get(id);
        LOGGER.info("Adding to onCompletion Listener.");
        sseEmitter.onCompletion(() -> {
            synchronized (emitterList) {
                emitterList.remove(sseEmitter);
            }
        });

        LOGGER.info("Adding Emitter to onTimeout Listener.");
        sseEmitter.onTimeout(() -> emitterList.get(emitterList.indexOf(sseEmitter)).complete());
    }

    SseEmitter stream(Long id) {
        var emitterList = emittersMap.get(id);
        var sseEmitter = new SseEmitter();
        try {
            if (emitterList.isEmpty()) {
                LOGGER.info(format("ID: %d has no Emitter List.", id));
                stream(id, sseEmitter);
            } else {
                LOGGER.info(format("Adding new Emitter for ID: %d, to Emitter List.", id));
                emitterList.add(sseEmitter);
            }
        } catch (NullPointerException e) {
            LOGGER.warn(format("ID: %d, has no Emitter instance.", id));
            stream(id, sseEmitter);
        }
        return sseEmitter;
    }
}

