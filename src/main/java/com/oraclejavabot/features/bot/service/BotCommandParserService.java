package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.config.TelegramBotProperties;
import com.oraclejavabot.features.bot.util.BotCommands;
import com.oraclejavabot.features.bot.util.BotLabels;
import org.springframework.stereotype.Service;

@Service
public class BotCommandParserService {

    private final TelegramBotProperties botProperties;

    public BotCommandParserService(TelegramBotProperties botProperties) {
        this.botProperties = botProperties;
    }

    public boolean isStartCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.START_COMMAND.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.SHOW_MAIN_SCREEN.getLabel());
    }

    public boolean isHideCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.HIDE_COMMAND.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.HIDE_MAIN_SCREEN.getLabel());
    }

    public boolean isListCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.TODO_LIST.getCommand())
                || matchesCommand(requestText, BotCommands.TASK_LIST.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.LIST_ALL_ITEMS.getLabel())
                || requestText.equalsIgnoreCase(BotLabels.MY_TASK_LIST.getLabel())
                || isListIntent(requestText);
    }

    public boolean isAddCommand(String requestText) {
        return matchesCommand(requestText, BotCommands.ADD_ITEM.getCommand())
                || matchesCommand(requestText, BotCommands.ADD_TASK.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.ADD_NEW_ITEM.getLabel());
    }

    public boolean isProjectCommand(String requestText) {
        return startsWithCommand(requestText, BotCommands.PROJECT.getCommand())
                || requestText.equalsIgnoreCase(BotLabels.SELECT_PROJECT.getLabel())
                || isProjectIntent(requestText);
    }

    public boolean isCreateTaskIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.contains("crear tarea")
                || normalized.contains("crea tarea")
                || normalized.contains("nueva tarea")
                || normalized.contains("agregar tarea")
                || normalized.contains("anadir tarea")
                || normalized.contains("añadir tarea");
    }

    public boolean isListIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.contains("ver tareas")
                || normalized.contains("mis tareas")
                || normalized.contains("listar tareas")
                || normalized.contains("lista de tareas");
    }

    public boolean isProjectIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.contains("seleccionar proyecto")
                || normalized.contains("cambiar proyecto")
                || normalized.equals("proyecto")
                || normalized.equals("project");
    }

    public boolean isCancelIntent(String text) {
        String normalized = normalizeText(text);
        return normalized.equals("cancel")
                || normalized.equals("cancelar")
                || normalized.equals("salir");
    }

    public String extractCommandPayload(String requestText, String command) {
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

    public boolean isInteger(String value) {
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
}
