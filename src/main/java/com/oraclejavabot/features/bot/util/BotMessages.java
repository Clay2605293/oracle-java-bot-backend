package com.oraclejavabot.features.bot.util;

public enum BotMessages {

    HELLO_BOT("Hello! I'm the Task Manager Bot!\nSelect an option below to continue."),
    TYPE_NEW_TASK("Type a new task title and send it."),
    NEW_ITEM_ADDED("New task added and assigned to you! Use /tasklist to refresh."),
    ITEM_DONE("Task completed! Use /tasklist to refresh."),
    ITEM_UNDONE("Task reopened! Use /tasklist to refresh."),
    ITEM_IN_PROGRESS("Task set to in progress! Use /tasklist to refresh."),
    ITEM_DELETED("Task deleted! Use /tasklist to refresh."),
    BYE("Bye! Use /start to resume."),
    USER_NOT_REGISTERED("Your Telegram user is not registered in the platform. Please register first in the web app."),
    NO_PROJECT_FOUND("No projects available for your user."),
    PROJECT_REQUIRED("No active project selected. Use /project first."),
    PROJECT_SELECTION_PROMPT("Select your active project by sending the project number."),
    PROJECT_SELECTED("Project selected successfully."),
    TASK_NOT_FOUND("Task not found. Use /tasklist to refresh first."),
    EMPTY_TASK_LIST("No assigned tasks found in the active project."),
    UNKNOWN_COMMAND("Command not recognized. Use /start to see available options.");

    private final String message;

    BotMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
