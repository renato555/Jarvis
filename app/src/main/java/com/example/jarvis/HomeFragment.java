package com.example.jarvis;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
    public Button dontPressMeButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ResourceType")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        taskLayout = (LinearLayout) view.findViewById(R.id.tasks_layout);
        welcomeText = (TextView) view.findViewById(R.id.weclomeText);

        dontPressMeButton = (Button) view.findViewById(R.id.dontPressMe);
        enableButtons();

        setUpWelcomeText();
        loadAllTasks();
        return view;
    }



    private void setUpWelcomeText() {
        welcomeText.setText("Welcome, " + Constants.NAME);
    }

    public void loadAllTasks() {
        Map<String, List<String>> todoTasks;
        try {
            FileInputStream fos = getContext().openFileInput(Constants.TODO_DATABASE_FILE);
            ObjectInputStream oi = new ObjectInputStream(fos);

            todoTasks = (Map<String, List<String>>) oi.readObject();
            fos.close();
            oi.close();
            allTasks = todoTasks.get(Constants.ALL_TASKS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (allTasks != null)
            printTasks();

    }

    private void printTasks(){
        taskLayout.removeAllViews();
        List<String> currentTasks = allTasks;
        for (String currentText : currentTasks) {
            TextView task = new TextView(getContext());
            setTextViewParams(task, currentText);
            taskLayout.addView(task);
        }
    }

    public void setTextViewParams (TextView task, String currentText){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(25, 7, 0, 7);
        task.setLayoutParams(params);
        task.setText(currentText);
        task.setTextSize(TypedValue.COMPLEX_UNIT_PX, 50);
        task.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onPause() {
        Log.i("Test", "Pause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
    }

    public void disableButtons(){
        dontPressMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
    }

    public void enableButtons(){
        dontPressMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DontPressMeActivity.class);
                startActivity(intent);
            }
        });
    }
}


