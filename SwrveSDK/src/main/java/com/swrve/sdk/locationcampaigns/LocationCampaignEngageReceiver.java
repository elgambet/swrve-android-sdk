package com.swrve.sdk.locationcampaigns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.plotprojects.retail.android.FilterableNotification;
import com.swrve.sdk.SwrveBase;
import com.swrve.sdk.SwrveHelper;
import com.swrve.sdk.SwrveLogger;
import com.swrve.sdk.SwrveSDKBase;

public class LocationCampaignEngageReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "SwrveLocation";

    @Override
    public void onReceive(Context context, Intent intent) {
        FilterableNotification filterableNotification = intent.getParcelableExtra("notification");
        if (filterableNotification == null || SwrveHelper.isNullOrEmpty(filterableNotification.getData())) {
            SwrveLogger.e(LOG_TAG, "LocationCampaignEngageReceiver. Payload data is null or empty or missing.");
        } else {
            ((SwrveBase) (SwrveSDKBase.getInstance())).initSDKForLocationCampaigns(context); // TODO: Make sure certain variables have been initialised. For FA hack this. For GA fix this.

            // Queue event. Wakelock not required because app will be started in foreground.
            String locationMessageId = filterableNotification.getData();
            SwrveSDKBase.event("Swrve.Location.Location-" + locationMessageId + ".engaged");
        }

        launchApp(context, intent.getExtras());
    }

    private void launchApp(Context context, Bundle extras) {
        Intent openIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (openIntent != null) {
            if (extras != null) {
                openIntent.putExtras(extras);
            }
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(openIntent);
        } else {
            SwrveLogger.e(LOG_TAG, "LocationCampaignEngageReceiver. Cannot find launch intent.");
        }
    }
}