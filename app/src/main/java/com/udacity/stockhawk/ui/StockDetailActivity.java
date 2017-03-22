package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abhi on 3/20/17.
 */

public class StockDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    @BindView(R.id.barchart)
    BarChart barchart;
    private Cursor mCursor;
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> range = new ArrayList<>();
    ArrayList<Float> price = new ArrayList<>();

    BarDataSet barDataSet;
    BarData barData;
    ArrayList<BarEntry> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.string_symbol), intent.getStringExtra(getResources().getString(R.string.string_symbol)));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Contract.Quote.URI,
                new String[]{Contract.Quote.COLUMN_HISTORY_DATE, Contract.Quote.COLUMN_HISTORY_VALUE},
                Contract.Quote.COLUMN_SYMBOL + " = ?",
                new String[]{args.getString(getResources().getString(R.string.string_symbol))},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        if (mCursor.moveToFirst()) {
            date = stringToList(mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY_DATE)));
            range = stringToList(mCursor.getString(mCursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY_VALUE)));

            for (int i = 0; i < range.size(); i++) {
                price.add(Float.parseFloat(range.get(i)));
                entries.add(new BarEntry(Float.parseFloat(range.get(i)), i));
            }


            makeChart(date, price);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    private ArrayList<String> stringToList(String list) {
        return new ArrayList<>(Arrays.asList(list.split("\n")));
    }

    void makeChart(ArrayList<String> date, ArrayList<Float> price) {
        entries = new ArrayList<BarEntry>();
        for (int i = 0; i < date.size(); i++) {
            entries.add(new BarEntry(price.get(i), i));
        }
        barDataSet = new BarDataSet(entries, "Close Price");
        barData = new BarData(date, barDataSet);
        barchart.setData(barData);
        barchart.setDescription("Close price vs Date");
        barchart.animateY(1500);
        barchart.invalidate();
    }

}

