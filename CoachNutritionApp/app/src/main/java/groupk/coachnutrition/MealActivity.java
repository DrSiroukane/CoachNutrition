package groupk.coachnutrition;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import intermediatecontentprovider.IntermediateCoachNutrition;
import modules.Meal;

/**
 * Group K
 * 
 * @author Slimane SIROUKANE
 * @author Fatima CHIKH
 * 
 * Meal Activity that handle different tasks of Meal
 */
public class MealActivity extends AppCompatActivity implements View.OnClickListener,LoaderManager.LoaderCallbacks<Cursor> {
    AlertDialog dialog;
    View pop_view;
    private IntermediateCoachNutrition inter;
    private Spinner spinner_meal;
    public SimpleCursorAdapter adapter_meal;
    public Meal m;
    Button b_update_meal;
    Button b_delete_meal;
    boolean submit_delete = false;
    private LoaderManager manager;
    private static final int READ_REQUEST_CODE = 42;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button b_add_meal = (Button) findViewById(R.id.b_add_meal);
        b_add_meal.setOnClickListener(this);

        b_update_meal = (Button) findViewById(R.id.b_update_meal);
        b_update_meal.setOnClickListener(this);

        b_delete_meal = (Button) findViewById(R.id.b_delete_meal);
        b_delete_meal.setOnClickListener(this);

        Button b_upload_meal = (Button) findViewById(R.id.b_upload_meal);
        b_upload_meal.setOnClickListener(this);

        inter = new IntermediateCoachNutrition(this);
        
        /****Spinner_meal****/
        spinner_meal = (Spinner) findViewById(R.id.spinner);
        adapter_meal = new SimpleCursorAdapter(
            this,
            android.R.layout.simple_spinner_item, 
            null,
            new String[]{IntermediateCoachNutrition.COL_NAME},
            new int[]{android.R.id.text1}, 
            0
        );
        Log.d("simple_cursor_adapter",""+adapter_meal.getCursorToStringConverter());
        spinner_meal.setAdapter(adapter_meal);
        
        /****manager****/
        manager=getLoaderManager();
        manager.initLoader(0,null,this);
    }

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {
        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    uploadDataMeal(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("file path", "Uri: " + uri.toString());
            }
        }
    }

    /**
     * Method to insert data from choosen CSV file to database
     * 
     * @param  uri         
     * @throws IOException 
     */
    public void uploadDataMeal(Uri uri) throws IOException {
        inter=new IntermediateCoachNutrition(this);

        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] str = line.split(",");
                inter.insertLineMeal(new Meal(
                    str[0],
                    Float.parseFloat(str[1])
                ));
            }
            IntermediateCoachNutrition.notification(this, "The insertion of data from the selected CSV file has been completed with success", 1);
        } catch (IOException e) {
            e.printStackTrace();
            IntermediateCoachNutrition.notification(this, "Something went wrong !!\nPlease, Check your csv file.", 1);
        }

        reader.close();
        inputStream.close();

        manager.restartLoader(0, null, this);
    }
    
    /**
     * Method handle Validate button to insert or update specified Meal
     * 
     * @param view 
     */
    public void submitMeal(View view) {
        Log.i("submit meal", "submit meal button get clicked (add || update)");
        int id = Integer.parseInt(((EditText) pop_view.findViewById(R.id.et_meal_id)).getText().toString());

        String nom = ((EditText) pop_view.findViewById(R.id.et_meal_name)).getText().toString();
        String calorie_s = ((EditText) pop_view.findViewById(R.id.et_meal_cal)).getText().toString();

        if(!nom.isEmpty() && !calorie_s.isEmpty()) {
            float calorie = Float.parseFloat(calorie_s);
            if (id == -1) {
                Log.d("submit_meal", "nom " + nom + ", calorie " + calorie);

                Meal m = new Meal(nom, calorie);
                if (m != null) {
                    Log.d("Meal object", "name : " + m.getNom() + ", calorie : " + m.getCalorie());
                    inter.insertLineMeal(m);
                    Log.d("insertLineMeal", "" + m);
                }

            } else {
                m.setNom(((EditText) pop_view.findViewById(R.id.et_meal_name)).getText().toString());
                m.setCalorie(Float.parseFloat(((EditText) pop_view.findViewById(R.id.et_meal_cal)).getText().toString()));
                inter.updateMeal(m);
                Log.d("submit_meal", "updateMeal");
            }

            manager.restartLoader(0,null,this);
            dialog.cancel();
        }else{
            IntermediateCoachNutrition.notification(this, "Please, fill all fields.", 1);
        }
    }

    /**
     * Method that handle different buttons on Day Activity
     * 
     * @param view 
     */
    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder;
        long id;//= spinnerID;  //spinner.getSelectedItemId();
        int position = spinner_meal.getSelectedItemPosition();
        id = adapter_meal.getItemId(position);
        Log.d("id spinner",""+id);
        switch (view.getId()) {
            case R.id.b_add_meal:
                Log.i("pop add meal", "add meal button get clicked");
                builder = new AlertDialog.Builder(MealActivity.this);
                pop_view = getLayoutInflater().inflate(R.layout.meal_data, null);
                ((EditText) pop_view.findViewById(R.id.et_meal_id)).setText("-1");
                builder.setView(pop_view);
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.b_update_meal:
                Log.i("pop update meal", "update meal button get clicked");
                builder = new AlertDialog.Builder(MealActivity.this);
                pop_view = getLayoutInflater().inflate(R.layout.meal_data, null);
                m = inter.getMealById((int) id);
                ((EditText) pop_view.findViewById(R.id.et_meal_id)).setText(m.getId() + "");
                ((EditText) pop_view.findViewById(R.id.et_meal_name)).setText(m.getNom());
                ((EditText) pop_view.findViewById(R.id.et_meal_cal)).setText(m.getCalorie() + "");
                builder.setView(pop_view);
                dialog = builder.create();
                dialog.show();
                break;

            case R.id.b_delete_meal:
                Log.d("delete_meal", "button get clicked");
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

                if(submit_delete){
                    IntermediateCoachNutrition inter = new IntermediateCoachNutrition(this);
                    inter.deleteMeal((int) id);
                    Log.d("delete_meal", "delete meal selected " + id);
                }

                break;

            case R.id.b_upload_meal:
                Log.i("pop upload meal", "upload meal button get clicked");
                String way = "";
                performFileSearch();
                break;

            default:
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content")
                .authority(IntermediateCoachNutrition.authority)
                .appendPath(IntermediateCoachNutrition.TAB_MEAL);
        final Uri uri=builder.build();
        Log.d("uri=", uri.toString());

        String sort_order = IntermediateCoachNutrition.COL_NAME;
       
        return new CursorLoader(
            this, 
            uri,
            new String[]{"rowid as _id",IntermediateCoachNutrition.COL_ID_MEAL, IntermediateCoachNutrition.COL_NAME},
            null, 
            null,
            sort_order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.getCount() == 0){
            b_update_meal.setEnabled(false);
            b_delete_meal.setEnabled(false);
        }else{
            b_update_meal.setEnabled(true);
            b_delete_meal.setEnabled(true);
        }

        adapter_meal.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter_meal.swapCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}