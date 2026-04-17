package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.features.bot.util.BotLabels;
import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotKeyboardService {

    public ReplyKeyboardMarkup buildMainMenuKeyboard() {
        return ReplyKeyboardMarkup.builder()
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .selective(true)
                .keyboardRow(new KeyboardRow(BotLabels.LIST_ALL_ITEMS.getLabel(), BotLabels.ADD_NEW_ITEM.getLabel()))
                .keyboardRow(new KeyboardRow(BotLabels.SELECT_PROJECT.getLabel()))
                .keyboardRow(new KeyboardRow(BotLabels.SHOW_MAIN_SCREEN.getLabel(), BotLabels.HIDE_MAIN_SCREEN.getLabel()))
                .build();
    }

    public ReplyKeyboardMarkup buildProjectSelectionKeyboard(List<ProjectResponseDTO> projects) {
        List<KeyboardRow> rows = new ArrayList<>();

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

    public ReplyKeyboardMarkup buildTaskKeyboard(List<TaskResponseDTO> tasks) {
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
}
