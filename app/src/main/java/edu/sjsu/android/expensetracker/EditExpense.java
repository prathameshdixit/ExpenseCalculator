package edu.sjsu.android.expensetracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditExpense extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText amountID;

    private TextInputEditText noteID;
    private Database db;
    private long id;
    private ImageButton closeButtonID;
    private Button categoryList;
    private boolean isNewExpense;
    private ExpenseClass expense;

    private static final String DATE_FORMAT = "dd MMMM yyyy";

    TextInputEditText date;

    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();

        setContentView(R.layout.expense_activity);

        date = findViewById(R.id.btnPickDate);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        date.setText(dateFormat.format(calendar.getTime()));
        System.out.println(dateFormat.format(calendar.getTime()));
        date.setKeyListener(null);


        Button btnPickDate = findViewById(R.id.date_picker);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Pick a date");
                MaterialDatePicker<Long> datePicker = builder.build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        selectedDate = Calendar.getInstance();
                        selectedDate.setTimeInMillis(selection + 86400000);

                        System.out.println(selectedDate);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        date.setText(dateFormat.format(selectedDate.getTime()));
                    }
                });

                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
        getExpenseID();
        setIDs();
        displayContent();
        onClickListeners();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void applyTheme() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this );
        int oldTheme = Integer.parseInt(sharedPreferences.getString("Theme","0"));
        if(oldTheme==0){
            int themeId = R.style.LightTheme;
            setTheme(themeId);
        }
        else if(oldTheme==1){
            int themeId = R.style.DarkTheme;
            setTheme(themeId);
        }
        else {
            Toast.makeText(this, "No theme", Toast.LENGTH_SHORT).show();
        }
    }
    private void getExpenseID() {

        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id");
    }
    private void displayContent() {

        db = new Database(this);

        if (id == -1) {
            isNewExpense = true;
            expense = new ExpenseClass();
        } else {
            expense = db.getExpense(id);
            amountID.setText(String.valueOf(expense.getmAmount()));
            categoryList.setText(expense.getmCategory());
            noteID.setText(expense.getmNote());
        }
    }
    private void onClickListeners() {
        closeButtonID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(amountID.getText().toString().trim().isEmpty() || Double.parseDouble(amountID.getText().toString())< 1){
                    Toast.makeText(EditExpense.this,"Cannot add zero amount",Toast.LENGTH_SHORT).show();
                }
                else{
                    double amount = Double.valueOf(amountID.getText().toString());//Amount will be stored as double
                    amount = Double.parseDouble(new DecimalFormat("##.##").format(amount));
                    String category = categoryList.getText().toString(); //Category will be stored as string
                    if(category.equals("Category")){
                        category = "others";
                    }
                    String note= noteID.getText().toString();
                    expense.setmAmount(amount);
                    expense.setmCategory(category);
                    expense.setmNote(note);
                    expense.setmDate(date.toString());
                    System.out.println("date = " + date);
                    if (isNewExpense) {
                        id = db.addExpense(expense);
                        expense.setmID(id);
                        isNewExpense = false;
                    } else {
                        db.updateExpense(expense);
                    }
                }
                onBackPressed();
            }
        });
        categoryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryList = new Intent(EditExpense.this, CategoryList.class);
                categoryList.putExtra("purpose",false);
                // Send the intent to launch a new activity
                startActivityForResult(categoryList,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK){
            String category = data.getStringExtra("category");
            categoryList.setText(category);
        }
    }
    private void setIDs() {

        fab = findViewById(R.id.addfab);
        amountID = findViewById(R.id.amount_edittext);
        amountID.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(6,2)});
        noteID = findViewById(R.id.addnote_edittext);
        noteID.setFilters(new InputFilter[] {new InputFilter.LengthFilter(50)});
        closeButtonID = findViewById(R.id.close_button);
        categoryList = findViewById(R.id.category_list_imagebutton);
    }
    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }

    }
}
