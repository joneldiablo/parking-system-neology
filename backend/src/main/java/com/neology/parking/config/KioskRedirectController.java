package com.neology.parking.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KioskRedirectController {

    @GetMapping(value = {"/kiosk", "/kiosk/"})
    public String reenviarALaAppDelKiosko() {
        return "redirect:/kiosk/index.html";
    }
}