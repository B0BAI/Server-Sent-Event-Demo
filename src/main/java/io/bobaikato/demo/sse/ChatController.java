package io.bobaikato.demo.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class ChatController {

    final private EmitterService emitterService;

    @Autowired
    public ChatController(EmitterService emitterService) {
        this.emitterService = emitterService;
    }

    @PostMapping(path = "/chat", consumes = "application/json", produces = "application/json")
    public Message sendMessage(@RequestBody Message message) {
        return emitterService.sendMessage(message);
    }

    @GetMapping(path = "/loops")
    public void sendMessages() {
        for (int a = 0; a <= 100; ++a) {
            emitterService.sendMessage(new Message("Bobai", "Hey " + a));
        }
    }


    @GetMapping(path = "/stream")
    public SseEmitter stream() {
        return emitterService.stream();
    }

    @GetMapping(path = "/streams")
    public SseEmitter streams() {
        return emitterService.stream();
    }
}
