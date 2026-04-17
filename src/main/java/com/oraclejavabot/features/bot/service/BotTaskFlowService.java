package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.config.TelegramBotProperties;
import com.oraclejavabot.features.bot.util.BotHelper;
import com.oraclejavabot.features.bot.util.BotLabels;
import com.oraclejavabot.features.bot.util.BotMessages;
import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;
import com.oraclejavabot.features.tasks.service.TaskUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class BotTaskFlowService {

    private static final Logger logger = LoggerFactory.getLogger(BotTaskFlowService.class);

    private final TaskService taskService;
    private final TaskUserService taskUserService;
    private final BotProjectFlowService botProjectFlowService;
    private final BotConversationStateService botConversationStateService;
    private final BotKeyboardService botKeyboardService;
    private final BotCommandParserService botCommandParserService;
    private final TelegramBotProperties botProperties;

    public BotTaskFlowService(TaskService taskService,
                              TaskUserService taskUserService,
                              BotProjectFlowService botProjectFlowService,
                              BotConversationStateService botConversationStateService,
                              BotKeyboardService botKeyboardService,
                              BotCommandParserService botCommandParserService,
                              TelegramBotProperties botProperties) {
        this.taskService = taskService;
        this.taskUserService = taskUserService;
        this.botProjectFlowService = botProjectFlowService;
        this.botConversationStateService = botConversationStateService;
        this.botKeyboardService = botKeyboardService;
        this.botCommandParserService = botCommandParserService;
        this.botProperties = botProperties;
    }

    public void startTaskCreationFlow(Long chatId, String userId, TelegramClient client) {
        botConversationStateService.setCreatingTaskFlow(chatId, true);
        botProjectFlowService.promptProjectSelection(chatId, userId, client, true);
    }

    public void createTaskFromTitle(Long chatId, String userId, String taskTitle, TelegramClient client) {

        String normalizedTitle = taskTitle == null ? "" : taskTitle.trim();

        if (normalizedTitle.isBlank()) {
            BotHelper.sendMessage(chatId, "The task title cannot be empty. Please send a valid title.", client);
            return;
        }

        ProjectResponseDTO project = botConversationStateService.getActiveProject(chatId);

        if (project == null) {
            botConversationStateService.clearTransientChatState(chatId);
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.PROJECT_REQUIRED.getMessage(),
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return;
        }

        try {
            TaskRequestDTO request = new TaskRequestDTO();
            request.setTitulo(normalizedTitle);
            request.setFechaLimite(LocalDateTime.now()
                    .plusDays(botProperties.getDefaultDueDays())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            request.setPrioridadId(botProperties.getDefaultPriorityId());

            // 🔥 Usamos ID solo internamente
            TaskResponseDTO createdTask = taskService.createTask(project.getProjectId(), request);
            taskUserService.assignUser(createdTask.getTaskId(), userId);

            botConversationStateService.setState(chatId, ConversationState.IDLE);
            botConversationStateService.removeListedTasks(chatId);
            botConversationStateService.removeProjectOptions(chatId);
            botConversationStateService.removeCreatingTaskFlow(chatId);

            BotHelper.sendMessage(
                    chatId,
                    "Task created successfully ✅\n\n"
                            + "Title: " + normalizedTitle
                            + "\nProject: " + project.getNombre(),
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );

        } catch (Exception e) {
            logger.error("Error creating task from bot", e);

            BotHelper.sendMessage(
                    chatId,
                    "Task could not be created. Try again.",
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );

            botConversationStateService.setState(chatId, ConversationState.IDLE);
            botConversationStateService.removeCreatingTaskFlow(chatId);
        }
    }

    public void handleListTasks(Long chatId, String userId, TelegramClient client) {

        ProjectResponseDTO project = botConversationStateService.getActiveProject(chatId);

        if (project == null) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.PROJECT_REQUIRED.getMessage(),
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return;
        }

        List<TaskResponseDTO> tasks =
                taskService.getAssignedTasksByUserAndProject(userId, project.getProjectId());

        botConversationStateService.setListedTasks(chatId, tasks);
        botConversationStateService.setState(chatId, ConversationState.IDLE);

        ReplyKeyboardMarkup keyboard = botKeyboardService.buildTaskKeyboard(tasks);

        String header = BotLabels.MY_TASK_LIST.getLabel()
                + "\nProject: " + project.getNombre();

        if (tasks.isEmpty()) {
            header = header + "\n" + BotMessages.EMPTY_TASK_LIST.getMessage();
        }

        BotHelper.sendMessage(chatId, header, client, keyboard);
    }

    public boolean handleTaskAction(Long chatId, String requestText, TelegramClient client) {

        int dashIndex = requestText.indexOf(BotLabels.DASH.getLabel());
        if (dashIndex <= 0) {
            return false;
        }

        String possibleIndex = requestText.substring(0, dashIndex).trim();

        if (!botCommandParserService.isInteger(possibleIndex)) {
            return false;
        }

        List<TaskResponseDTO> cachedTasks = botConversationStateService.getListedTasks(chatId);

        if (cachedTasks == null || cachedTasks.isEmpty()) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.TASK_NOT_FOUND.getMessage(),
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return true;
        }

        int taskNumber = Integer.parseInt(possibleIndex);

        if (taskNumber < 1 || taskNumber > cachedTasks.size()) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.TASK_NOT_FOUND.getMessage(),
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return true;
        }

        TaskResponseDTO task = cachedTasks.get(taskNumber - 1);
        String action = requestText.substring(dashIndex + 1).trim();

        try {

            if (action.equalsIgnoreCase(BotLabels.DONE.getLabel())) {
                taskService.changeStatus(task.getTaskId(), 3);
                BotHelper.sendMessage(chatId, BotMessages.ITEM_DONE.getMessage(), client);

            } else if (action.equalsIgnoreCase(BotLabels.UNDO.getLabel())) {
                taskService.changeStatus(task.getTaskId(), botProperties.getDefaultStatusId());
                BotHelper.sendMessage(chatId, BotMessages.ITEM_UNDONE.getMessage(), client);

            } else if (action.equalsIgnoreCase(BotLabels.IN_PROGRESS.getLabel())) {
                taskService.changeStatus(task.getTaskId(), 2);
                BotHelper.sendMessage(chatId, BotMessages.ITEM_IN_PROGRESS.getMessage(), client);

            } else if (action.equalsIgnoreCase(BotLabels.DELETE.getLabel())) {
                taskService.deleteTask(task.getTaskId());
                BotHelper.sendMessage(chatId, BotMessages.ITEM_DELETED.getMessage(), client);

            } else {
                return false;
            }

            botConversationStateService.removeListedTasks(chatId);
            return true;

        } catch (Exception e) {
            logger.error("Error handling task action from bot", e);

            BotHelper.sendMessage(
                    chatId,
                    "Task action failed. Use the task list to refresh.",
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );

            return true;
        }
    }
}