package com.takezeroapps.countit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;

import Exceptions.NoCountEnteredException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean isNegative, vibrateSetting, resetconfirmSetting, screenSetting, volumeSetting;
    int newNum;
    OperatorFragment opf = new OperatorFragment();
    Button addButton, subButton;
    ImageButton resetButton, counterButton;
    public static boolean portraitMode=true;
    Bundle in;
    int count;
    boolean inputDialogCreated;
    View counterChangeView;
    EditText input;

    @Override
    public void onResume()
    {
        //get saved settings from stored preferences
        super.onResume();

        //remove keyboard so its not stuck on screen when activity is resumed

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        vibrateSetting = prefs.getBoolean("switch_preference_vibrate", true);
        resetconfirmSetting = prefs.getBoolean("switch_preference_resetconfirm", true);
        screenSetting = prefs.getBoolean("switch_preference_screen", false);
        volumeSetting = prefs.getBoolean("switch_preference_volume", false);

        if(screenSetting)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        portraitMode=true;

        //create sharedpref for counter view if it doesn't already exist
        File f = new File("/data/data/com.takezeroapps.countit/shared_prefs/CounterView.xml");
        if(!f.exists())
        {
            SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("CounterView", 0);
            editor.commit();
        }

        SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sp.edit();
        editor1.putBoolean("orientation_key", portraitMode);
        editor1.commit();


        // Check for the rotation
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            portraitMode=false;
            opf.changeCount(count, portraitMode);

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("orientation_key", portraitMode);
            editor.commit();

            finish();
            startActivity(getIntent());
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            portraitMode=true;
            opf.changeCount(count, portraitMode);

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("orientation_key", portraitMode);
            editor.commit();

            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            if(volumeSetting) {
                addButton.performClick();
                return true;
            }
            else return false;
        } else if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            if(volumeSetting) {
                subButton.performClick();
                return true;
            }
            else return false;

        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //fixes issue where loading into landscape uses the wrong font size
        Configuration newConfig = getResources().getConfiguration();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            portraitMode=false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            portraitMode=true;
        }

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        count = sharedPref.getInt("count_key", 0);

        opf.changeCount(count, portraitMode);

        final Vibrator vib = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        final long[] pattern = {0, 20, 150, 20}; //double vibration pattern for errors

        addButton = (Button) findViewById(R.id.plusButton);
        subButton = (Button) findViewById(R.id.minusButton);
        resetButton = (ImageButton) findViewById(R.id.resetButton);
        counterButton = (ImageButton) findViewById(R.id.countButton);

        //long click on actual number count, this is to manually enter a count
        counterButton.setOnLongClickListener(
                new ImageButton.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(final View view) {
                        counterChangeView =view;
                        inputDialogCreated=true;
                        final int currcount = opf.getCount();

                        final CharSequence[] negOptions = {MainActivity.this.getResources().getString(R.string.make_negative_num)}; //choices to select from, only one choice so it only has one element
                        final ArrayList selectedItems=new ArrayList();

                        final AlertDialog.Builder counterChanger = new AlertDialog.Builder(MainActivity.this);
                        counterChanger.setTitle(R.string.change_count); //set title

                        // Set up the input
                        input = new EditText(MainActivity.this);

                        // Specify the type of input expected; this, for example, sets the input as a number, and will use the numpad
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        input.setHint(R.string.enter_new_count);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                        counterChanger.setView(input);

                        // Set up the buttons
                        counterChanger.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    String input_string = input.getText().toString().trim();

                                    if (input_string.isEmpty() || input_string.length() == 0 || input_string.equals("") || TextUtils.isEmpty(input_string)) //check if input is empty
                                    {
                                        throw new NoCountEnteredException();
                                    } else //if string is not empty, convert to int
                                        newNum = Integer.valueOf(input.getText().toString());//get integer value of new number

                                    {
                                        if (isNegative) {
                                            opf.changeCount(-1 * newNum, portraitMode); //if isNegative checkbox is checked, make the number negative
                                            count = newNum * -1;
                                        } else {
                                            opf.changeCount(newNum, portraitMode); //if checkbox is not checked, keep number the same
                                            count = newNum;
                                        }
                                    }

                                    //removes keyboard from screen when user clicks ok so it is not stuck on the screen
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                }
                                catch (NoCountEnteredException e1)
                                {
                                    if (vibrateSetting)
                                        vib.vibrate(pattern, -1);

                                    Snackbar.make(view, R.string.no_input_message, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    newNum = opf.getCount(); //set new count back to old count (or else manually setting a real number > resetting count > entering blank input = count being the original real number instead of 0 after the reset)
                                    dialog.cancel();

                                    //removes keyboard from screen when user clicks ok so it is not stuck on the screen
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                }
                                catch(Exception e2)
                                {
                                    //checking if the number is higher than maximum is no longer needed because the program will throw an exception if its too high anyway, this is where it is caught
                                    if (vibrateSetting)
                                        vib.vibrate(pattern, -1);
                                    Snackbar.make(view, R.string.invalid_number_entered, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    dialog.cancel();

                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                                }
                            }
                        });
                        counterChanger.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel(); //cancel dialog and do not save changes when "cancel" button is clicked

                                //removes keyboard from screen when user clicks cancel so it is not stuck on the screen
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            }
                        });
                        counterChanger.setMultiChoiceItems(negOptions, null, new DialogInterface.OnMultiChoiceClickListener() { //checkbox for negative number
                            @Override
                            public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                if (isChecked) {
                                    isNegative=true; //if checkbox is checked, set the boolean value for negative number as true
                                }
                                else
                                    isNegative=false; //otherwise if checkbox is not checked, then keep value positive
                            }
                        });

                        counterChanger.setOnCancelListener(new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                //removes keyboard from screen when user clicks outside of dialog box so it is not stuck on the screen
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                            }
                        });


                        counterChanger.show(); //show dialog
                        input.requestFocus();
                        InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm2.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        isNegative=false; //sets negative flag back to false after dialog is closed. This is so the input doesn't stay negative on each new change by the user

                        return true;
                    }
                }
        );

        //When the PLUS button is pressed, increment by 1
        addButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        int currAddCount=opf.getCount();
                        if((currAddCount + 1)>2147483646) //if next number would be higher than max print error
                        {
                            if(vibrateSetting)
                                vib.vibrate(pattern, -1);
                            Snackbar.make(v, R.string.max_num, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                        else //else add to count
                        {
                            if(vibrateSetting)
                                vib.vibrate(10);
                            count++;
                            opf.addCount(portraitMode);
                        }
                    }
                }
        );

        //Subtraction button
        subButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        int currSubCount=opf.getCount();
                        if((currSubCount - 1) < -2147483647) //if next number would be lower than min print error
                        {
                            if(vibrateSetting)
                                vib.vibrate(pattern, -1);
                            Snackbar.make(v, R.string.min_num, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                        else //else decrement
                        {
                            if(vibrateSetting)
                                vib.vibrate(10);
                            opf.subCount(portraitMode);
                            count--;
                        }
                    }
                }
        );
        resetButton.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        if(vibrateSetting)
                            vib.vibrate(10);
                        if(resetconfirmSetting) {
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder resetDialog = new AlertDialog.Builder(MainActivity.this);

                            // Set Dialog Title, message, and other properties
                            resetDialog.setMessage(R.string.reset_question)
                                    .setTitle(R.string.reset_title)
                            ; // semi-colon only goes after ALL of the properties

                            // Add the buttons
                            resetDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // reset count if "yes" is clicked
                                    opf.resetCount(portraitMode);
                                    count=0;
                                }
                            });
                            resetDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //cancel dialog if "no" is clicked
                                    dialog.cancel();
                                }
                            });

                            // Get the AlertDialog from create()
                            AlertDialog dialog = resetDialog.create();

                            //show dialog when reset button is clicked
                            resetDialog.show();
                        }
                        else{
                            opf.resetCount(portraitMode);
                            count=0;
                        }
                    }
                }
        );

        //Top appbar with options, do not remove
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();

        //save count
        int c = opf.getCount();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("count_key", c);
        editor.commit();

        //save orientation mode
        editor.putBoolean("orientation_key", portraitMode);
        editor.commit();

        //remove keyboard so its not stuck on screen when activity is pauses
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        if(input != null)
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

        if(counterChangeView != null)
            imm.hideSoftInputFromWindow(counterChangeView.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_settings) {
            Log.d("test", "Setting dots Pressed");
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Go to home/main activity

        } else if (id == R.id.nav_multicounter) {
            //go to multicounter
            startActivity(new Intent(MainActivity.this, CounterListActivity.class));

        } else if (id == R.id.nav_settings) {
            //go to settings
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));

        } else if (id == R.id.nav_share) {
            //let users share app

        } else if (id == R.id.nav_rate) {
            //go to app page in google store

        } else if (id == R.id.nav_contact) {
            //let users contact through email

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
