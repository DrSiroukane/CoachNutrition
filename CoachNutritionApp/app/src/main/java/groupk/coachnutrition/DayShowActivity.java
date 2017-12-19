package groupk.coachnutrition;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import intermediatecontentprovider.IntermediateCoachNutrition;
import modules.History;
import modules.HistoryMeal;
import modules.Meal;

/**
 * Group K
 * 
 * @author Slimane SIROUKANE
 * @author Fatima CHIKH
 * 
 * Day Show Activity to display different information about selected History
 */
public class DayShowActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{
    public String day_date;
    public IntermediateCoachNutrition inter;
    public float day_min;
    public float day_max;
    public History h;
    public HistoryMeal hm;
    public int   idh;
    public boolean submit_delete;
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


        Button b_update_meal = (Button) findViewById(R.id.b_update_day_meal);
        b_update_meal.setOnClickListener(this);


        Button b_delete_meal = (Button) findViewById(R.id.b_delete_day_meal);
        b_delete_meal.setOnClickListener(this);

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
    @Override
    public void onClick(View view){
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

                String quantity_s = ((EditText) findViewById(R.id.et_meal_quantity)).getText().toString();
                if(!quantity_s.isEmpty()) {
                    hm.setQuantity(Float.parseFloat(quantity_s));
                    //Get old Quantity in HistoryMeal and TotalCalories
                    float old_TotalCaloriesHM = hm.getTotalCaloriesHM();
                    //Get Calories
                    int idm = hm.getIdMeal();
                    Meal m = inter.getMealById(idm);
                    Log.d("dayShow", "calories " + m.getCalorie());
                    //Update new TotalCalories
                    hm.setTotalCaloriesHM((m.getCalorie() * hm.getQuantity()));
                    inter.updateHistoryMeal(hm);
                    //Update TotalCaloriesH in History
                    h.setTotalCal(h.getTotalCal() + hm.getTotalCaloriesHM() - old_TotalCaloriesHM);
                    inter.updateHitory(h);
                    ((TextView) findViewById(R.id.tv_day_total_cal)).setText(h.getTotalCal() + "");
                }else{
                    IntermediateCoachNutrition.notification(this, "Please, insert a quantity", 1);
                }
                break;

            case R.id.b_delete_day_meal:
                Log.i("b_delete_day_meal", "b_delete_day_meal button get clicked");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                submit_delete = true;
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                submit_delete = false;
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("Are you sure you want to delete this?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();

                if(submit_delete) {
                    //update TotalCalories History
                    h.setTotalCal(h.getTotalCal() - hm.getTotalCaloriesHM());
                    //delete MealHistoryMeal
                    inter.deleteMealHistoryMeal(hm.getIdMeal(), hm.getIdHistory());
                    //Update TotalCalorie History
                    inter.updateHitory(h);
                    ((TextView) findViewById(R.id.tv_day_total_cal)).setText(h.getTotalCal() + "");
                }
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