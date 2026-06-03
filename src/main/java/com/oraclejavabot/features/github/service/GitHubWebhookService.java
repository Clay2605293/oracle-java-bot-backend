package com.oraclejavabot.features.github.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oraclejavabot.features.github.model.GitHubCommitEntity;
import com.oraclejavabot.features.github.model.GitHubIssueEntity;
import com.oraclejavabot.features.github.model.ProjectRepositoryEntity;
import com.oraclejavabot.features.github.repository.GitHubCommitRepository;
import com.oraclejavabot.features.github.repository.GitHubIssueRepository;
import com.oraclejavabot.features.github.repository.ProjectRepositoryJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class GitHubWebhookService {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private final ProjectRepositoryJpaRepository projectRepositoryJpaRepository;
  private final GitHubCommitRepository gitHubCommitRepository;
  private final GitHubIssueRepository gitHubIssueRepository;

  public GitHubWebhookService(
      ProjectRepositoryJpaRepository projectRepositoryJpaRepository,
      GitHubCommitRepository gitHubCommitRepository,
      GitHubIssueRepository gitHubIssueRepository) {
    this.projectRepositoryJpaRepository = projectRepositoryJpaRepository;
    this.gitHubCommitRepository = gitHubCommitRepository;
    this.gitHubIssueRepository = gitHubIssueRepository;
  }

  public void processEvent(String event, String payload) {
    try {
      System.out.println("GitHub event received in service: " + event);

      if ("push".equals(event)) {
        processPushEvent(payload);
        return;
      }

      if ("issues".equals(event)) {
        processIssuesEvent(payload);
        return;
      }

      System.out.println("GitHub event ignored in service: " + event);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("Error processing GitHub webhook event: " + event, e);
    }
  }

  private void processPushEvent(String payload) throws Exception {
    JsonNode root = objectMapper.readTree(payload);

    Optional<ProjectRepositoryEntity> repoOpt = findRepository(root);
    if (repoOpt.isEmpty()) {
      System.out.println("Repository not registered. Ignoring push event.");
      return;
    }

    ProjectRepositoryEntity repository = repoOpt.get();

    String branchName = extractBranchName(root.path("ref").asText(null));
    JsonNode commits = root.path("commits");

    if (!commits.isArray()) {
      System.out.println("Push event has no commits array.");
      return;
    }

    for (JsonNode commitNode : commits) {
      String sha = commitNode.path("id").asText(null);

      if (sha == null || sha.isBlank()) {
        continue;
      }

      if (gitHubCommitRepository.existsById(sha)) {
        System.out.println("Commit already exists. Skipping: " + sha);
        continue;
      }

      String message = truncate(commitNode.path("message").asText(null), 500);

      GitHubCommitEntity commit = new GitHubCommitEntity();
      commit.setCommitSha(sha);
      commit.setRepositoryId(repository.getRepositoryId());
      commit.setProjectId(repository.getProjectId());

      commit.setAuthorName(commitNode.path("author").path("name").asText(null));
      commit.setAuthorEmail(commitNode.path("author").path("email").asText(null));
      commit.setAuthorUsername(commitNode.path("author").path("username").asText(null));

      commit.setCommitMessage(message);
      commit.setCommitUrl(commitNode.path("url").asText(null));
      commit.setBranchName(branchName);
      commit.setCommitDate(parseGitHubDate(commitNode.path("timestamp").asText(null)));
      commit.setIsMergeCommit(isMergeCommit(message) ? 1 : 0);

      // Importante: evita insertar NULL en columna NOT NULL.
      commit.setCreatedAt(LocalDateTime.now());

      gitHubCommitRepository.save(commit);

      System.out.println("Saved GitHub commit: " + sha);
    }
  }

  private void processIssuesEvent(String payload) throws Exception {
    JsonNode root = objectMapper.readTree(payload);

    Optional<ProjectRepositoryEntity> repoOpt = findRepository(root);
    if (repoOpt.isEmpty()) {
      System.out.println("Repository not registered. Ignoring issues event.");
      return;
    }

    ProjectRepositoryEntity repository = repoOpt.get();

    JsonNode issueNode = root.path("issue");
    if (issueNode.isMissingNode() || issueNode.isNull()) {
      System.out.println("Issues event has no issue object.");
      return;
    }

    boolean isPullRequest = issueNode.has("pull_request");
    if (isPullRequest) {
      System.out.println("Issue event belongs to a pull request. Ignoring.");
      return;
    }

    Long issueId = issueNode.path("id").asLong();

    GitHubIssueEntity issue = gitHubIssueRepository.findById(issueId)
        .orElseGet(GitHubIssueEntity::new);

    issue.setIssueId(issueId);
    issue.setRepositoryId(repository.getRepositoryId());
    issue.setProjectId(repository.getProjectId());

    issue.setIssueNumber(issueNode.path("number").asLong());
    issue.setTitle(truncate(issueNode.path("title").asText(null), 500));
    issue.setBody(issueNode.path("body").asText(null));
    issue.setState(issueNode.path("state").asText(null));

    issue.setAuthorUsername(issueNode.path("user").path("login").asText(null));

    JsonNode assignee = issueNode.path("assignee");
    if (!assignee.isMissingNode() && !assignee.isNull()) {
      issue.setAssigneeUsername(assignee.path("login").asText(null));
    }

    issue.setCreatedAtGithub(parseGitHubDate(issueNode.path("created_at").asText(null)));
    issue.setUpdatedAtGithub(parseGitHubDate(issueNode.path("updated_at").asText(null)));
    issue.setClosedAtGithub(parseGitHubDate(issueNode.path("closed_at").asText(null)));
    issue.setIssueUrl(issueNode.path("html_url").asText(null));
    issue.setIsPullRequest(0);

    String action = root.path("action").asText(null);
    if ("closed".equals(action)) {
      issue.setClosedByUsername(root.path("sender").path("login").asText(null));
    }

    // Importante: evita insertar NULL en columna NOT NULL.
    issue.setSyncedAt(LocalDateTime.now());

    gitHubIssueRepository.save(issue);

    System.out.println("Saved GitHub issue: #" + issue.getIssueNumber());
  }

  private Optional<ProjectRepositoryEntity> findRepository(JsonNode root) {
    JsonNode repositoryNode = root.path("repository");

    String repoName = repositoryNode.path("name").asText(null);
    String owner = repositoryNode.path("owner").path("name").asText(null);

    if (owner == null || owner.isBlank()) {
      owner = repositoryNode.path("owner").path("login").asText(null);
    }

    System.out.println("GitHub payload repo owner: " + owner);
    System.out.println("GitHub payload repo name: " + repoName);

    if (owner == null || owner.isBlank() || repoName == null || repoName.isBlank()) {
      return Optional.empty();
    }

    Optional<ProjectRepositoryEntity> repo = projectRepositoryJpaRepository
        .findByOwnerIgnoreCaseAndRepoNameIgnoreCaseAndIsActive(owner, repoName, 1);

    System.out.println("Repository registered in DB: " + repo.isPresent());

    return repo;
  }

  private String extractBranchName(String ref) {
    if (ref == null || ref.isBlank()) {
      return null;
    }

    return ref.replace("refs/heads/", "");
  }

  private LocalDateTime parseGitHubDate(String value) {
    if (value == null || value.isBlank() || "null".equals(value)) {
      return null;
    }

    return OffsetDateTime.parse(value).toLocalDateTime();
  }

  private boolean isMergeCommit(String message) {
    return message != null && message.toLowerCase().startsWith("merge");
  }

  private String truncate(String value, int maxLength) {
    if (value == null) {
      return null;
    }

    return value.length() <= maxLength ? value : value.substring(0, maxLength);
  }
}