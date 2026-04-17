package com.oraclejavabot.features.bot.service;

import com.oraclejavabot.features.projects.dto.ProjectResponseDTO;
import com.oraclejavabot.features.tasks.dto.TaskResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BotConversationStateService {

    private final Map<Long, ConversationState> stateByChat = new ConcurrentHashMap<>();
    private final Map<Long, String> activeProjectByChat = new ConcurrentHashMap<>();
    private final Map<Long, List<ProjectResponseDTO>> projectOptionsByChat = new ConcurrentHashMap<>();
    private final Map<Long, List<TaskResponseDTO>> listedTasksByChat = new ConcurrentHashMap<>();
    private final Map<Long, Boolean> creatingTaskFlowByChat = new ConcurrentHashMap<>();

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

    public String getActiveProject(Long chatId) {
        return activeProjectByChat.get(chatId);
    }

    public void setActiveProject(Long chatId, String projectId) {
        activeProjectByChat.put(chatId, projectId);
    }

    public List<ProjectResponseDTO> getProjectOptions(Long chatId) {
        return projectOptionsByChat.get(chatId);
    }

    public void setProjectOptions(Long chatId, List<ProjectResponseDTO> projects) {
        projectOptionsByChat.put(chatId, projects);
    }

    public void removeProjectOptions(Long chatId) {
        projectOptionsByChat.remove(chatId);
    }

    public List<TaskResponseDTO> getListedTasks(Long chatId) {
        return listedTasksByChat.get(chatId);
    }

    public void setListedTasks(Long chatId, List<TaskResponseDTO> tasks) {
        listedTasksByChat.put(chatId, tasks);
    }

    public void removeListedTasks(Long chatId) {
        listedTasksByChat.remove(chatId);
    }

    public boolean isCreatingTaskFlow(Long chatId) {
        return creatingTaskFlowByChat.getOrDefault(chatId, false);
    }

    public void setCreatingTaskFlow(Long chatId, boolean creatingTaskFlow) {
        creatingTaskFlowByChat.put(chatId, creatingTaskFlow);
    }

    public void removeCreatingTaskFlow(Long chatId) {
        creatingTaskFlowByChat.remove(chatId);
    }

    public void clearTransientChatState(Long chatId) {
        stateByChat.remove(chatId);
        listedTasksByChat.remove(chatId);
        projectOptionsByChat.remove(chatId);
        creatingTaskFlowByChat.remove(chatId);
    }
}
