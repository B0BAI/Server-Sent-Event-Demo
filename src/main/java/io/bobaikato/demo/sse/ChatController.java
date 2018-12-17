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

    @GetMapping(path = "/loops")
    public void sendMessages() {
        for (int a = 0; a <= 100; ++a) {
            sseService.sendMessage(new Message("Bobai", "Hey " + a));
        }
    }


    @GetMapping(path = "/stream")
    public SseEmitter stream() {
        return sseService.stream();
    }

    @GetMapping(path = "/streams")
    public SseEmitter streams() {
        return sseService.stream();
    }
}
