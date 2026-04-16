package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.config.TelegramBotProperties;
import com.oraclejavabot.features.bot.util.BotCommands;
import com.oraclejavabot.features.bot.util.BotHelper;
import com.oraclejavabot.features.bot.util.BotLabels;
import com.oraclejavabot.features.bot.util.BotMessages;
import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.projects.service.ProjectService;
import com.oraclejavabot.features.tasks.dto.TaskRequestDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import com.oraclejavabot.features.tasks.service.TaskService;
import com.oraclejavabot.features.tasks.service.TaskUserService;
import com.oraclejavabot.features.users.model.UserEntity;
import com.oraclejavabot.features.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramBotCommandService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotCommandService.class);

    private final TaskService taskService;
    private final ProjectService projectService;
    private final TaskUserService taskUserService;
    private final UserService userService;
    private final TelegramBotProperties botProperties;

    /**
     * Estado persistente simple por chat.
     */
    private final Map<Long, ConversationState> stateByChat = new ConcurrentHashMap<>();

    /**
     * Proyecto activo por chat.
     */
    private final Map<Long, String> activeProjectByChat = new ConcurrentHashMap<>();

    /**
     * Opciones de proyecto cargadas para selección.
     */
    private final Map<Long, List<ProjectResponseDTO>> projectOptionsByChat = new ConcurrentHashMap<>();

    /**
     * Última lista de tareas mostrada en el chat.
     * Se usa para resolver acciones tipo "1-DONE".
     */
    private final Map<Long, List<TaskResponseDTO>> listedTasksByChat = new ConcurrentHashMap<>();

    /**
     * Indica si la selección de proyecto actual viene del flujo de crear tarea
     * o solo de cambiar/seleccionar proyecto.
     */
    private final Map<Long, Boolean> creatingTaskFlowByChat = new ConcurrentHashMap<>();

    public TelegramBotCommandService(TaskService taskService,
                                     ProjectService projectService,
                                     TaskUserService taskUserService,
                                     UserService userService,
                                     TelegramBotProperties botProperties) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.taskUserService = taskUserService;
        this.userService = userService;
        this.botProperties = botProperties;
    }

    public void handleTextMessage(Long chatId,
                                  Long telegramUserId,
                                  String telegramUsername,
                                  String rawText,
                                  TelegramClient client) {
        if (chatId == null || rawText == null) {
            return;
        }

        String requestText = rawText.trim();
        if (requestText.isEmpty()) {
            return;
        }

        Optional<UserEntity> userOpt = resolveBotUser(telegramUserId, telegramUsername);
        if (userOpt.isEmpty()) {
            clearTransientChatState(chatId);
            BotHelper.sendMessage(chatId, BotMessages.USER_NOT_REGISTERED.getMessage(), client);
            return;
        }

        String userId = uuidToHex(userOpt.get().getUserId());
        ConversationState currentState = stateByChat.getOrDefault(chatId, ConversationState.IDLE);

        if (isStartCommand(requestText)) {
            stateByChat.put(chatId, ConversationState.IDLE);
            sendMainMenu(chatId, client);
            return;
        }

        if (isHideCommand(requestText)) {
            clearTransientChatState(chatId);
            BotHelper.sendMessage(chatId, BotMessages.BYE.getMessage(), client);
            return;
        }

        if (isCancelIntent(requestText)) {
            clearTransientChatState(chatId);
            BotHelper.sendMessage(chatId, "Operación cancelada. Usa el menú para continuar.", client, buildMainMenuKeyboard());
            return;
        }

        /**
         * Flujo conversacional en progreso.
         */
        if (currentState == ConversationState.SELECTING_PROJECT) {
            handleProjectSelectionStep(chatId, userId, requestText, client);
            return;
        }

        if (currentState == ConversationState.ENTERING_TASK_TITLE) {
            createTaskFromTitle(chatId, userId, requestText, client);
            return;
        }

        /**
         * Acciones explícitas.
         */
        if (isProjectCommand(requestText)) {
            handleProjectCommand(chatId, userId, requestText, client);
            return;
        }

        if (isCreateTaskIntent(requestText) || isAddCommand(requestText)) {
            startTaskCreationFlow(chatId, userId, client);
            return;
        }

        if (isListCommand(requestText)) {
            handleListTasks(chatId, userId, client);
            return;
        }

        if (handleTaskAction(chatId, requestText, client)) {
            return;
        }

        BotHelper.sendMessage(chatId, BotMessages.UNKNOWN_COMMAND.getMessage(), client, buildMainMenuKeyboard());
    }

    private Optional<UserEntity> resolveBotUser(Long telegramUserId, String telegramUsername) {
        Set<String> candidates = new LinkedHashSet<>();

        if (telegramUserId != null) {
            candidates.add(String.valueOf(telegramUserId));
        }

        if (telegramUsername != null && !telegramUsername.isBlank()) {
            String cleanUsername = telegramUsername.trim();

            candidates.add(cleanUsername);

            if (cleanUsername.startsWith("@")) {
                String withoutAt = cleanUsername.substring(1).trim();
                if (!withoutAt.isEmpty()) {
                    candidates.add(withoutAt);
                }
            } else {
                candidates.add("@" + cleanUsername);
            }
        }

        for (String candidate : candidates) {
            Optional<UserEntity> user = userService.findByTelegramIdFlexible(candidate);
            if (user.isPresent()) {
                return user;
            }
        }

        return Optional.empty();
    }

    private void sendMainMenu(Long chatId, TelegramClient client) {
        stateByChat.put(chatId, ConversationState.IDLE);

        String activeProject = activeProjectByChat.get(chatId);

        if (activeProject == null || activeProject.isBlank()) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.HELLO_BOT.getMessage() + "\n\nNo active project selected yet.",
                    client,
                    buildMainMenuKeyboard()
            );
            return;
        }

        BotHelper.sendMessage(
                chatId,
                BotMessages.HELLO_BOT.getMessage() + "\n\nActive project: " + activeProject,
                client,
                buildMainMenuKeyboard()
        );
    }

    private ReplyKeyboardMarkup buildMainMenuKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .keyboardRow(new KeyboardRow(BotLabels.LIST_ALL_ITEMS.getLabel(), BotLabels.ADD_NEW_ITEM.getLabel()))
                .keyboardRow(new KeyboardRow(BotLabels.SELECT_PROJECT.getLabel()))
                .keyboardRow(new KeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel(), BotLabels.HIDE_MAIN_SCREEN.getLabel()))
                .build();
    }

    private void handleProjectCommand(Long chatId, String userId, String requestText, TelegramClient client) {
        creatingTaskFlowByChat.put(chatId, false);

        String payload = extractCommandPayload(requestText, BotCommands.PROJECT.getCommand());

        if (payload == null || payload.isBlank()) {
            promptProjectSelection(chatId, userId, client, false);
            return;
        }

        if (!trySelectProjectFromInput(chatId, userId, payload, client)) {
            BotHelper.sendMessage(chatId, BotMessages.PROJECT_SELECTION_PROMPT.getMessage(), client, buildProjectSelectionKeyboard(chatId));
        }
    }

    private void startTaskCreationFlow(Long chatId, String userId, TelegramClient client) {
        creatingTaskFlowByChat.put(chatId, true);
        promptProjectSelection(chatId, userId, client, true);
    }

    private void promptProjectSelection(Long chatId,
                                        String userId,
                                        TelegramClient client,
                                        boolean continueToTaskTitleAfterSelection) {
        List<ProjectResponseDTO> projects = projectService.getProjectsByUser(userId);

        if (projects.isEmpty()) {
            BotHelper.sendMessage(chatId, BotMessages.NO_PROJECT_FOUND.getMessage(), client, buildMainMenuKeyboard());
            return;
        }

        projectOptionsByChat.put(chatId, projects);
        stateByChat.put(chatId, ConversationState.SELECTING_PROJECT);

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

        BotHelper.sendMessage(chatId, sb.toString(), client, buildProjectSelectionKeyboard(chatId));
    }

    private ReplyKeyboardMarkup buildProjectSelectionKeyboard(Long chatId) {
        List<KeyboardRow> rows = new ArrayList<>();
        List<ProjectResponseDTO> projects = projectOptionsByChat.get(chatId);

        if (projects != null) {
            for (int i = 0; i < projects.size(); i++) {
                KeyboardRow row = new KeyboardRow();
                row.add(String.valueOf(i + 1));
                row.add(projects.get(i).getNombre());
                rows.add(row);
            }
        }

        rows.add(new KeyboardRow("Cancel"));

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();
    }

    private void handleProjectSelectionStep(Long chatId,
                                            String userId,
                                            String requestText,
                                            TelegramClient client) {
        if (!trySelectProjectFromInput(chatId, userId, requestText, client)) {
            BotHelper.sendMessage(
                    chatId,
                    "I couldn't identify that project. Reply with the project number or tap one of the buttons.",
                    client,
                    buildProjectSelectionKeyboard(chatId)
            );
            return;
        }

        boolean isCreatingTask = creatingTaskFlowByChat.getOrDefault(chatId, false);

        if (isCreatingTask) {
            stateByChat.put(chatId, ConversationState.ENTERING_TASK_TITLE);

            BotHelper.sendMessage(
                    chatId,
                    BotMessages.PROJECT_SELECTED.getMessage() + "\n\nNow send me the task title.",
                    client
            );

            BotHelper.sendMessage(chatId, BotMessages.TYPE_NEW_TASK.getMessage(), client);
        } else {
            stateByChat.put(chatId, ConversationState.IDLE);

            BotHelper.sendMessage(
                    chatId,
                    "Project selected successfully. You can now manage your tasks.",
                    client,
                    buildMainMenuKeyboard()
            );
        }
    }

    private boolean trySelectProjectFromInput(Long chatId, String userId, String selectionInput, TelegramClient client) {
        List<ProjectResponseDTO> projects = projectOptionsByChat.get(chatId);

        if (projects == null || projects.isEmpty()) {
            projects = projectService.getProjectsByUser(userId);
            if (projects.isEmpty()) {
                BotHelper.sendMessage(chatId, BotMessages.NO_PROJECT_FOUND.getMessage(), client, buildMainMenuKeyboard());
                return true;
            }
            projectOptionsByChat.put(chatId, projects);
        }

        String candidate = selectionInput.trim();
        String candidateLower = candidate.toLowerCase();

        ProjectResponseDTO selected = null;

        if (isInteger(candidate)) {
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

        activeProjectByChat.put(chatId, selected.getProjectId());
        listedTasksByChat.remove(chatId);

        BotHelper.sendMessage(
                chatId,
                BotMessages.PROJECT_SELECTED.getMessage() + " " + selected.getNombre(),
                client
        );
        return true;
    }

    private void createTaskFromTitle(Long chatId, String userId, String taskTitle, TelegramClient client) {
        String normalizedTitle = taskTitle == null ? "" : taskTitle.trim();

        if (normalizedTitle.isBlank()) {
            BotHelper.sendMessage(chatId, "The task title cannot be empty. Please send a valid title.", client);
            return;
        }

        String projectId = activeProjectByChat.get(chatId);
        if (projectId == null || projectId.isBlank()) {
            clearTransientChatState(chatId);
            BotHelper.sendMessage(chatId, BotMessages.PROJECT_REQUIRED.getMessage(), client, buildMainMenuKeyboard());
            return;
        }

        try {
            TaskRequestDTO request = new TaskRequestDTO();
            request.setTitulo(normalizedTitle);
            request.setFechaLimite(LocalDateTime.now()
                    .plusDays(botProperties.getDefaultDueDays())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            request.setPrioridadId(botProperties.getDefaultPriorityId());

            TaskResponseDTO createdTask = taskService.createTask(projectId, request);
            taskUserService.assignUser(createdTask.getTaskId(), userId);

            stateByChat.put(chatId, ConversationState.IDLE);
            listedTasksByChat.remove(chatId);
            projectOptionsByChat.remove(chatId);
            creatingTaskFlowByChat.remove(chatId);

            BotHelper.sendMessage(
                    chatId,
                    "Task created successfully ✅\n\nTitle: " + normalizedTitle + "\nProject: " + projectId,
                    client,
                    buildMainMenuKeyboard()
            );

        } catch (Exception e) {
            logger.error("Error creating task from bot", e);
            BotHelper.sendMessage(chatId, "Task could not be created. Try again.", client, buildMainMenuKeyboard());
            stateByChat.put(chatId, ConversationState.IDLE);
            creatingTaskFlowByChat.remove(chatId);
        }
    }

    private void handleListTasks(Long chatId, String userId, TelegramClient client) {
        String activeProjectId = activeProjectByChat.get(chatId);
        if (activeProjectId == null || activeProjectId.isBlank()) {
            BotHelper.sendMessage(chatId, BotMessages.PROJECT_REQUIRED.getMessage(), client, buildMainMenuKeyboard());
            return;
        }

        List<TaskResponseDTO> tasks = taskService.getAssignedTasksByUserAndProject(userId, activeProjectId);
        listedTasksByChat.put(chatId, tasks);
        stateByChat.put(chatId, ConversationState.IDLE);

        ReplyKeyboardMarkup keyboard = buildTaskKeyboard(tasks);

        String header = BotLabels.MY_TASK_LIST.getLabel() + "\nProject: " + activeProjectId;
        if (tasks.isEmpty()) {
            header = header + "\n" + BotMessages.EMPTY_TASK_LIST.getMessage();
        }

        BotHelper.sendMessage(chatId, header, client, keyboard);
    }

    private boolean handleTaskAction(Long chatId, String requestText, TelegramClient client) {
        int dashIndex = requestText.indexOf(BotLabels.DASH.getLabel());
        if (dashIndex <= 0) {
            return false;
        }

        String possibleIndex = requestText.substring(0, dashIndex).trim();
        if (!isInteger(possibleIndex)) {
            return false;
        }

        List<TaskResponseDTO> cachedTasks = listedTasksByChat.get(chatId);
        if (cachedTasks == null || cachedTasks.isEmpty()) {
            BotHelper.sendMessage(chatId, BotMessages.TASK_NOT_FOUND.getMessage(), client, buildMainMenuKeyboard());
            return true;
        }

        int taskNumber = Integer.parseInt(possibleIndex);
        if (taskNumber < 1 || taskNumber > cachedTasks.size()) {
            BotHelper.sendMessage(chatId, BotMessages.TASK_NOT_FOUND.getMessage(), client, buildMainMenuKeyboard());
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

            listedTasksByChat.remove(chatId);
            return true;

        } catch (Exception e) {
            logger.error("Error handling task action from bot", e);
            BotHelper.sendMessage(chatId, "Task action failed. Use the task list to refresh.", client, buildMainMenuKeyboard());
            return true;
        }
    }

    private ReplyKeyboardMarkup buildTaskKeyboard(List<TaskResponseDTO> tasks) {
        ReplyKeyboardMarkup keyboardMarkup = ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .build();

        List<KeyboardRow> keyboard = new ArrayList<>();

        keyboard.add(new KeyboardRow(BotLabels.SELECT_PROJECT.getLabel()));
        keyboard.add(new KeyboardRow(BotLabels.ADD_NEW_ITEM.getLabel()));
        keyboard.add(new KeyboardRow(BotLabels.MY_TASK_LIST.getLabel()));

        for (int i = 0; i < tasks.size(); i++) {
            TaskResponseDTO task = tasks.get(i);
            int idx = i + 1;

            KeyboardRow row = new KeyboardRow();
            row.add(task.getTitulo());

            if (task.getEstadoId() != null && task.getEstadoId() == 1) {
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.IN_PROGRESS.getLabel());
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
            } else if (task.getEstadoId() != null && task.getEstadoId() == 2) {
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
            } else if (task.getEstadoId() != null && task.getEstadoId() == 3) {
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
            } else {
                row.add(idx + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
            }

            keyboard.add(row);
        }

        keyboard.add(new KeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel(), BotLabels.HIDE_MAIN_SCREEN.getLabel()));

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    private boolean isStartCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.START_COMMAND.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.SHOW_MAIN_SCREEN.getLabel());
    }

    private boolean isHideCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.HIDE_COMMAND.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.HIDE_MAIN_SCREEN.getLabel());
    }

    private boolean isListCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.TODO_LIST.getCommand())
                || matchesCommand(requestText, BotCommands.TASK_LIST.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.LIST_ALL_ITEMS.getLabel())
                || requestText.equalsIgnoreCase(BotLabels.MY_TASK_LIST.getLabel())
                || isListIntent(requestText);
    }

    private boolean isAddCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.ADD_ITEM.getCommand())
                || matchesCommand(requestText, BotCommands.ADD_TASK.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.ADD_NEW_ITEM.getLabel());
    }

    private boolean isProjectCommand(String requestText) {
        return startsWithCommand(requestText, BotCommands.PROJECT.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.SELECT_PROJECT.getLabel())
                || isProjectIntent(requestText);
    }

    private boolean isCreateTaskIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.contains("crear tarea")
                || normalized.contains("crea tarea")
                || normalized.contains("nueva tarea")
                || normalized.contains("agregar tarea")
                || normalized.contains("anadir tarea")
                || normalized.contains("añadir tarea");
    }

    private boolean isListIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.contains("ver tareas")
                || normalized.contains("mis tareas")
                || normalized.contains("listar tareas")
                || normalized.contains("lista de tareas");
    }

    private boolean isProjectIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.contains("seleccionar proyecto")
                || normalized.contains("cambiar proyecto")
                || normalized.equals("proyecto")
                || normalized.equals("project");
    }

    private boolean isCancelIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.equals("cancel")
                || normalized.equals("cancelar")
                || normalized.equals("salir");
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim().toLowerCase();
    }

    private boolean matchesCommand(String requestText, String command) {
        String normalized = requestText.trim().toLowerCase();
        String cmd = command.toLowerCase();

        return normalized.equals(cmd)
                || normalized.equals(cmd + "@" + botProperties.getName().toLowerCase());
    }

    private boolean startsWithCommand(String requestText, String command) {
        String normalized = requestText.trim().toLowerCase();
        String cmd = command.toLowerCase();

        return normalized.equals(cmd)
                || normalized.startsWith(cmd + " ")
                || normalized.startsWith(cmd + "@");
    }

    private String extractCommandPayload(String requestText, String command) {
        if (!startsWithCommand(requestText, command)) {
            return null;
        }

        String normalized = requestText.trim();
        String lower = normalized.toLowerCase();
        String cmd = command.toLowerCase();

        if (lower.equals(cmd)) {
            return "";
        }

        if (lower.startsWith(cmd + "@")) {
            int spaceIndex = normalized.indexOf(' ');
            if (spaceIndex < 0) {
                return "";
            }
            return normalized.substring(spaceIndex + 1).trim();
        }

        return normalized.substring(command.length()).trim();
    }

    private boolean isInteger(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void clearTransientChatState(Long chatId) {
        stateByChat.remove(chatId);
        listedTasksByChat.remove(chatId);
        projectOptionsByChat.remove(chatId);
        creatingTaskFlowByChat.remove(chatId);
    }

    private String uuidToHex(UUID uuid) {
        return uuid.toString().replace("-", "").toUpperCase();
    }
}