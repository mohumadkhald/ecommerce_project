package com.projects.ecommerce.utilts;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class StatusController {

    @GetMapping("/status-stream")
    public SseEmitter stream() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // never timeout

        // Send initial event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")   // <== must match Angular
                    .data("BACKEND_UP"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        return emitter;
    }

}
