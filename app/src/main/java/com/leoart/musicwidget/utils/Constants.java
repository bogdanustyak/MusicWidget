package com.leoart.musicwidget.utils;

public class Constants {
    public interface ACTION {
        String MAIN_ACTION = "com.leoart.musicwidget.foregroundservice.action.main";
        String UPDATE_WIDGET_ACTION = "com.leoart.musicwidget.foregroundservice.action.update_widget";
        String UPDATE_WIDGET_PROGRESS_ACTION = "com.leoart.musicwidget.foregroundservice.action.update_widget_progress";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }
}