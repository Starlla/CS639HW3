package com.example.cs639hw3;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    ArrayList data;
    ArrayList xList;
    LineGraphView mLineGraphView;
    EditText dateText;
    EditText countText;
    Button addDataButton;
    Button clearButton;
    SeekBar sk;
    CheckBox showLinesCheckBox;
    CheckBox highlightIntegralCheckBox;
    DateValidator dateValidator;
    boolean valid;
    String[] monthAndDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateText = findViewById(R.id.date_text_edit);
        countText = findViewById(R.id.count_text_edit);

        data = new ArrayList<Integer>();
        xList = new ArrayList<String>();

        mLineGraphView = findViewById(R.id.line_graph_view);
        mLineGraphView.addData(data, xList);

        clearButton = findViewById(R.id.clearDataButton);
        addDataButton = findViewById(R.id.addDataButton);
        sk=findViewById(R.id.seekBar);
        showLinesCheckBox = findViewById(R.id.show_lines);
        highlightIntegralCheckBox = findViewById(R.id.highlight_integral);
        dateValidator = new DateValidator();


        setAddTextChangedListener();
        setClearDataButtonOnClickListener();
        setAddDataButtonOnClickListener();
        setOnSeekerBarListener();
        setCheckBoxesOnCheckedChangeListener();

    }

    private void setClearDataButtonOnClickListener(){
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                xList.clear();
                mLineGraphView.invalidate();
            }
        });
    }

    private void setAddTextChangedListener(){
        countText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable edt) {
                if (edt.length() == 1 && edt.toString().equals("0"))
                    countText.setText("");
            }
        });

        dateText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable edt) {
                valid = dateValidator.validate(edt.toString());
            }
        });
    }

    private void setAddDataButtonOnClickListener(){
        addDataButton.setOnClickListener(view1 ->  {

            if (dateText.getText().toString().trim().isEmpty() || countText.getText().toString().trim().isEmpty())
                Toast.makeText(this, R.string.filed_could_not_be_empty, Toast.LENGTH_LONG).show();
            else if(!valid)
                Toast.makeText(MainActivity.this,R.string.date_not_valid,Toast.LENGTH_SHORT).show();
            else {
                //Add Leading 0 for date
                monthAndDay = dateText.getText().toString().split("/");
                if(monthAndDay[0].length() == 1)
                    monthAndDay[0] = "0" + monthAndDay[0];
                if(monthAndDay[1].length() == 1)
                    monthAndDay[1] = "0" + monthAndDay[1];
                dateText.setText(getString(R.string.new_date_format,monthAndDay[0],monthAndDay[1]));

                //Check if the Date is already exist
                String dateString = dateText.getText().toString();
                int countNumber = Integer.parseInt(countText.getText().toString());
                if(xList.contains(dateString))
                    data.set(xList.indexOf(dateString),countNumber);
                else {
                    if (xList.size() == 5) {
                        xList.remove(0);
                        data.remove(0);
                    }
                    xList.add(dateString);
                    data.add(countNumber);
                }
                dateText.setText("");
                countText.setText("");
                mLineGraphView.addData(data, xList);
            }
        });
    }

    private void setOnSeekerBarListener(){
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                mLineGraphView.mPointRadius = dpToPx(5) * (1 + progress * 0.01f);
                mLineGraphView.invalidate();
            }
        });

    }

    private void setCheckBoxesOnCheckedChangeListener(){
        showLinesCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mLineGraphView.showLines(true);
                    mLineGraphView.invalidate();
                }
                else {
                    mLineGraphView.showLines(false);
                    mLineGraphView.invalidate();
                }
            }
        });

        highlightIntegralCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mLineGraphView.highlightIntegral(true);
                    mLineGraphView.invalidate();
                }
                else {
                    mLineGraphView.highlightIntegral(false);
                    mLineGraphView.invalidate();
                }
            }
        });

    }

    private int dpToPx(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }



}



