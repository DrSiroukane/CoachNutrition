package groupk.coachnutrition;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import modules.History;
import intermediatecontentprovider.IntermediateCoachNutrition;

/**
 * Group K
 * 
 * @author Slimane SIROUKANE
 * @author Fatima CHIKH
 * 
 * Fragment that help user to pick a date
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();

        // get last history
        IntermediateCoachNutrition inter = new IntermediateCoachNutrition(getContext());
        History h = inter.getLastHistory();
        if(h != null){
            Log.i("last_history", h.toString());
            c.setTime(h.getDate());
        }

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        Log.i("Date ", year +"-"+month +"-"+day);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(c.getTime());
        ((EditText) getActivity().findViewById(R.id.et_day_date)).setText(date);
    }
}