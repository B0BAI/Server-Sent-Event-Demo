package io.bobaikato.demo.sse;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@Component
public class SseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter getEmitterByUserId(long userId) {
        return emitters.get(userId);
    }

    void sendMessage(Long id, Message message) {
        SseEmitter emitter = emitters.get(id);
        try {
            if (emitter != null) {
                try {
                    emitter.send(message, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    emitter.complete();
                    emitters.remove(id);
                    e.printStackTrace();
                }
            }
        } catch (java.util.ConcurrentModificationException exception) {
            System.out.println("HERE: " + exception);
        }
        System.out.println("Got message: " + message);
    }


    SseEmitter stream() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
        });
        return emitter;
    }
}
