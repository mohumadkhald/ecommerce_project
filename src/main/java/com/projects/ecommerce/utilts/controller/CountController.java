package com.projects.ecommerce.utilts.controller;

import com.projects.ecommerce.utilts.CountAllService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api")
public class CountController {
    @Autowired
    private CountAllService  countAllService;


    @GetMapping("counts")
    public ResponseEntity<Map<String, Long>> getAllCounts() {
        return ResponseEntity.ok(countAllService.getCounts());
    }
}
