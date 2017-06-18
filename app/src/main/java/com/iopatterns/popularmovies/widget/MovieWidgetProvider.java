package com.iopatterns.popularmovies.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.iopatterns.popularmovies.MainActivity;
import com.iopatterns.popularmovies.MovieDetails;
import com.iopatterns.popularmovies.R;

/**
 * Created by self on 17.6.18.
 */

public class MovieWidgetProvider extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++)
        {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widget_textview, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent)
    {
        Log.d("WIDGET 1", "KDJF;ALKDJFAL;KDJ");
        super.onReceive(context, intent);
        if (MovieDetails.ACTION_DATA_UPDATED.equals(intent.getAction()))
        {
            Log.d("WIDGET 5", "KDJF;ALKDJFAL;KDJ");
            //context.startService(new Intent(context, MainActivity.class));
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                    MovieWidgetProvider.class));
            Log.d("WIDGET ID: ", String.valueOf(appWidgetIds.length));

            for(int appWidgetId : appWidgetIds)
            {
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
                views.setTextViewText(R.id.widget_textview, "PADRINO");

                // Tell the AppWidgetManager to perform an update on the current app widget
                appWidgetManager.updateAppWidget(appWidgetId, views);

                Log.d("WIDGET 4", "KDJF;ALKDJFAL;KDJ");
            }



        }
    }

}

