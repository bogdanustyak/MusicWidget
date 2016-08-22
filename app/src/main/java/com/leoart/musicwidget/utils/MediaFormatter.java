package com.leoart.musicwidget.utils;

import android.content.Context;

import com.leoart.musicwidget.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaFormatter {

    public String formatMediaFileDuration(Context context, long duration) {
        String formattedDuration;
        if (duration > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            formattedDuration = String.format(context.getString(R.string.duration), sdf.format(new Date(duration)));
        } else {
            formattedDuration = context.getString(R.string.empty_durarion);
        }
        return formattedDuration;
    }
}
