package com.example.jarvis;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private List<String> allTasks;
    private LinearLayout taskLayout;
    private TextView welcomeText;
    private MaterialButton dontPressMeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        taskLayout = (LinearLayout) view.findViewById(R.id.tasks_layout);
        welcomeText = (TextView) view.findViewById(R.id.weclomeText);
        dontPressMeButton = (MaterialButton) view.findViewById(R.id.dontPressMe);

        dontPressMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DontPressMeActivity.class);
                startActivity(intent);
            }
        });

        setUpWelcomeText();
        loadAllTasks();
        return view;
    }

    public void loadAllTasks(){
        Map<String, List<String>> todoTasks;
        try{
            FileInputStream fis = getContext().openFileInput( Constants.DATABASE);
            ObjectInputStream oi = new ObjectInputStream( fis);
            todoTasks = (Map<String, List<String>>) oi.readObject();
            fis.close();
            oi.close();
            allTasks = todoTasks.get(Constants.ALL_TASKS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(allTasks != null)
            printTasks();
    }

    private void printTasks(){
        taskLayout.removeAllViews();
        List<String> currentTasks = allTasks;
        for( String currentText : currentTasks){
            TextView task = new TextView( getContext());
            setTextViewParams(task, currentText);
            taskLayout.addView( task);
        }
    }

    public void setTextViewParams(TextView task , String currentText){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 7, 0, 7);
        task.setLayoutParams( params);
        task.setText(currentText);
        task.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        task.setTypeface(null, Typeface.BOLD);
    }

    private void setUpWelcomeText(){
        welcomeText.setText("Welcome...");
    }
}
