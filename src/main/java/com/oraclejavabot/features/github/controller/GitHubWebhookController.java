package com.oraclejavabot.features.github.controller;

import com.oraclejavabot.features.github.service.GitHubSignatureService;
import com.oraclejavabot.features.github.service.GitHubWebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
public class GitHubWebhookController {

  private final GitHubSignatureService signatureService;
  private final GitHubWebhookService webhookService;

  public GitHubWebhookController(
      GitHubSignatureService signatureService,
      GitHubWebhookService webhookService) {
    this.signatureService = signatureService;
    this.webhookService = webhookService;
  }

  @PostMapping("/webhook")
  public ResponseEntity<String> handleWebhook(
      @RequestHeader(value = "X-GitHub-Event", required = false) String event,
      @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
      @RequestBody(required = false) String payload) {
    String safePayload = payload == null ? "" : payload;

    if (!signatureService.isValidSignature(safePayload, signature)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid GitHub webhook signature");
    }

    if ("ping".equals(event)) {
      return ResponseEntity.ok("GitHub webhook ping received");
    }

    if ("push".equals(event) || "issues".equals(event)) {
      webhookService.processEvent(event, safePayload);
      return ResponseEntity.ok("GitHub " + event + " event processed");
    }

    return ResponseEntity.ok("GitHub event ignored: " + event);
  }
}