package com.pronofoot.teamazerty.app;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.pronofoot.teamazerty.app.core.Constants;
import com.pronofoot.teamazerty.app.ui.EspacepronoFragmentActivity;
import com.pronofoot.teamazerty.app.ui.ProfilActivity;

import static com.pronofoot.teamazerty.app.CommonUtilities.displayMessage;

/**
 * Created by PBVZ9205 on 21/05/2014.
 */
public class GCMIntentService extends GCMBaseIntentService {

    public GCMIntentService() {
       super(Constants.Push.GOOGLE_PROJECT_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        //Log.i("TA", "Device registered: regId = " + registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        //Log.i(TAG, "Device unregisteredregId = " + registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        //Il y a toujours un message
        String message = intent.getExtras().getString(Constants.Push.PARAM_MESSAGE);
        //Après, soit c'est un rappel, soit c'est un message avec lien
        String typeNotif = intent.getExtras().getString(Constants.Push.PARAM_TYPE, Constants.Push.TypePush.TYPE_GRILLE);
        if (typeNotif.equalsIgnoreCase(Constants.Push.TypePush.TYPE_GRILLE)) {
            String grille = intent.getExtras().getString(Constants.Push.PARAM_GRILLE_NOM);
            int grille_id = (new Integer(intent.getExtras().getString(Constants.Push.PARAM_GRILLE_ID))).intValue();
            displayMessage(context, message);
            generateNotificationGrille(context, message, grille, grille_id);
        } else if (typeNotif.equalsIgnoreCase(Constants.Push.TypePush.TYPE_URL)) {
            String titre = intent.getExtras().getString(Constants.Push.PARAM_TITRE);
            String url = intent.getExtras().getString(Constants.Push.PARAM_URL);
            displayMessage(context, message);
            generateNotificationUrl(context, message, titre, url);
        }
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.e("TA", "Received error: " + errorId);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void generateNotificationUrl(Context context, String message, String title, String url) {
        int ico = R.drawable.ic_football;
        long when = System.currentTimeMillis();
        Resources res = context.getResources();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        //On va sur les pref
        Intent profilIntent = new Intent(context, ProfilActivity.class);
        profilIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingProfilIntent = PendingIntent.getActivity(context, 0, profilIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentText(message)
                .setSmallIcon(ico)
                .setLargeIcon(BitmapFactory.decodeResource(res, ico))
                .setWhen(when)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setDefaults(Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        try {
            builder
                .addAction(R.drawable.ic_action_settings, context.getResources().getString(R.string.page_profile), pendingProfilIntent)
                .setStyle(new Notification.BigTextStyle()
                .bigText(message));
        } catch (Exception e) {}

        if (!url.isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, browserIntent, 0);

            builder
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_action_web_site, context.getResources().getString(R.string.open), pendingIntent)
            ;
        }

        Notification notification = builder.build();

        notificationManager.notify((int)when, notification);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotificationGrille(Context context, String message, String title, int grilleId) {
        int ico = R.drawable.ic_football;
        long when = System.currentTimeMillis();
        Resources res = context.getResources();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification notification = new Notification(icon, message, when);

        //On va sur l'espace prono
        Intent grilleIntent = new Intent(context, EspacepronoFragmentActivity.class);
        grilleIntent.putExtra(Constants.Indent.GRILLE_PRONO, grilleId);
        grilleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingGrilleIntent = PendingIntent.getActivity(context, 0, grilleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //On va sur les pref
        Intent profilIntent = new Intent(context, ProfilActivity.class);
        profilIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingProfilIntent = PendingIntent.getActivity(context, 0, profilIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
            .setContentText(message)
            .setSmallIcon(ico)
            .setLargeIcon(BitmapFactory.decodeResource(res, ico))
            .setWhen(when)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setDefaults(Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
            .setContentIntent(pendingGrilleIntent)
            .addAction(R.drawable.ic_action_settings, context.getResources().getString(R.string.page_profile), pendingProfilIntent)
            .addAction(R.drawable.ic_espace_prono, context.getResources().getString(R.string.espace_pronostique), pendingGrilleIntent)
            .setStyle(new Notification.BigTextStyle()
                .bigText(message))
            ;

        Notification notification = builder.build();

        notificationManager.notify((int)when, notification);

    }

}
