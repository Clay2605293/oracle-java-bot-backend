package com.oraclejavabot.features.github.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Service
public class GitHubSignatureService {

  @Value("${github.webhook.secret}")
  private String webhookSecret;

  public boolean isValidSignature(String payload, String signatureHeader) {
    if (webhookSecret == null || webhookSecret.isBlank()) {
      return false;
    }

    if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
      return false;
    }

    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKey = new SecretKeySpec(
          webhookSecret.getBytes(StandardCharsets.UTF_8),
          "HmacSHA256");

      mac.init(secretKey);

      byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
      String expectedSignature = "sha256=" + HexFormat.of().formatHex(rawHmac);

      return MessageDigest.isEqual(
          expectedSignature.getBytes(StandardCharsets.UTF_8),
          signatureHeader.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      return false;
    }
  }
}