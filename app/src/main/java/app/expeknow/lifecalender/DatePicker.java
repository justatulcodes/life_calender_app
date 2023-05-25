package app.expeknow.lifecalender;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import app.expeknow.lifecalender.R;

import java.util.Calendar;
import java.util.Date;


public class DatePicker extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.MyDatePickerStyle,
                (DatePickerDialog.OnDateSetListener) getActivity(), year, month, dayOfMonth);
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        return dialog;

    }
}
