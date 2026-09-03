package com.example.spring_boot_project_api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.example.spring_boot_project_api.dto.request.bakong.BakongRequest;
import com.example.spring_boot_project_api.dto.response.bakong.BakongResponse;
import com.example.spring_boot_project_api.service.BakongService;
import com.example.spring_boot_project_api.service.BakongTokenService;
import com.example.spring_boot_project_api.dto.request.bakong.CheckTransactionRequest;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.KHQRCurrency;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import kh.gov.nbc.bakong_khqr.model.IndividualInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BakongServiceImpl implements BakongService {

  @Value("${bakong.account-id}")
  private String bakongAccountId;

  @Value("${bakong.base-url}")
  private String baseUrl;

  private final RestClient restClient = RestClient.create();
  private final ObjectMapper mapper;
  private final BakongTokenService bakongTokenService;

  @Override
  public KHQRResponse<KHQRData> generateQR(BakongRequest bakongRequest) {

    IndividualInfo individualInfo = new IndividualInfo();

    // 1. Set Expiration Timestamp (Required for dynamic KHQR with amounts)
    Long expMinutes = bakongRequest.expirationTimestamp() != null ? bakongRequest.expirationTimestamp() : 15L;
    individualInfo.setExpirationTimestamp(System.currentTimeMillis() + expMinutes * 60 * 1000);

    // 2. Account ID
    String accountId = bakongAccountId;
    individualInfo.setBakongAccountId(accountId);

    // 3. Name & Location
    individualInfo.setMerchantName(getOrDefault(bakongRequest.merchantName(), "Sea Sengly"));
    individualInfo.setMerchantCity(getOrDefault(bakongRequest.merchantCity(), "Phnom Penh"));

    // 4. Currency & Amount
    individualInfo.setCurrency(bakongRequest.currency() != null ? bakongRequest.currency() : KHQRCurrency.USD);
    individualInfo.setAmount(getOrDefault(bakongRequest.amount(), 0.01));

    // 5. Optional Fields
    individualInfo.setBillNumber(getOrDefault(bakongRequest.billNumber(), "BILL-" + System.currentTimeMillis()));
    individualInfo.setMobileNumber(bakongRequest.mobileNumber());
    individualInfo.setStoreLabel(bakongRequest.storeLabel());
    individualInfo.setTerminalLabel(bakongRequest.terminalLabel());

    // Generate individual QR payload
    KHQRResponse<KHQRData> response = BakongKHQR.generateIndividual(individualInfo);

    if (response.getKHQRStatus() != null) {
      log.info("Bakong SDK Code: {}, Message: {}",
          response.getKHQRStatus().getCode(),
          response.getKHQRStatus().getMessage());
    }

    if (response.getData() != null) {
      log.info("Generated Individual QR String: {}", response.getData().getQr());
      log.info("Generated MD5: {}", response.getData().getMd5());
    }

    return response;
  }

  @Override
  public byte[] getQRImage(KHQRData qr) {
    try {
      if (qr == null || qr.getQr() == null || qr.getQr().isBlank()) {
        return "Invalid QR data".getBytes(StandardCharsets.UTF_8);
      }

      String qrCodeText = qr.getQr();

      QRCodeWriter qrCodeWriter = new QRCodeWriter();

      Map<EncodeHintType, Object> hints = new HashMap<>();
      hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
      hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
      hints.put(EncodeHintType.MARGIN, 1);

      BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, 300, 300, hints);

      ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

      return pngOutputStream.toByteArray();

    } catch (WriterException e) {
      return "Error encoding QR data".getBytes(StandardCharsets.UTF_8);
    } catch (Exception e) {
      return ("Unexpected error: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
    }
  }

  @Override
  public BakongResponse checkTransactionByMD5(CheckTransactionRequest request) {
    String bearerToken = bakongTokenService.getToken();

    String url = baseUrl.replaceAll("/+$", "") + "/v1/check_transaction_by_md5";

    String responseBody = restClient.post()
        .uri(url)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
        .body(Map.of("md5", request.md5()))
        .retrieve()
        .body(String.class);

    log.info("Data response from Bakong API: {}", responseBody);

    try {
      return mapper.readValue(responseBody, BakongResponse.class);
    } catch (Exception e) {
      throw new RuntimeException("Invalid upstream response", e);
    }
  }

  private <T> T getOrDefault(T value, T defaultValue) {
    return value != null ? value : defaultValue;
  }
}