package com.feed_the_beast.ftblib.lib.gui;

import com.feed_the_beast.ftblib.lib.icon.Icon;

public interface GuiIcons {
    Icon BLUE_BUTTON = get("blue_button");
    Icon UP = get("up");
    Icon DOWN = get("down");
    Icon LEFT = get("left");
    Icon RIGHT = get("right");
    Icon ACCEPT = get("accept");
    Icon ADD = get("add");
    Icon REMOVE = get("remove");
    Icon INFO = get("info");
    Icon ACCEPT_GRAY = get("accept_gray");
    Icon ADD_GRAY = get("add_gray");
    Icon REMOVE_GRAY = get("remove_gray");
    Icon INFO_GRAY = get("info_gray");
    Icon SETTINGS = get("settings");
    Icon SETTINGS_RED = get("settings_red");
    Icon CANCEL = get("cancel");
    Icon BACK = get("back");
    Icon CLOSE = get("close");
    Icon REFRESH = get("refresh");
    Icon PLAYER = get("player");
    Icon PLAYER_GRAY = get("player_gray");
    Icon ONLINE = get("online");
    Icon SORT_AZ = get("sort_az");
    Icon FRIENDS = get("friends");
    Icon BUG = get("bug");
    Icon JACKET = get("jacket");
    Icon BED = get("bed");
    Icon BELL = get("bell");
    Icon COMPASS = get("compass");
    Icon MAP = get("map");
    Icon SHIELD = get("shield");
    Icon ART = get("art");
    Icon MONEY_BAG = get("money_bag");
    Icon CONTROLLER = get("controller");
    Icon FEATHER = get("feather");
    Icon CAMERA = get("camera");
    Icon INV_IO = get("inv_io");
    Icon INV_IN = get("inv_in");
    Icon INV_OUT = get("inv_out");
    Icon INV_NONE = get("inv_none");
    Icon RS_NONE = get("rs_none");
    Icon RS_HIGH = get("rs_high");
    Icon RS_LOW = get("rs_low");
    Icon RS_PULSE = get("rs_pulse");
    Icon SECURITY_PUBLIC = get("security_public");
    Icon SECURITY_PRIVATE = get("security_private");
    Icon SECURITY_TEAM = get("security_team");
    Icon COLOR_BLANK = get("color_blank");
    Icon COLOR_HSB = get("color_hsb");
    Icon COLOR_RGB = get("color_rgb");
    Icon ONLINE_RED = get("online_red");
    Icon NOTES = get("notes");
    Icon CHAT = get("chat");
    Icon BIN = get("bin");
    Icon MARKER = get("marker");
    Icon BEACON = get("beacon");
    Icon DICE = get("dice");
    Icon DIAMOND = get("diamond");
    Icon TIME = get("time");
    Icon GLOBE = get("globe");
    Icon MONEY = get("money");
    Icon CHECK = get("check");
    Icon STAR = get("star");
    Icon HEART = get("heart");
    Icon BOOK = Icon.getIcon("minecraft:items/book_normal");
    Icon BOOK_RED = Icon.getIcon("minecraft:items/book_enchanted");
    Icon BARRIER = Icon.getIcon("minecraft:items/barrier");
    Icon TOGGLE_GAMEMODE = get("toggle_gamemode");
    Icon TOGGLE_RAIN = get("toggle_rain");
    Icon TOGGLE_DAY = get("toggle_day");
    Icon TOGGLE_NIGHT = get("toggle_night");
    Icon LOCK = get("lock");
    Icon LOCK_OPEN = get("lock_open");
    Icon SUPPORT = getImage("support");

    static Icon get(String id) {
        return Icon.getIcon("ftblib:icons/" + id);
    }

    static Icon getImage(String id) {
        return Icon.getIcon("ftblib:textures/icons/" + id + ".png");
    }
}