package groupk.coachnutrition;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import intermediatecontentprovider.IntermediateCoachNutrition;
import modules.History;

/**
 * Main Activity HealthCheck
 */
public class HealthCheckActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    IntermediateCoachNutrition inter;
    History last_history;

    SimpleCursorAdapter adapter_week;
    ListView list_week;

    LoaderManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_check);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        inter = new IntermediateCoachNutrition(this);
        last_history = inter.getLastHistory();

        adapter_week = new SimpleCursorAdapter(
                this,
                R.layout.list_item_history,
                null, //pas de cursor pour l'instant
                new String[]{
                        IntermediateCoachNutrition.COL_DATE,
                        IntermediateCoachNutrition.COL_MIN,
                        IntermediateCoachNutrition.COL_MAX,
                        IntermediateCoachNutrition.COL_TOTAL_CALORIES_H,
                        IntermediateCoachNutrition.COL_MARK
                },
                new int[]{
                    R.id.tv_item_date, 
                    R.id.tv_item_min, 
                    R.id.tv_item_max, 
                    R.id.tv_item_total_cal, 
                    R.id.tv_item_mark
                }
        );

        list_week = (ListView) findViewById(R.id.lv_week);
        list_week.setAdapter(adapter_week);

        manager = getLoaderManager();
        manager.initLoader(0,null,this);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        manager.restartLoader(0, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meals) {
            Intent intent = new Intent(this, MealActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_day) {
            IntermediateCoachNutrition inter = new IntermediateCoachNutrition(this);
            if(inter.checkMeals()){
                Intent intent = new Intent(this, DayActivity.class);
                startActivity(intent);
            }else{
                IntermediateCoachNutrition.notification(this, "There is no Meals on database.\nPlease, try to insert some first.", 1);
            }

        }else if (id == R.id.nav_exit){
            finish();
            System.exit(1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method that fill Day field by its data
     * 
     * @param c 
     */
    public void fillDayData(Cursor c){
        // code that fill day part
        if(c.getCount() == 0){
            ((LinearLayout) findViewById(R.id.ll_day)).setVisibility(LinearLayout.GONE);
            return;
        }

        c.moveToFirst();

        TextView tv_date = (TextView) findViewById(R.id.tv_day_date);
        TextView tv_min = (TextView) findViewById(R.id.tv_day_min);
        TextView tv_max = (TextView) findViewById(R.id.tv_day_max);
        TextView tv_cal = (TextView) findViewById(R.id.tv_day_cal);
        TextView tv_mark = (TextView) findViewById(R.id.tv_day_mark);

        String date = c.getString(c.getColumnIndex(IntermediateCoachNutrition.COL_DATE));
        float min = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_MIN));
        float max = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_MAX));
        float cal = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_TOTAL_CALORIES_H));
        float mark = c.getFloat(c.getColumnIndex(IntermediateCoachNutrition.COL_MIN));

        tv_date.setText(date);
        tv_min.setText(min + "");
        tv_max.setText(max + "");
        tv_cal.setText(cal + "");
        tv_mark.setText(mark + "");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(IntermediateCoachNutrition.authority)
                .appendPath(IntermediateCoachNutrition.TAB_HISTORY);
        final Uri uri=builder.build();

        last_history = inter.getLastHistory();

       return new CursorLoader(
            this,
            uri,
            new String[]{
                    "rowid as _id",
                    IntermediateCoachNutrition.COL_DATE, 
                    IntermediateCoachNutrition.COL_MIN, 
                    IntermediateCoachNutrition.COL_MAX, 
                    IntermediateCoachNutrition.COL_TOTAL_CALORIES_H,
                    IntermediateCoachNutrition.COL_MARK
                },
            IntermediateCoachNutrition.COL_DATE + " <= '" + ((last_history != null) ? History.getDate(last_history.getDate()) : "0000-00-00") + "'",
            null,
            IntermediateCoachNutrition.COL_DATE + " DESC LIMIT 7"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(last_history != null){
            // Cursor cursor = inter.getWeekHistory(History.getDate(last_history.getDate()));

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) list_week.getLayoutParams();
            lp.height = data.getCount() * 210;
            list_week.setLayoutParams(lp);

            if(data.getCount() != 0){
                fillDayData(data);
            }

            findViewById(R.id.ll_day).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_week).setVisibility(View.VISIBLE);
        }

        adapter_week.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter_week.swapCursor(null);
    }
}