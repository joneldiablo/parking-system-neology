package com.neology.parking.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Controller
public class KioskRedirectController {

    @GetMapping(value = {"/kiosk", "/kiosk/"})
    public void reenviarALaAppDelKiosko(HttpServletResponse response) throws IOException {
        // Redirect relativo puro: detrás de un reverse proxy (https) Tomcat
        // absolutaría la URL con el host interno (127.0.0.1:8082) y rompería el kiosko.
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", "/kiosk/index.html");
    }
}