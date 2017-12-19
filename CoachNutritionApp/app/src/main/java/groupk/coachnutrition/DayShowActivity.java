package groupk.coachnutrition;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import modules.History;
import modules.HistoryMeal;
import modules.Meal;
import intermediatecontentprovider.IntermediateCoachNutrition;

/**
 * Day Show Activity to display different information about selected History
 */
public class DayShowActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public String day_date;
    public IntermediateCoachNutrition inter;
    public float day_min;
    public float day_max;
    public History h;
    public HistoryMeal hm;
    public int   idh;
    public int idm;
    public Spinner spinner_day_show;
    public SimpleCursorAdapter adapter_day_show;
    public LoaderManager manager_day_show;
    public long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_show);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        day_date = extras.getString("day_date");
        Log.d("dayShow","day_date " + day_date);

        inter = new IntermediateCoachNutrition(this);
    
        //Get Min & Max Values
        h = inter.getHistoryByDate(day_date);
        Log.d("dayShow", "history " + h);

        //Get idhitory, min & max
        idh = h.getId();
        day_min = h.getMin();
        day_max = h.getMax();
        Log.d("dayShow","Min:" + day_min+",Max: " + day_max);
        Log.i("dayShow : extras values", "day_date: "+ day_date + " day_min: "+ day_min + " day_max: "+ day_max);

        ((TextView) findViewById(R.id.tv_meal_date)).setText(day_date);
        ((TextView) findViewById(R.id.tv_meal_min)).setText(day_min + "");
        ((TextView) findViewById(R.id.tv_meal_max)).setText(day_max + "");
        ((TextView) findViewById(R.id.tv_day_total_cal)).setText(h.getTotalCal()+"");

        /****Spinner_day_show****/
        spinner_day_show = (Spinner) findViewById(R.id.s_day_meals);

        adapter_day_show = new SimpleCursorAdapter( 
            this,
            android.R.layout.simple_spinner_item, 
            null,
            new String[]{IntermediateCoachNutrition.COL_NAME},
            new int[]{android.R.id.text1}, 
            0
        );

        Log.d("simple_cursor_adapter","" + adapter_day_show.getCursorToStringConverter());
        spinner_day_show.setAdapter(adapter_day_show);
        
        spinner_day_show.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                id = adapter_day_show.getItemId(position);
                Log.d("addDayMeal","id_meal" + id);
                hm = inter.getHistoryMeal(h.getId(), (int) id);
                Log.i("spinner listener hm", "id: "+ id + " quantity: " + hm.getQuantity());
                ((EditText) findViewById(R.id.et_meal_quantity)).setText(hm.getQuantity() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /****manager_day_show****/
        manager_day_show = getLoaderManager();
        manager_day_show.initLoader(0, null, this);
    }

    /**
     * Method that handle different buttons onClick
     * Update || Delete selected Meal in spinner
     * 
     * @param view
     */
    public void dayMealOnClick(View view){
        //id of selection in adapter_day_meal
        if(spinner_day_show != null){
            int position = spinner_day_show.getSelectedItemPosition();
            id = adapter_day_show.getItemId(position);
            Log.d("addDayMeal","id_meal" + id);
            hm = inter.getHistoryMeal(h.getId(), (int) id);
        }else{
            return;
        }

        switch (view.getId()) {
            case R.id.b_update_day_meal:
                Log.i("b_update_day_meal", "b_update_day_meal button get clicked");
                // set new quantity on history meal
                hm.setQuantity(Float.parseFloat(((EditText) findViewById(R.id.et_meal_quantity)).getText().toString()));
                //Get old Quantity in HistoryMeal and TotalCalories
                float old_TotalCaloriesHM = hm.getTotalCaloriesHM();
                //Get Calories
                int idm = hm.getIdMeal();
                Meal m = inter.getMealById(idm);
                Log.d("dayShow","calories " + m.getCalorie());
                //Update new TotalCalories
                hm.setTotalCaloriesHM((m.getCalorie()*hm.getQuantity()));
                inter.updateHistoryMeal(hm);
                //Update TotalCaloriesH in History
                h.setTotalCal(h.getTotalCal() + hm.getTotalCaloriesHM()-old_TotalCaloriesHM);
                inter.updateHitory(h);
                ((TextView) findViewById(R.id.tv_day_total_cal)).setText(h.getTotalCal()+"");
                break;

            case R.id.b_delete_day_meal:
                Log.i("b_delete_day_meal", "b_delete_day_meal button get clicked");
                //update TotalCalories History
                h.setTotalCal(h.getTotalCal()-hm.getTotalCaloriesHM());
                //delete MealHistoryMeal
                inter.deleteMealHistoryMeal(hm.getIdMeal(),hm.getIdHistory());
                //Update TotalCalorie History
                inter.updateHitory(h);
                ((TextView) findViewById(R.id.tv_day_total_cal)).setText(h.getTotalCal()+"");
                break;

            default:
                break;
        }

        manager_day_show.restartLoader(0 , null , this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(IntermediateCoachNutrition.authority)
                .appendPath("all_tables");
        builder = ContentUris.appendId(builder, h.getId());
        final Uri uri = builder.build();
        Log.d("uri=", uri.toString());
    
        return new CursorLoader(
            this, 
            uri, 
            new String[]{
                IntermediateCoachNutrition.COL_ID_MEAL ,
                IntermediateCoachNutrition.COL_NAME , 
                IntermediateCoachNutrition.COL_QUANTITY
            },
            null, 
            null, 
            null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() == 0){
            finish();
        }

        adapter_day_show.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter_day_show.swapCursor(null);
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