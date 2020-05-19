package com.push.snakeeyes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;

@Controller
public class IndexController {
	
	@Operation(summary = "Exists only to load index.html")
    @GetMapping("/")
    public String index() {
        return "index";
    }

}
