package io.bobaikato.demo.sse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomEmitterService {

    private final List<SseEmitter> emitters = new ArrayList<>();

    Message sendMessage(Message message) {
        System.out.println("Got message: " + message);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(message, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
                //e.printStackTrace();
            }
        }
        return message;
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
