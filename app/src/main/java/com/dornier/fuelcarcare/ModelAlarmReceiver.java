package com.dornier.fuelcarcare;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Deneb on 11/09/2017.
 */

public class ModelAlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        String msg = bundle.getString("notification");
        String detail = bundle.getString("detail");
        Date now = new Date();
        ModelDataManager.getInstance().setActualContext(context);
        ModelDataManager.getInstance().loadFromDB();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for(ModelVehicle vehicle:ModelDataManager.getInstance().getVehicles()){
            for(ModelMaintenanceAlert alert:vehicle.getAllAlerts()){
                if ((fmt.format(now).equals(fmt.format(alert.getMaintenance_date()))) && !(alert.getStatus() == -1)){
                    alert.setStatus(-1);
                    ModelDataManager.getInstance().addOrUpdateMaintenance(vehicle,alert);

                    Notification noti = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        noti = new Notification.Builder(context)
                                .setContentTitle(msg)
                                .setContentText(detail)
                                .setSmallIcon(R.drawable.icon_nobg)
                                .build();
                    }
                    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            noti = new Notification.Builder(context)
                                    .setContentTitle(msg)
                                    .setContentText(detail)
                                    .setSmallIcon(R.drawable.icon_nobg)
                                    .getNotification();
                    }

                    else{
                        noti = new Notification(R.drawable.icon_nobg,msg, System.currentTimeMillis());
                    }

                    NotificationManager notificationManager = (NotificationManager)
                            context.getSystemService(NOTIFICATION_SERVICE);
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(0, noti);
                }
            }
        }
    }
}
