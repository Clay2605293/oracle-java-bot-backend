package com.oraclejavabot.features.bot.util;

public enum BotMessages {

    HELLO_BOT(
            "Hello! I'm your Task Manager Bot 🤖\n\n" +
            "Use the menu below to manage your projects and tasks."
    ),

    TYPE_NEW_TASK(
            "Send me the title of the new task."
    ),

    NEW_ITEM_ADDED(
            "Task created and assigned to you successfully ✅"
    ),

    ITEM_DONE(
            "Task marked as completed ✅"
    ),

    ITEM_UNDONE(
            "Task reopened 🔄"
    ),

    ITEM_IN_PROGRESS(
            "Task is now in progress 🚧"
    ),

    ITEM_DELETED(
            "Task deleted 🗑️"
    ),

    BYE(
            "Session closed. Use /start to begin again."
    ),

    USER_NOT_REGISTERED(
            "Your Telegram account is not linked to any user.\n" +
            "Please register first in the web application."
    ),

    NO_PROJECT_FOUND(
            "No projects were found for your user."
    ),

    PROJECT_REQUIRED(
            "No active project selected.\n" +
            "Use 'Select Project' to continue."
    ),

    PROJECT_SELECTION_PROMPT(
            "Select a project by sending its number:"
    ),

    PROJECT_SELECTED(
            "Project selected successfully:"
    ),

    TASK_NOT_FOUND(
            "Task not found. Please refresh your task list."
    ),

    EMPTY_TASK_LIST(
            "No tasks assigned in this project yet."
    ),

    UNKNOWN_COMMAND(
            "I didn’t understand that command.\n" +
            "Use the menu or /start to continue."
    );

    private final String message;

    BotMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}