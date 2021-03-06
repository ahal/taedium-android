package me.taedium.android.add;

import me.taedium.android.R;
import me.taedium.android.widgets.InputFilterMinMax;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddTime extends WizardActivity {
    private static final int DIALOG_START_TIME = 201;
    private static final int DIALOG_END_TIME = 202;
    
    private EditText etMinDuration, etMaxDuration;
    private TextView tvStartTime, tvEndTime;
    private Button bStartTime, bEndTime;
    private int sHour = 0, sMin = 0, eHour = 23, eMin = 59;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_3_time);
        
        initializeWizard(this, AddEnvironment.class, ACTIVITY_ADD_ENVIRONMENT);
        data = getIntent().getExtras();

        title = this.getResources().getString(R.string.help_add_time_title);
        helpText = this.getResources().getString(R.string.help_add_time); 
        
        // Setup the duration edit texts
        etMinDuration = (EditText)findViewById(R.id.etAddMinDuration);
        etMaxDuration = (EditText)findViewById(R.id.etAddMaxDuration);
        
        InputFilterMinMax durationFilter = new InputFilterMinMax(0, 60*24);
        etMinDuration.setFilters(new InputFilter[] { durationFilter });
        etMaxDuration.setFilters(new InputFilter[] { durationFilter });
			
        // Setup the time pickers        
        tvStartTime = (TextView) findViewById(R.id.tvAddSTimeView);
        tvEndTime = (TextView) findViewById(R.id.tvAddETimeView);
        bStartTime = (Button) findViewById(R.id.bAddSTimeUpdate);
        bEndTime = (Button) findViewById(R.id.bAddETimeUpdate);

        // add a click listener to the button
        bStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_START_TIME);
            }
        });
        bEndTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_END_TIME);
            }
        });
        restoreData();
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DIALOG_START_TIME:
                return new TimePickerDialog(this, mStartTimeListener, sHour, sMin, false);
            case DIALOG_END_TIME:
                return new TimePickerDialog(this, mEndTimeListener, eHour, eMin, false);
            default:
                // This dialog not known to this activity
                return super.onCreateDialog(id);
        }
    }
    
    // updates the time we display in the TextView
    private void updateDisplayTime() {
        tvStartTime.setText(new StringBuilder().append(pad(sHour)).append(":").append(pad(sMin)));
        tvEndTime.setText(new StringBuilder().append(pad(eHour)).append(":").append(pad(eMin)));
    }

    private static String pad(int c) {
        if (c >= 10) return String.valueOf(c);
        else return "0" + String.valueOf(c);
    }

    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mStartTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            sHour = hour;
            sMin = minute;
            updateDisplayTime();
        }
    };
    private TimePickerDialog.OnTimeSetListener mEndTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hour, int minute) {
            eHour = hour;
            eMin = minute;
            updateDisplayTime();
        }
    };

    @Override
    protected void fillData() {
    	data.putString("min_duration", escapeString(etMinDuration.getText().toString()));
    	data.putString("max_duration", escapeString(etMaxDuration.getText().toString()));
        data.putInt("start_time", sHour * 3600 + sMin * 60);
        data.putInt("end_time", eHour * 3600 + eMin * 60);
    }

    @Override
    protected void restoreData() {
        // Restore saved settings
        if (data.containsKey("min_duration")) {
            etMinDuration.setText(data.getString("min_duration"));
        }
        
        if (data.containsKey("max_duration")) {
            etMaxDuration.setText(data.getString("max_duration"));
        }
        
        if (data.containsKey("start_time")) {
            int sTime = data.getInt("start_time");
            sHour = sTime / 3600;
            sMin = (sTime / 60) - (sHour * 60);
            updateDisplayTime();
        }
        
        if (data.containsKey("end_time")) {
            int eTime = data.getInt("end_time");
            eHour = eTime / 3600;
            eMin = (eTime / 60) - (eHour * 60);
            updateDisplayTime();
        }
        
        if (data.containsKey("min_duration")) {
            etMinDuration.setText(data.getString("min_duration"));
        }
        
        if (data.containsKey("max_duration")) {
            etMaxDuration.setText(data.getString("max_duration"));
        }
        
        if (data.containsKey("start_time")) {
            int sTime = data.getInt("start_time");
            sHour = sTime / 3600;
            sMin = (sTime / 60) - (sHour * 60);
            updateDisplayTime();
        }
        
        if (data.containsKey("end_time")) {
            int eTime = data.getInt("end_time");
            eHour = eTime / 3600;
            eMin = (eTime / 60) - (eHour * 60);
            updateDisplayTime();
        }
    }
}
