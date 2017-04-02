package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.content.CursorLoader;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by abhi on 4/2/17.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class RecyclerWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList<Content> content = new ArrayList<>();
    Content contents = new Content();
    private Context context = null;
    private int appWidgetId;
    private Cursor mCursor;

    public RecyclerWidgetAdapter(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

             mCursor = context.getContentResolver().query(Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_item_quote);

        if (mCursor.moveToPosition(position)) {


            remoteView.setTextViewText(R.id.symbol, mCursor.getString(Contract.Quote.POSITION_SYMBOL));
            remoteView.setTextViewText(R.id.price, mCursor.getString(Contract.Quote.POSITION_PRICE));
            remoteView.setTextViewText(R.id.change, mCursor.getString(Contract.Quote.POSITION_PERCENTAGE_CHANGE));

            if (mCursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE) > 0) {
                remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            }
            else {
                remoteView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            Intent newIntent = new Intent();
            newIntent.putExtra("symbol", mCursor.getString(Contract.Quote.POSITION_SYMBOL));
            // In setOnClickFillIntent method, the ID to be passed is of the Rootview
            // of the layout passed in the remote view - above, i.e. rootview of the list_item_quote.
            remoteView.setOnClickFillInIntent(R.id.list_item_quote_rootview, newIntent);
        }

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
}

