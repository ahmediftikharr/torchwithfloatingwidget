package com.example.mytorch;

public final class Constants {
    public static final String BROADCAST_CLOSE_SCREEN = "app.real.flashlight.closescreen";
    public static final String BROADCAST_UPDATE_ACTION = "app.real.flashlight.updatebroadcast";
    public static String CHANNEL_ID = "FLASHLIGHT_NOTIFICATIONS";
    public static final String CURR_FLSHLGHT_MODE = "mode";
    public static final boolean DEFAULT_AUTO_ON_VAL = true;
    public static final int DEFAULT_MODE_VALUE = 1;
    public static final int DEFAULT_OFF_TOUT_MIN = 5;
    public static final int DEFAULT_PALETTE = 0;
    public static final boolean DEFAULT_PERSIST_VAL = true;
    public static final boolean DEFAULT_SOUND_ONOFF = false;
    public static final String DONT_SHOW_STAR_PREF = "dnt_show_star";
    public static final int LIST_MAX_LENGTH = 5632;
    public static final int LIST_MIDDLE = 2817;
    public static final String NOADS_PURCHASED_PREF = "noads_prchsd";
    public static final String OFF_EXTRA = "off";
    public static final int RESULT_PERMISSION_CAMERA = 0;
    public static final int SEEK_BAR_MAX = 60;
    public static final String SETTINGS_FIRST_START = "first_start";
    public static final String SETTINGS_KEY_AUTO_ON = "auto_on";
    public static final String SETTINGS_KEY_OFF_TOUT_MIN = "off_tout";
    public static final String SETTINGS_KEY_PERSISTENCE = "persistence";
    public static final String SETTINGS_KEY_SOUND = "sound_pref";
    public static final String SETTINGS_LAST_ON_TIME = "last_on";
    public static final String SETTINGS_PALETTE = "palette";
    public static final String SETTINGS_SAVED_POS = "svd_pos";
    public static final String SETTINGS_VIBRO_FBACK = "vibro_fback";
    public static final String SUPPORT_EMAIL = "help@kid-control.com";
    public static final int VIBRO_DELAY = 25;

    private Constants() {
        throw new AssertionError();
    }
}
