package com.takezeroapps.countit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

/**
 * Created by scoob on 1/7/2017.
 */
public class SingleCounterFragment extends Fragment{

    TextView counterName, counterCount;
    Button plusButton, minusButton;
    ImageButton resetButton;
    String mcName, cName;
    int cCount;

    public static SingleCounterFragment newInstance(String mcName, String cName, int cCount) {
        SingleCounterFragment myFragment = new SingleCounterFragment();
        Bundle args = new Bundle();
        args.putString("mcName", mcName);
        args.putString("cName", cName);
        args.putInt("cCount", cCount);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try
        {
            mcName=getArguments().getString("mcName");
            cName = getArguments().getString("cName");
            cCount = getArguments().getInt("cCount");
            counterName.setText(cName);
            counterCount.setText(Integer.toString(cCount));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sc_counter_fragment, container, false);
        counterName=(TextView)view.findViewById(R.id.scounter_name);
        counterCount=(TextView)view.findViewById(R.id.scounter_count);
        plusButton=(Button)view.findViewById(R.id.scounter_plus);
        minusButton=(Button)view.findViewById(R.id.scounter_minus);
        resetButton=(ImageButton)view.findViewById(R.id.scounter_reset);

        //Addition Button
        plusButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        int currAddCount=getCount();
                        if((currAddCount + 1)>2147483646) //if next number would be higher than max print error
                        {
                            //if(vibrateSetting)
                            //    vib.vibrate(pattern, -1);
                            Snackbar.make(v, R.string.max_num, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                        else //else add to count
                        {
                            //if(vibrateSetting)
                            //    vib.vibrate(10);
                            addCount();
                        }
                    }
                }
        );

        //Subtraction button
        minusButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        int currSubCount=getCount();
                        if((currSubCount - 1) < -2147483647) //if next number would be lower than min print error
                        {
                            //if(vibrateSetting)
                            //    vib.vibrate(pattern, -1);
                            Snackbar.make(v, R.string.min_num, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }
                        else //else decrement
                        {
                            //if(vibrateSetting)
                            //    vib.vibrate(10);
                            subCount();
                        }
                    }
                }
        );
        resetButton.setOnClickListener(
                new ImageButton.OnClickListener(){
                    public void onClick(View v){
                        resetCount();
                        //if(vibrateSetting)
                        //    vib.vibrate(10);
                        /*
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
                                    opf.resetCount();
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
                            resetCount();
                        }
                        */
                    }
                }
        );

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        for(Multicounter m: CounterListActivity.multicounterList)
        {
            if(m.getName().equals(mcName))
            {
                for(Counter c: m.counters)
                {
                    if(c.getLabel().equals(cName))
                    {
                        c.setCount(getCount());
                        break;
                    }
                }
            }
        }

        //save multicounter list
        SharedPreferences sharedPref = getActivity().getSharedPreferences("MultiCounterList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonMC = gson.toJson(CounterListActivity.multicounterList);
        editor.putString("MultiCounterList", jsonMC);
        editor.commit();
    }

    public int getCount()
    {
        return Integer.parseInt(counterCount.getText().toString());
    }
    public void setCount(int num)
    {
        counterCount.setText(Integer.toString(num));
    }

    public void addCount()
    {
        int num = Integer.valueOf(counterCount.getText().toString());
        num++;
        counterCount.setText(Integer.toString(num));
    }
    public void subCount()
    {
        int num = Integer.valueOf(counterCount.getText().toString());
        num--;
        counterCount.setText(Integer.toString(num));
    }
    public void resetCount()
    {
        setCount(0);
    }

    public void setLabel(String name)
    {
        counterName.setText(name);
    }

}
