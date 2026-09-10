package com.neology.parking.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class SpaErrorController implements ErrorController {

    /**
     * Rutas del cliente (Angular). Al recargar o entrar directo sirven el index.html
     * con estado 200 para que el router del frontend retome la navegación.
     */
    @GetMapping({"/login", "/vehiculos", "/estancias", "/registro", "/pagos", "/usuarios"})
    public String clientRoutes() {
        return "forward:/index.html";
    }

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, HttpServletResponse response) {
        String originalUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        boolean isApiCall = originalUri != null && originalUri.startsWith("/neo");

        if (status != null && status == 404 && !isApiCall) {
            response.setStatus(HttpServletResponse.SC_OK);
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