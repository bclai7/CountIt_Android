package com.takezeroapps.countit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.takezeroapps.countit.R;

/**
 * Created by scoob on 1/7/2017.
 */
public class OperatorFragment extends Fragment{

    private static TextView cnt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.counter_fragment, container, false);
        cnt = (TextView) view.findViewById(R.id.count);
        return view;
    }

    public int getCount() //this function is called by the main activity to get the current count
    {
        return Integer.valueOf(cnt.getText().toString());
    }
    public void changeCount(int num) //This function is used by the main activity to change the count when the button is held
    {
        Operations op = new Operations(); //create object from Operations.java class
        int size = op.getSize(num); //use method to get size of text
        cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size); //sets the text size in sp units depending on the size of the number
        String countstring = Integer.toString(num); //convert the input number to a string
        cnt.setText(countstring); //sets text to specified string
    }

    public void addCount()
    {
        int num = Integer.valueOf(cnt.getText().toString());
        num++;
        Operations op = new Operations(); //create object from Operations.java class
        int size = op.getSize(num); //use method to get size of text
        cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size); //sets the text size in sp units depending on the size of the number
        String countstring = Integer.toString(num);
        cnt.setText(countstring);
    }
    public void subCount()
    {
        int num = Integer.valueOf(cnt.getText().toString());
        num--;
        Operations op = new Operations(); //create object from Operations.java class
        int size = op.getSize(num); //use method to get size of text
        cnt.setTextSize(TypedValue.COMPLEX_UNIT_SP, size); //sets the text size in sp units depending on the size of the number
        String countstring = Integer.toString(num);
        cnt.setText(countstring);
    }
    public void resetCount()
    {
        changeCount(0);
    }

}
