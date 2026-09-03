package com.example.spring_boot_project_api.controller;

import com.example.spring_boot_project_api.dto.request.bakong.BakongRequest;
import com.example.spring_boot_project_api.dto.request.bakong.CheckTransactionRequest;
import com.example.spring_boot_project_api.service.BakongService;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bakong")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BakongController {

  private final BakongService bakongService;

  // POST /api/v1/bakong/generate-qr
  @PostMapping("/generate-qr")
  public ResponseEntity<KHQRResponse<KHQRData>> generateQR(@RequestBody BakongRequest request) {
    return ResponseEntity.ok(bakongService.generateQR(request));
  }

  // POST /api/v1/bakong/qr-image
  @PostMapping(value = "/qr-image", produces = MediaType.IMAGE_PNG_VALUE)
  public ResponseEntity<byte[]> getQRImage(@RequestBody KHQRData qrData) {
    return ResponseEntity.ok(bakongService.getQRImage(qrData));
  }

  // POST /api/v1/bakong/check-transaction
  @PostMapping("/check-transaction")
  public ResponseEntity<?> checkTransaction(@RequestBody CheckTransactionRequest request) {
    return ResponseEntity.ok(bakongService.checkTransactionByMD5(request));
  }
}