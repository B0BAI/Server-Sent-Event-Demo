package io.bobaikato.demo.sse;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@Component
public class SseService {

    private final Map<Long, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    void sendMessage(Long id, Message message) {
        List<SseEmitter> emitterList = emittersMap.get(id);
        try {
            if (!emitterList.isEmpty()) {
                emitterList.parallelStream().forEach(emitter -> {
                    try {
                        emitter.send(message, MediaType.APPLICATION_JSON);
                    } catch (IOException e) {
                        emitter.complete();
                        emittersMap.remove(id);
                    }
                });
            }
        } catch (java.util.ConcurrentModificationException e) {
            e.printStackTrace();
        }
        System.out.println("Got message: " + message);
    }

    private void stream(Long id, List<SseEmitter> emitterList, SseEmitter sseEmitter) {
        emittersMap.put(id, new Vector<>() {{
            add(sseEmitter);
        }});
        sseEmitter.onCompletion(() -> emitterList.remove(sseEmitter));
        sseEmitter.onTimeout(() -> emitterList.get(emitterList.indexOf(sseEmitter)).complete());
    }

    SseEmitter stream(Long id) {
        List<SseEmitter> emitterList = emittersMap.get(id);
        SseEmitter sseEmitter = new SseEmitter();
        System.out.println(emitterList);
        try {
            if (emitterList.isEmpty()) {
                stream(id, emitterList, sseEmitter);
            } else {
                emitterList.add(sseEmitter);
                emittersMap.put(id, emitterList);
            }
        } catch (NullPointerException e) {
            stream(id, emitterList, sseEmitter);
        }
        return sseEmitter;
    }
}

