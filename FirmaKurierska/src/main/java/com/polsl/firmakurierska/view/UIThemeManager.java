package com.polsl.firmakurierska.view;

public class UIThemeManager {

    private static UIThemeManager themeManager = null;
    private boolean themeMode;

    private UIThemeManager() {
        themeMode = false;
    }

    public static synchronized UIThemeManager getUIThemeManager() {

        if (themeManager == null) {
            themeManager = new UIThemeManager();
        }

        return themeManager;
    }

    public void setThemeMode(boolean isDarkMode) {
        themeMode = isDarkMode;
    }

    public boolean getThemeMode() {
        return themeMode;
    }

}
