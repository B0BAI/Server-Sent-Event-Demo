package io.bobaikato.demo.sse;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class ChatController {

    final private CustomEmitterService customEmitterService;

    @Autowired
    public ChatController(CustomEmitterService customEmitterService) {
        this.customEmitterService = customEmitterService;
    }

    @PostMapping(path = "/chat", consumes = "application/json", produces = "application/json")
    public Message sendMessage(@RequestBody Message message) {
        return customEmitterService.sendMessage(message);
    }

    @GetMapping(path = "/loops")
    public void sendMessages() {
        for (int a = 0; a <= 100; ++a) {
            customEmitterService.sendMessage(new Message("Bobai", "Hey " + a));
        }
    }


    @GetMapping(path = "/stream")
    public SseEmitter stream() {
        return customEmitterService.stream();
    }

    @GetMapping(path = "/streams")
    public SseEmitter streams() {
        return customEmitterService.stream();
    }
}
