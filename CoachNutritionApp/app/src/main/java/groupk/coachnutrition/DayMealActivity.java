package groupk.coachnutrition;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import intermediatecontentprovider.IntermediateCoachNutrition;
import modules.History;
import modules.HistoryMeal;
import modules.Meal;

/**
 * Day Meal Activity that handle adding Meal to Pivot table for selected History on Day Activity
 */
public class DayMealActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public String  day_date;
    public Spinner spinner_day_meal;
    public SimpleCursorAdapter adapter_day_meal;
    public LoaderManager manager_day_meal;
    public static  long id;
    public static float calorie;
    public Meal m;
    public History h;
    public HistoryMeal hm;
    public IntermediateCoachNutrition inter;
    public static int id_day_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_meal);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get_day_date
        Bundle extras = getIntent().getExtras();
        day_date = extras.getString("day_date");
        Log.d("day_date",""+day_date);

        inter=new IntermediateCoachNutrition(this);
        h = inter.getHistoryByDate(day_date);
        Log.d("History","h: "+h);
        id_day_date=h.getId();
        Log.d("id_day_date",""+id_day_date);  
        ((TextView) findViewById(R.id.tv_meal_date)).setText(day_date);

        /**Spinner_day_meal**/
        spinner_day_meal = (Spinner) findViewById(R.id.s_day_meals);

        adapter_day_meal = new SimpleCursorAdapter( 
            this,
            android.R.layout.simple_spinner_item, 
            null,
            new String[]{IntermediateCoachNutrition.COL_NAME},
            new int[]{android.R.id.text1}, 
            0
        );
        spinner_day_meal.setAdapter(adapter_day_meal);
    
        /**manager_day_meal***/
        manager_day_meal = getLoaderManager();
        manager_day_meal.initLoader(0,null,this);
    }

    /**
     * Method handle adding a new History_Meal line on pivot table
     * after selecting a Meal from spinner then clicking to ADD button
     * 
     * @param view
     */
    public void addDayMeal(View view){
        Log.i("b_day_add_meal", "b_day_add_meal get clicked");
        //id of selection in adapter_day_meal
        if(spinner_day_meal != null){
            int position = spinner_day_meal.getSelectedItemPosition();
            id = adapter_day_meal.getItemId(position);
            Log.d("addDayMeal","id_meal" + id);
        }

        //Geet_meal
        m = inter.getMealById((int) id);
        Log.d("addDayMeal","GetMeal" + m);
        if(m != null) {
                calorie = m.getCalorie();//Calorie of object Meal
                Log.d("addDayMeal","meal_calorie" + calorie);
        }

        String quantity_s = ((EditText) findViewById(R.id.et_day_meal_quantity)).getText().toString();
        if(!quantity_s.isEmpty()){
            // new meal and its quantity with history(day_date)
            try{
                float quantity = Float.parseFloat(quantity_s);
                Log.d("addDayMeal","quantity " + quantity);
                hm=new HistoryMeal(m.getId(),h.getId(),quantity,(calorie*quantity));
                Log.d("HistoryMeal","_idm " + hm.getIdMeal() + "_idH " + hm.getIdHistory() + "_TCalories " + hm.getTotalCaloriesHM());
                if(hm!=null){
                    Log.d("addDayMeal","HistoryMeal " + hm);
                    //insert in TABLE_HISTORYMEALS
                    inter.insertLineHistorisationMeal(hm);
                    //set the new values of TotalCalories H
                    if(h!=null){
                        //insert new values of total calories all meals of the day in TABLE History
                        Log.d("addDayMeal","totalcaloriesH" + h.getTotalCal());
                        h.setTotalCal(h.getTotalCal() + hm.getTotalCaloriesHM());
                        Log.d("addMeal","update_caloriesH " + h.getTotalCal());
                        inter.updateHitory(h);
                        Log.d("addMeal","updateTCaloriesH " + h.getTotalCal());
                    }
                }
            }catch( NumberFormatException e){
                IntermediateCoachNutrition.notification(this, "Please, be sure that you insert number !!", 1);
            }
        }else{
            IntermediateCoachNutrition.notification(this, "Please, insert a quantity", 1);
        }



        manager_day_meal.restartLoader(0, null, this);
    }

    //Methodes Callback
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Cursor cursor = inter.getHistoryMeal(h.getId());

        // Generate string that has all Meal ids exsiting on History_Meal
        String history_meals = "( ";
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            history_meals  += ((history_meals.equals("( ")) ? "" : ",") + cursor.getInt(0);
        }
        history_meals += " )";

        Log.i("existe histories", history_meals);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(IntermediateCoachNutrition.authority)
                .appendPath(IntermediateCoachNutrition.TAB_MEAL);
        final Uri uri=builder.build();

        Log.d("uri=", uri.toString());
        Log.d("Loader_Cursor","onCreateLoader");

        String sort_order = IntermediateCoachNutrition.COL_NAME;

        return new CursorLoader(
            this, 
            uri, 
            new String[]{"rowid as _id",IntermediateCoachNutrition.COL_ID_MEAL,
            IntermediateCoachNutrition.COL_NAME},
            IntermediateCoachNutrition.COL_ID_MEAL + " not in "+ history_meals,
            null, 
            sort_order
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        adapter_day_meal.swapCursor(data);
        if(data.getCount() == 0){
            IntermediateCoachNutrition.notification(this, "It didn't leave any Meal to add", 1);
            this.finish();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter_day_meal.swapCursor(null);
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