package com.oraclejavabot.features.bot.util;

public enum BotLabels {

    SHOW_MAIN_SCREEN("Show Main Screen"),
    HIDE_MAIN_SCREEN("Hide Main Screen"),
    LIST_ALL_ITEMS("List All Tasks"),
    ADD_NEW_ITEM("Add New Task"),
    SELECT_PROJECT("Select Project"),
    DONE("DONE"),
    UNDO("UNDO"),
    DELETE("DELETE"),
    IN_PROGRESS("IN PROGRESS"),
    MY_TASK_LIST("MY TASK LIST"),
    DASH("-");

    private final String label;

    BotLabels(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
