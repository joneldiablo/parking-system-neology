package com.neology.parking.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.neology.parking.repository.VehiculoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@RestController
@RequestMapping("/neo")
@RequiredArgsConstructor
@Tag(name = "QR de vehículo", description = "Generación de códigos QR para el kiosko de acceso")
public class QrController {

    private static final int SIZE = 300;
    private final VehiculoRepository vehiculoRepository;

    @GetMapping(value = "/qr/{placa}", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Obtener QR de una placa registrada (etiqueta para parabrisas)")
    public ResponseEntity<byte[]> generarQr(@PathVariable String placa) {
        String placaNormalizada = placa.trim().toUpperCase();
        if (!placaNormalizada.matches("^[A-Z0-9]{3,10}$")) {
            return ResponseEntity.badRequest().build();
        }
        if (!vehiculoRepository.existsById(placaNormalizada)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            Map<EncodeHintType, Object> hints = Map.of(
                    EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                    EncodeHintType.CHARACTER_SET, "UTF-8",
                    EncodeHintType.MARGIN, 1
            );
            BitMatrix matrix = new MultiFormatWriter()
                    .encode(placaNormalizada, BarcodeFormat.QR_CODE, SIZE, SIZE, hints);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + placaNormalizada + "-qr.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(out.toByteArray());
        } catch (WriterException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}