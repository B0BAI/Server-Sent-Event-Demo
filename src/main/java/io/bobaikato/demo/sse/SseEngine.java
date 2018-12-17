package io.bobaikato.demo.sse;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ConcurrentModificationException;

import java.io.IOException;
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
            LOGGER.info(String.format("(Sending Msg) Concurrent Modification Exception: %s", e));
        } catch (NullPointerException e) {
            /* *
             * Recommendation: Log Exception and since there isn't
             * any ID to accept msg save in DB or msg will be lost
             * */
        }
    }

    private void stream(Long id, List<SseEmitter> emitterList, SseEmitter sseEmitter) {
        emittersMap.put(id, new Vector<>() {{
            add(sseEmitter);
        }});
        sseEmitter.onCompletion(() -> emitterList.remove(sseEmitter));
        sseEmitter.onTimeout(() -> emitterList.get(emitterList.indexOf(sseEmitter)).complete());
    }

    SseEmitter stream(Long id) {
        var emitterList = emittersMap.get(id);
        var sseEmitter = new SseEmitter();
        try {
            if (emitterList.isEmpty()) {
                stream(id, emitterList, sseEmitter);
            } else {
                emitterList.add(sseEmitter);
            }
        } catch (NullPointerException e) {
            stream(id, emitterList, sseEmitter);
        }
        return sseEmitter;
    }
}

