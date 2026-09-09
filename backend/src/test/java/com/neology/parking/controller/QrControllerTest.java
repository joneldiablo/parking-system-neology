package com.neology.parking.controller;

import com.neology.parking.repository.VehiculoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrControllerTest {

    @Mock private VehiculoRepository vehiculoRepository;

    @InjectMocks private QrController controller;

    @Test
    void placaNoValida_devuelveBadRequest() {
        ResponseEntity<byte[]> res = controller.generarQr("AB"); // < 3 caracteres

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void vehiculoNoRegistrado_devuelveNotFound() {
        when(vehiculoRepository.existsById("ZZZ999")).thenReturn(false);

        ResponseEntity<byte[]> res = controller.generarQr("ZZZ999");

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void vehiculoRegistrado_devuelvePng() {
        when(vehiculoRepository.existsById("NR001")).thenReturn(true);

        ResponseEntity<byte[]> res = controller.generarQr("NR001");

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_PNG);
        assertThat(res.getHeaders().getContentDisposition().getFilename())
                .isEqualTo("NR001-qr.png");
        byte[] body = res.getBody();
        assertThat(body).isNotNull();
        assertThat(body).startsWith((byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47); // PNG magic
    }
}