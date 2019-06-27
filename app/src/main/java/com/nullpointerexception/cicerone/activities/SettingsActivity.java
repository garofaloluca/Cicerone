package com.nullpointerexception.cicerone.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nullpointerexception.cicerone.R;
import com.nullpointerexception.cicerone.components.AuthenticationManager;
import com.nullpointerexception.cicerone.components.BackEndInterface;
import com.nullpointerexception.cicerone.components.User;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private EditText mName, mSurname, mPhone, mDate;
    private Menu menu;
    private Toolbar toolbar;

    //Datepicker object
    private Calendar calendar;
    private DatePickerDialog dpd;

    //Istance of user
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Initialization of UI
        initUI();

        toolbar.setTitle("Modifica profilo");

        //Set toolbar
        setSupportActionBar(toolbar);

        //Check if actionbar is initialized
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Change color of toolbar
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        user = AuthenticationManager.get().getUserLogged();

        //set field in activity
        setTextField();
    }

    private void initUI() {
        mName = findViewById(R.id.name_et);
        mSurname = findViewById(R.id.surname_et);
        mPhone = findViewById(R.id.phone_et);
        mDate = findViewById(R.id.date_et);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setTextField() {
        //set name in hint
        mName.setHint(user.getName());

        //set surname in hint
        mSurname.setHint(user.getSurname());

        //set phone in hint
        mPhone.setHint(user.getPhoneNumber());

        //set date in hint
        mDate.setHint(user.getDateBirth());

        //set date picker
        mDate.setOnClickListener(v -> {
            calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            dpd = new DatePickerDialog(this, R.style.DialogTheme,
                    (view1, year1, month1, dayOfMonth) -> {
                        String birthdayString = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        mDate.setText(birthdayString);
                    }, year, month, day);
            dpd.show();
        });
    }

    /**
     * @param menu The options menu in which you place your items
     * @return true if menu has been create
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_settings, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.modify_menu_btn) {

            if(!mName.getText().toString().isEmpty())
                user.setName(mName.getText().toString());

            if(!mSurname.getText().toString().isEmpty())
                user.setSurname(mSurname.getText().toString());

            if(!mDate.getText().toString().isEmpty())
                user.setDateBirth(mDate.getText().toString());

            if(!mPhone.getText().toString().isEmpty())
                user.setPhoneNumber(mPhone.getText().toString());

            BackEndInterface.get().removeEntity(user, new BackEndInterface.OnOperationCompleteListener() {
                @Override
                public void onSuccess() {
                    BackEndInterface.get().storeEntity(user);
                    finish();
                }

                @Override
                public void onError() {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}

