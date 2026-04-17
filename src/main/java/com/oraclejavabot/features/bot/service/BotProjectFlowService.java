package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.features.bot.util.BotCommands;
import com.oraclejavabot.features.bot.util.BotHelper;
import com.oraclejavabot.features.bot.util.BotMessages;
import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.projects.service.ProjectService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
public class BotProjectFlowService {

    private final ProjectService projectService;
    private final BotConversationStateService botConversationStateService;
    private final BotKeyboardService botKeyboardService;
    private final BotCommandParserService botCommandParserService;

    public BotProjectFlowService(ProjectService projectService,
                                 BotConversationStateService botConversationStateService,
                                 BotKeyboardService botKeyboardService,
                                 BotCommandParserService botCommandParserService) {
        this.projectService = projectService;
        this.botConversationStateService = botConversationStateService;
        this.botKeyboardService = botKeyboardService;
        this.botCommandParserService = botCommandParserService;
    }

    public void handleProjectCommand(Long chatId, String userId, String requestText, TelegramClient client) {
        botConversationStateService.setCreatingTaskFlow(chatId, false);

        String payload = botCommandParserService.extractCommandPayload(requestText, BotCommands.PROJECT.getCommand());

        if (payload == null || payload.isBlank()) {
            promptProjectSelection(chatId, userId, client, false);
            return;
        }

        if (!trySelectProjectFromInput(chatId, userId, payload, client)) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.PROJECT_SELECTION_PROMPT.getMessage(),
                    client,
                    botKeyboardService.buildProjectSelectionKeyboard(botConversationStateService.getProjectOptions(chatId))
            );
        }
    }

    public void promptProjectSelection(Long chatId,
                                       String userId,
                                       TelegramClient client,
                                       boolean continueToTaskTitleAfterSelection) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByUser(userId);

        if (projects.isEmpty()) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.NO_PROJECT_FOUND.getMessage(),
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return;
        }

        botConversationStateService.setProjectOptions(chatId, projects);
        botConversationStateService.setState(chatId, ConversationState.SELECTING_PROJECT);

        StringBuilder sb = new StringBuilder();
        if (continueToTaskTitleAfterSelection) {
            sb.append("Let's create a task.\n\n");
        }

        sb.append(BotMessages.PROJECT_SELECTION_PROMPT.getMessage()).append("\n\n");

        for (int i = 0; i < projects.size(); i++) {
            ProjectResponseDTO project = projects.get(i);
            sb.append(i + 1)
                    .append(". ")
                    .append(project.getNombre())
                    .append(" [")
                    .append(project.getProjectId())
                    .append("]\n");
        }

        if (continueToTaskTitleAfterSelection) {
            sb.append("\nYou can reply with the number or the project id.");
        }

        BotHelper.sendMessage(chatId, sb.toString(), client, botKeyboardService.buildProjectSelectionKeyboard(projects));
    }

    public void handleProjectSelectionStep(Long chatId,
                                           String userId,
                                           String requestText,
                                           TelegramClient client) {
        if (!trySelectProjectFromInput(chatId, userId, requestText, client)) {
            BotHelper.sendMessage(
                    chatId,
                    "I couldn't identify that project. Reply with the project number or tap one of the buttons.",
                    client,
                    botKeyboardService.buildProjectSelectionKeyboard(botConversationStateService.getProjectOptions(chatId))
            );
            return;
        }

        boolean isCreatingTask = botConversationStateService.isCreatingTaskFlow(chatId);

        if (isCreatingTask) {
            botConversationStateService.setState(chatId, ConversationState.ENTERING_TASK_TITLE);

            BotHelper.sendMessage(
                    chatId,
                    BotMessages.PROJECT_SELECTED.getMessage() + "\n\nNow send me the task title.",
                    client
            );

            BotHelper.sendMessage(chatId, BotMessages.TYPE_NEW_TASK.getMessage(), client);
        } else {
            botConversationStateService.setState(chatId, ConversationState.IDLE);

            BotHelper.sendMessage(
                    chatId,
                    "Project selected successfully. You can now manage your tasks.",
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
        }
    }

    private boolean trySelectProjectFromInput(Long chatId, String userId, String selectionInput, TelegramClient client) {
        List<ProjectResponseDTO> projects = botConversationStateService.getProjectOptions(chatId);

        if (projects == null || projects.isEmpty()) {
            projects = projectService.getProjectsByUser(userId);
            if (projects.isEmpty()) {
                BotHelper.sendMessage(
                        chatId,
                        BotMessages.NO_PROJECT_FOUND.getMessage(),
                        client,
                        botKeyboardService.buildMainMenuKeyboard()
                );
                return true;
            }
            botConversationStateService.setProjectOptions(chatId, projects);
        }

        String candidate = selectionInput.trim();
        String candidateLower = candidate.toLowerCase();

        ProjectResponseDTO selected = null;

        if (botCommandParserService.isInteger(candidate)) {
            int index = Integer.parseInt(candidate);
            if (index >= 1 && index <= projects.size()) {
                selected = projects.get(index - 1);
            }
        } else {
            for (ProjectResponseDTO project : projects) {
                boolean matchesId = project.getProjectId() != null
                        && project.getProjectId().equalsIgnoreCase(candidate);

                boolean matchesName = project.getNombre() != null
                        && project.getNombre().trim().equalsIgnoreCase(candidateLower);

                if (matchesId || matchesName) {
                    selected = project;
                    break;
                }
            }
        }

        if (selected == null) {
            return false;
        }

        botConversationStateService.setActiveProject(chatId, selected.getProjectId());
        botConversationStateService.removeListedTasks(chatId);

        BotHelper.sendMessage(
                chatId,
                BotMessages.PROJECT_SELECTED.getMessage() + " " + selected.getNombre(),
                client
        );
        return true;
    }
}
