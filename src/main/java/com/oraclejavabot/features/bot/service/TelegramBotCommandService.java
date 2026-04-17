package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.features.bot.util.BotHelper;
import com.oraclejavabot.features.bot.util.BotMessages;
import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.users.model.UserEntity;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramBotCommandService {

    private final BotUserResolutionService botUserResolutionService;
    private final BotProjectFlowService botProjectFlowService;
    private final BotTaskFlowService botTaskFlowService;
    private final BotConversationStateService botConversationStateService;
    private final BotCommandParserService botCommandParserService;
    private final BotKeyboardService botKeyboardService;

    public TelegramBotCommandService(BotUserResolutionService botUserResolutionService,
                                     BotProjectFlowService botProjectFlowService,
                                     BotTaskFlowService botTaskFlowService,
                                     BotConversationStateService botConversationStateService,
                                     BotCommandParserService botCommandParserService,
                                     BotKeyboardService botKeyboardService) {
        this.botUserResolutionService = botUserResolutionService;
        this.botProjectFlowService = botProjectFlowService;
        this.botTaskFlowService = botTaskFlowService;
        this.botConversationStateService = botConversationStateService;
        this.botCommandParserService = botCommandParserService;
        this.botKeyboardService = botKeyboardService;
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

        Optional<UserEntity> userOpt =
                botUserResolutionService.resolveBotUser(telegramUserId, telegramUsername);

        if (userOpt.isEmpty()) {
            botConversationStateService.clearTransientChatState(chatId);
            BotHelper.sendMessage(chatId, BotMessages.USER_NOT_REGISTERED.getMessage(), client);
            return;
        }

        String userId = uuidToHex(userOpt.get().getUserId());
        ConversationState currentState = botConversationStateService.getStateOrIdle(chatId);

        // =============================
        // START COMMAND (con debounce)
        // =============================
        if (botCommandParserService.isStartCommand(requestText)) {

            // 🔥 evita doble ejecución de /start
            if (botConversationStateService.isDuplicateStart(chatId)) {
                return;
            }

            botConversationStateService.setState(chatId, ConversationState.IDLE);
            sendMainMenu(chatId, client);
            return;
        }

        // =============================
        // HIDE
        // =============================
        if (botCommandParserService.isHideCommand(requestText)) {
            botConversationStateService.clearTransientChatState(chatId);
            BotHelper.sendMessage(chatId, BotMessages.BYE.getMessage(), client);
            return;
        }

        // =============================
        // CANCEL
        // =============================
        if (botCommandParserService.isCancelIntent(requestText)) {
            botConversationStateService.clearTransientChatState(chatId);
            BotHelper.sendMessage(
                    chatId,
                    "Operation cancelled. Use the menu to continue.",
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return;
        }

        // =============================
        // STATE HANDLING
        // =============================
        if (currentState == ConversationState.SELECTING_PROJECT) {
            botProjectFlowService.handleProjectSelectionStep(chatId, userId, requestText, client);
            return;
        }

        if (currentState == ConversationState.ENTERING_TASK_TITLE) {
            botTaskFlowService.createTaskFromTitle(chatId, userId, requestText, client);
            return;
        }

        // =============================
        // COMMANDS
        // =============================
        if (botCommandParserService.isProjectCommand(requestText)) {
            botProjectFlowService.handleProjectCommand(chatId, userId, requestText, client);
            return;
        }

        if (botCommandParserService.isCreateTaskIntent(requestText)
                || botCommandParserService.isAddCommand(requestText)) {
            botTaskFlowService.startTaskCreationFlow(chatId, userId, client);
            return;
        }

        if (botCommandParserService.isListCommand(requestText)) {
            botTaskFlowService.handleListTasks(chatId, userId, client);
            return;
        }

        // =============================
        // TASK ACTIONS
        // =============================
        if (botTaskFlowService.handleTaskAction(chatId, requestText, client)) {
            return;
        }

        // =============================
        // FALLBACK
        // =============================
        BotHelper.sendMessage(
                chatId,
                BotMessages.UNKNOWN_COMMAND.getMessage(),
                client,
                botKeyboardService.buildMainMenuKeyboard()
        );
    }

    private void sendMainMenu(Long chatId, TelegramClient client) {

        botConversationStateService.setState(chatId, ConversationState.IDLE);

        ProjectResponseDTO activeProject =
                botConversationStateService.getActiveProject(chatId);

        if (activeProject == null) {
            BotHelper.sendMessage(
                    chatId,
                    BotMessages.HELLO_BOT.getMessage()
                            + "\n\nNo active project selected yet.",
                    client,
                    botKeyboardService.buildMainMenuKeyboard()
            );
            return;
        }

        BotHelper.sendMessage(
                chatId,
                BotMessages.HELLO_BOT.getMessage()
                        + "\n\nActive project: " + activeProject.getNombre(),
                client,
                botKeyboardService.buildMainMenuKeyboard()
        );
    }

    private String uuidToHex(UUID uuid) {
        return uuid.toString().replace("-", "").toUpperCase();
    }
}