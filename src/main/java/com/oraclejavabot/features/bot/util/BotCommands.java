package com.oraclejavabot.features.bot.util;

public enum BotCommands {

    START_COMMAND("/start"),
    HIDE_COMMAND("/hide"),
    TODO_LIST("/todolist"),
    TASK_LIST("/tasklist"),
    ADD_ITEM("/additem"),
    ADD_TASK("/addtask"),
    PROJECT("/project");

    private final String command;

    BotCommands(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
