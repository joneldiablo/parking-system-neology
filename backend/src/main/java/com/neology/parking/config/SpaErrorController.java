package com.neology.parking.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        String originalUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        boolean isApiCall = originalUri != null && originalUri.startsWith("/neo");

        if (status != null && status == 404 && !isApiCall) {
            return "forward:/index.html";
        }

        int code = status == null ? 500 : status;
        return ResponseEntity.status(code)
                .body(Map.of(
                        "status", code,
                        "error", "Error en el servidor",
                        "path", originalUri == null ? "" : originalUri
                ));
    }
}