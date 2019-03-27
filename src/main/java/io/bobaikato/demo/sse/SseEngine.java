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

@Data
@NoArgsConstructor
@Component
public class SseEngine {

    private final Map<Long, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    private final static Log LOGGER = LogFactory.getLog(SseEngine.class);

    synchronized void sendMessage(Long id, Message message) {
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
            LOGGER.info(String.format("(Sending Msg) Concurrent Modification Exception: %s", e));
        } catch (NullPointerException e) {
            /* *
             * Recommendation: Log Exception and since there isn't
             * any ID to accept msg save in DB or msg will be lost
             * */
            LOGGER.warn("No Stream instance, data should be stored in DB");
        }
    }

    private synchronized void stream(Long id, SseEmitter sseEmitter) {
        emittersMap.put(id, new Vector<>() {{
            add(sseEmitter);
        }});

        var emitterList = emittersMap.get(id);

        sseEmitter.onCompletion(() -> {
            synchronized (emitterList) {
                emitterList.remove(sseEmitter);
            }
        });
        sseEmitter.onTimeout(() -> emitterList.get(emitterList.indexOf(sseEmitter)).complete());
    }

    synchronized SseEmitter stream(Long id) {
        var emitterList = emittersMap.get(id);
        var sseEmitter = new SseEmitter();
        try {
            if (emitterList.isEmpty()) {
                stream(id, sseEmitter);
            } else {
                emitterList.add(sseEmitter);
            }
        } catch (NullPointerException e) {
            LOGGER.warn("ID has no Stream instance");
            stream(id, sseEmitter);
        }
        return sseEmitter;
    }
}

