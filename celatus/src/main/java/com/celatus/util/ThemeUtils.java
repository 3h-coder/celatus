package com.celatus.util;

import com.celatus.App;
import com.celatus.enums.AppTheme;
import com.celatus.enums.UserSettings;

public class ThemeUtils {

    public static String getTheme() {
        var theme = App.getProperties().getProperty(UserSettings.THEME.toString());
        if (theme == null || theme.isEmpty()) {
            // fallback to default theme if not set
            theme = AppTheme.DEFAULT.toString(); 
        }

        return App.class.getResource("styles/" + theme.toLowerCase() + ".css").toExternalForm();
    }
    
}
