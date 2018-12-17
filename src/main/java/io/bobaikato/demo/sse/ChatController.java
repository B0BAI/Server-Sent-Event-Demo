package io.bobaikato.demo.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
public class ChatController {

    final private SseService sseService;

    @Autowired
    public ChatController(SseService sseService) {
        this.sseService = sseService;
    }

    @PostMapping(path = "/chat/{id}", consumes = "application/json", produces = "application/json")
    public void sendMessage(@RequestBody @PathVariable("id") Long id, Message message) {
        sseService.sendMessage(id, message);
    }

    @GetMapping(path = "/stream/{id}")
    public SseEmitter stream(@PathVariable("id") Long id) {
        return sseService.stream(id);
    }
}
