package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BotConversationStateService {

    // =============================
    // CORE STATE
    // =============================

    private final Map<Long, ConversationState> stateByChat = new ConcurrentHashMap<>();

    // 🔥 Proyecto activo como DTO (no solo ID)
    private final Map<Long, ProjectResponseDTO> activeProjectByChat = new ConcurrentHashMap<>();

    // =============================
    // TRANSIENT STATE
    // =============================

    private final Map<Long, List<ProjectResponseDTO>> projectOptionsByChat = new ConcurrentHashMap<>();
    private final Map<Long, List<TaskResponseDTO>> listedTasksByChat = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> creatingTaskFlowByChat = new ConcurrentHashMap<>();

    // =============================
    // ANTI-DUPLICATE CONTROL
    // =============================

    // 🔥 Evita doble ejecución de /start (Telegram envía duplicados a veces)
    private final Map<Long, Long> lastStartCommandTime = new ConcurrentHashMap<>();

    private static final long START_DEBOUNCE_MS = 2000;

    public boolean isDuplicateStart(Long chatId) {
        long now = System.currentTimeMillis();
        Long lastTime = lastStartCommandTime.get(chatId);

        if (lastTime != null && (now - lastTime) < START_DEBOUNCE_MS) {
            return true;
        }

        lastStartCommandTime.put(chatId, now);
        return false;
    }

    // =============================
    // STATE
    // =============================

    public ConversationState getStateOrIdle(Long chatId) {
        return stateByChat.getOrDefault(chatId, ConversationState.IDLE);
    }

    public void setState(Long chatId, ConversationState state) {
        if (state == null) {
            stateByChat.remove(chatId);
            return;
        }
        stateByChat.put(chatId, state);
    }

    // =============================
    // ACTIVE PROJECT
    // =============================

    public ProjectResponseDTO getActiveProject(Long chatId) {
        return activeProjectByChat.get(chatId);
    }

    public void setActiveProject(Long chatId, ProjectResponseDTO project) {
        if (project == null) {
            activeProjectByChat.remove(chatId);
            return;
        }
        activeProjectByChat.put(chatId, project);
    }

    public void removeActiveProject(Long chatId) {
        activeProjectByChat.remove(chatId);
    }

    // =============================
    // PROJECT OPTIONS
    // =============================

    public List<ProjectResponseDTO> getProjectOptions(Long chatId) {
        return projectOptionsByChat.get(chatId);
    }

    public void setProjectOptions(Long chatId, List<ProjectResponseDTO> projects) {
        projectOptionsByChat.put(chatId, projects);
    }

    public void removeProjectOptions(Long chatId) {
        projectOptionsByChat.remove(chatId);
    }

    // =============================
    // TASK LIST CACHE
    // =============================

    public List<TaskResponseDTO> getListedTasks(Long chatId) {
        return listedTasksByChat.get(chatId);
    }

    public void setListedTasks(Long chatId, List<TaskResponseDTO> tasks) {
        listedTasksByChat.put(chatId, tasks);
    }

    public void removeListedTasks(Long chatId) {
        listedTasksByChat.remove(chatId);
    }

    // =============================
    // TASK CREATION FLOW
    // =============================

    public boolean isCreatingTaskFlow(Long chatId) {
        return creatingTaskFlowByChat.getOrDefault(chatId, false);
    }

    public void setCreatingTaskFlow(Long chatId, boolean creatingTaskFlow) {
        creatingTaskFlowByChat.put(chatId, creatingTaskFlow);
    }

    public void removeCreatingTaskFlow(Long chatId) {
        creatingTaskFlowByChat.remove(chatId);
    }

    // =============================
    // CLEANUP
    // =============================

    public void clearTransientChatState(Long chatId) {
        stateByChat.remove(chatId);
        listedTasksByChat.remove(chatId);
        projectOptionsByChat.remove(chatId);
        creatingTaskFlowByChat.remove(chatId);

        // 🔥 IMPORTANTE:
        // NO borramos el proyecto activo
        // porque es parte del contexto persistente del usuario
    }
}