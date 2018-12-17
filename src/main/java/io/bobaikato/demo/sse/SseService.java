package io.bobaikato.demo.sse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

@Service
public class SseService {

    private List<SseEmitter> emitters = new Vector<>();

    Message sendMessage(Message message) {
        System.out.println("Got message: " + message);
        try {
            emitters.parallelStream().forEach(emitter -> {
                try {
                    emitter.send(message, MediaType.APPLICATION_JSON);
                } catch (IOException e) {
                    emitter.complete();
                    emitters.remove(emitter);
                    e.printStackTrace();
                }
            });
        } catch (java.util.ConcurrentModificationException exception) {
            System.out.println("HERE: " + exception);
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
