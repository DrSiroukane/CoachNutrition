package groupk.coachnutrition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

import modules.History;
import intermediatecontentprovider.IntermediateCoachNutrition;

/**
 * Day Activity that controls all different Day History tasks
 */
public class DayActivity extends AppCompatActivity{
    AlertDialog dialog;
    View pop_view;
    String day_date = "0000-00-00";
    Date day_date_d = null;
    float day_min = 0;
    float day_max = 0;
    History h=null;
    IntermediateCoachNutrition inter;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inter = new IntermediateCoachNutrition(this);

        EditText et_day_date = (EditText) findViewById(R.id.et_day_date);
        et_day_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment f = new DatePickerFragment();
                f.show(getFragmentManager(), "date");
            }
        });

        et_day_date.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                day_date = s.toString();
                day_date_d = History.setDate(day_date);
                Log.i("Day_date", day_date);

                // get day_date history from database || create a new history
                h = inter.getHistoryByDate(day_date);

                /*
                    * if : history of day_daye doesn't exist in Table History {
                        get last history to pick min & max;
                        if: last history doesn't exist inset a new history with min & max equal 0{
                            insert date, min=0, max=0, total_calories=0;
                        }else{
                            insert date, min=last min, max=last max, total_calories=0;
                        }

                        show popUpMinMax for enter new values of min & max
                      }
                */

                /****History of day_date  ****/
                if(h == null){//If Object History h return null, then the history doesn't exists in the TBALE_HISTORY
                    Log.d("Cursor_Empty","date doesn't exist in the TABLE_HISTORY");

                    //Get Last History to pick last Min&&Max Value
                    History h_last = inter.getLastHistory();

                    /****Last History to pick Min&Max ****/
                    if(h_last == null){
                        //Insert objet History with Min = 0 and Max = 0
                        h = new History(day_date_d,0,0,0);
                        inter.insertLineHistory(h);
                        Log.d("Insert_History","with_min&max:0"+h);
                        h = inter.getHistoryByDate(day_date);
                    }else{
                        day_min = h_last.getMin();
                        day_max = h_last.getMax();
                        Log.d("Min&Max","min "+day_min+",max"+day_max );

                        //Insert Object History with Last Values of Min and Max
                        h = new History(day_date_d, day_min, day_max, 0);
                        inter.insertLineHistory(h);
                        Log.d("Insert_History","with_last_min&max"+h);

                        h = inter.getHistoryByDate(day_date);
                        Log.d("GetObjectHistory_h","where h="+h);
                    }

                    popUpMinMaxAlert();
                }

                if(h != null){
                    day_min = h.getMin();//History min of day_date
                    day_max = h.getMax();//History Max of day_date
                    Log.d("DayActivity : date" + day_date_d, ", min " + day_min + ", max " + day_max);
                }

                ((Button) findViewById(R.id.b_obj)).setEnabled(true);
                ((Button) findViewById(R.id.b_day_meals)).setEnabled(true);
                if(h.getTotalCal() != 0) {
                    ((Button) findViewById(R.id.b_day)).setEnabled(true);
                }else{
                    ((Button) findViewById(R.id.b_day)).setEnabled(false);
                }
                ((Button) findViewById(R.id.b_graph)).setEnabled(true);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!day_date.equals("0000-00-00")){
            h = inter.getHistoryByDate(day_date);
            if((h != null) && (h.getTotalCal() != 0)) {
                ((Button) findViewById(R.id.b_day)).setEnabled(true);
            }else{
                ((Button) findViewById(R.id.b_day)).setEnabled(false);
            }
        }else{
            ((Button) findViewById(R.id.b_day)).setEnabled(false);
        }
    }

    /**
     * Method for validate min & max on validate click
     *
     * @param view
     */
    public void validateOnClick(View view){
        Log.i("b_min_max_submit button", "b_min_max_submit button get clicked");
        try{
            //Get his id , Min & Max values
            Log.d("Update_Min&Max_Into","h_id=" + h.getId());

            float min = Float.parseFloat(((TextView) pop_view.findViewById(R.id.et_min_obj)).getText().toString());
            float max = Float.parseFloat(((TextView) pop_view.findViewById(R.id.et_max_obj)).getText().toString());

            if((0 < min) && (0 < max)){
                //Check if there is a change Yes or No
                if(h.getMin() != min || h.getMax() != max){ // update min and max in day_date if it get changed
                    h.setMin(min);
                    h.setMax(max);
                    Log.d("The_New_Values_Of","min="+h.getMin()+"max="+h.getMax());

                    //Update the new values of min & max in TABLE_HISTORY
                    inter.updateHitory(h);

                    //new Values of day_min and day_max
                    day_min = h.getMin();//History min of day_date
                    day_max = h.getMax();//History Max of day_date
                    Log.d("DayActivity : date" + day_date_d, ", min " + day_min + ", max " + day_max);
                }
                Log.i("min max", "min: " + h.getMin() + " max: " + h.getMax());
            }else{
                IntermediateCoachNutrition.notification(this, "Please, Min and MAx values should be great then 0", 0);
            }
        }catch (NumberFormatException e){
            Log.d("Objectif_min&max", "Erreur", e);
            IntermediateCoachNutrition.notification(this, "Please, Min and Max values should be float !!", 0);
        }

        dialog.cancel();
    }

    /**
     * Method that has instructions to pop up Min Max window
     * to help user to insert or update Min Max for specified day
     */
    public void popUpMinMaxAlert(){
        builder = new AlertDialog.Builder(this);
        pop_view = getLayoutInflater().inflate(R.layout.objectif, null);
        ((EditText) pop_view.findViewById(R.id.et_min_obj)).setText(day_min + "");
        ((EditText) pop_view.findViewById(R.id.et_max_obj)).setText(day_max + "");
        builder.setView(pop_view);
        dialog = builder.create();
        dialog.show();
    }

    /**
     * Method to handle different button click event on DayActivity
     *
     * @param view
     */
    public void dayOnClick(View view){
        switch (view.getId()){
            case R.id.b_obj:
                Log.i("b_obj button", "b_obj button get clicked");
                popUpMinMaxAlert();
                break;

            case R.id.b_day_meals:
                Log.i("b_date_meals button", "b day meals get clicked");
                if(!day_date.equals("0000-00-00")){
                    Intent intent = new Intent(this, DayMealActivity.class);
                    intent.putExtra("history_id", h.getId());
                    intent.putExtra("day_date", day_date);
                    startActivity(intent);
                }
                break;

            case R.id.b_day:
                if(!day_date.equals("0000-00-00")){
                    Intent intent = new Intent(this, DayShowActivity.class);
                    intent.putExtra("day_date", day_date);
                    startActivity(intent);
                }
                Log.i("b_date button", "b day get clicked");
                break;

            case R.id.b_graph:
                Log.i("b_date_meals button", "b graph get clicked");
                if(!day_date.equals("0000-00-00")){
                    Intent intent = new Intent(this, GraphActivity.class);
                    intent.putExtra("day_date", day_date);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
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