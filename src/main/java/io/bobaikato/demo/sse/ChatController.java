package io.bobaikato.demo.sse;

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


    @GetMapping(path = "/stream")
    public SseEmitter stream() {
        return customEmitterService.stream();
    }
}
