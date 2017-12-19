package groupk.coachnutrition;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Date;

import modules.History;
import intermediatecontentprovider.IntermediateCoachNutrition;

/**
 * Graph Activity to display different plots of (Min, Max, TotalCalories) in a Week or less
 */
public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    IntermediateCoachNutrition inter;

    String day_date;
    SimpleCursorAdapter adapter_history;
    Spinner spinner_history;
    boolean first_time_spinner = true;

    Date db, df;
    LineGraphSeries<DataPoint> series_cal, series_min, series_max;
    GraphView graph;

    LoaderManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Spinner instructions
         */
        inter = new IntermediateCoachNutrition(this);

        adapter_history = new SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item, null,
            new String[]{IntermediateCoachNutrition.COL_DATE},
            new int[]{android.R.id.text1}, 
            0
        );

        spinner_history = (Spinner) findViewById(R.id.s_begin_date);
        spinner_history.setAdapter(adapter_history);

        Bundle extras = getIntent().getExtras();
        day_date = extras.getString("day_date");

        manager=getLoaderManager();
        manager.initLoader(0,null,this);

        /**
         * Graph instruction
         * Using GraphView Plugin
         */
        graph = (GraphView) findViewById(R.id.day_graph);

        series_cal = new LineGraphSeries<DataPoint>();
        series_min = new LineGraphSeries<DataPoint>();
        series_max = new LineGraphSeries<DataPoint>();

        updateGraph(null);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(db.getTime());
        graph.getViewport().setMaxX(df.getTime());

        // set series cal
        series_cal.setTitle("Total Calories");
        series_cal.setDrawDataPoints(true);
        series_cal.setDataPointsRadius(10);
        series_cal.setThickness(8);

        // set series min
        series_min.setTitle("Min");
        series_min.setColor(Color.GREEN);
        series_min.setDrawDataPoints(true);
        series_min.setDataPointsRadius(10);
        series_min.setThickness(8);

        // set series max
        series_max.setTitle("Max");
        series_max.setColor(Color.RED);
        series_max.setDrawDataPoints(true);
        series_max.setDataPointsRadius(10);
        series_max.setThickness(8);

        // add series to graph
        graph.addSeries(series_cal);
        graph.addSeries(series_min);
        graph.addSeries(series_max);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set scrolling
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);
    }

    /**
     * Update graph depend on selected day
     *
     * @param view
     */
    public void updateGraph(View view){
        Log.i("Update Graph", "b_update_graph get clicked");

        if(view != null){
            day_date =  adapter_history.getCursor().getString(2);
        }

        Log.i("day_date graph", day_date);

        Cursor c = inter.getWeekHistory(day_date);

        int n = c.getCount();

        DataPoint[] series_cal_points = new DataPoint[n];
        DataPoint[] series_min_points = new DataPoint[n];
        DataPoint[] series_max_points = new DataPoint[n];

        Log.i("cursor length", n+"");

        int i = n-1;
        db = null;
        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            Log.i("date", c.getPosition()+"");

            db = History.setDate(c.getString(c.getColumnIndex(IntermediateCoachNutrition.COL_DATE)));
            float min = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_MIN));
            float max = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_MAX));
            float total_cal = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_TOTAL_CALORIES_H));

            if(i == (n-1)){
                df = db;
            }

            Log.i("hist " + db.getTime() , "min: "+min+ " max: "+max+ " total_cal: "+total_cal);

            series_min_points[i] = new DataPoint(db, min);
            series_max_points[i] = new DataPoint(db, max);
            series_cal_points[i] = new DataPoint(db, total_cal);

            i--;
        }

        Log.i("serie_length", series_cal_points.length + "");

        Log.i("date_interval", db.getTime() +" - "+ df.getTime());

        series_cal.resetData(series_cal_points);
        series_min.resetData(series_min_points);
        series_max.resetData(series_max_points);

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(db.getTime());
        graph.getViewport().setMaxX(df.getTime());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Loader_Cursor","onCreateLoader");

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(IntermediateCoachNutrition.authority)
                .appendPath(IntermediateCoachNutrition.TAB_HISTORY);

        final Uri uri=builder.build();

        return new CursorLoader(
            this, 
            uri, 
            new String[]{
                "rowid as _id",
                IntermediateCoachNutrition.COL_ID_HISTORY,
                IntermediateCoachNutrition.COL_DATE
            },
            null, 
            null, 
            IntermediateCoachNutrition.COL_DATE + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("Loader_Cursor","onLoadFinished");
        adapter_history.swapCursor(data);

        /**
         * get day_date position to select it on spinner
         */
        int position = 0;
        if(first_time_spinner){
            for(data.moveToFirst(); !data.isAfterLast(); data.moveToNext()){
                if(data.getString(2).equals(day_date)){
                    break;
                }
                position++;
            }
            first_time_spinner = false;
        }

        spinner_history.setSelection(position);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter_history.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}