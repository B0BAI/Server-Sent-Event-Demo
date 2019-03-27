package io.b0bai.demo.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class ChatController {

    final private SseEngine sseEngine;

    @Autowired
    public ChatController(SseEngine sseEngine) {
        this.sseEngine = sseEngine;
    }

    @PostMapping(path = "/chat/{id}", consumes = "application/json", produces = "application/json")
    public void sendMessage( @PathVariable("id") Long id, @RequestBody Message message) {
        sseEngine.sendMessage(id, message);
    }

    @GetMapping(path = "/stream/{id}")
    public SseEmitter stream(@PathVariable("id") Long id) {
        return sseEngine.stream(id);
    }
}
