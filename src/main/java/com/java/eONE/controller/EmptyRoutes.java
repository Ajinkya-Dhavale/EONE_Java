package com.java.eONE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmptyRoutes {
    
    @GetMapping("/")
    public String getGello()
    {
        return "index";
    }
}
