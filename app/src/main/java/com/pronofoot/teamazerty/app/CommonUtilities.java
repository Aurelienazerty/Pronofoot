package com.pronofoot.teamazerty.app;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    static final String DISPLAY_MESSAGE_ACTION =
            "com.pronofoot.teamazerty.app.gcm.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
