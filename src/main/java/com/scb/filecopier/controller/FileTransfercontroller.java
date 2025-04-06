package com.scb.filecopier.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.util.JSONPObject;

@RestController
@RequestMapping("/api")
public class FileTransfercontroller {
	
	@GetMapping("/copy")
    public String sayHello(@RequestParam String fileIdentifier) {
        return "Hello, " + fileIdentifier + "!";
    }
	
	@PostMapping("/transfer")
    public String greet(@RequestBody JSONPObject requestBody) {
        return "Welcome, " + requestBody.toString() + "!";
    }

}