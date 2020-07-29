package com.example.jarvis;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.icu.text.SymbolTable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ColleagueFragment extends Fragment {

    private LinearLayout colleaguesLayout;
    private Map<String, String> colleagues; // Map< ID, nickname>

    private OnSwipeTouchListener swipeListener;

    public ColleagueFragment( OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_colleague, container, false);
        colleaguesLayout = (LinearLayout) view.findViewById(R.id.colleagues_layout);
        setUpData();
        refreshColleagues();

        FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.addColleagueButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        System.out.println(colleagues);
        return view;
    }

    private void refreshColleagues() {
        colleaguesLayout.removeAllViews();
        if (colleagues == null)
            return;
        for (Map.Entry<String, String> entry : colleagues.entrySet()) {
            LinearLayout singleColleagueLayout = new LinearLayout(getContext());
            setupSingleColleagueLayout(singleColleagueLayout, entry);
            colleaguesLayout.addView(singleColleagueLayout);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Destroy");
    }

    private void setupSingleColleagueLayout(LinearLayout singleColleagueLayout, Map.Entry<String, String> entry) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15, 20, 15, 0);
        singleColleagueLayout.setLayoutParams(params);
        singleColleagueLayout.setBackgroundResource(R.drawable.colleague_frame);
        TextView nicknameView = new TextView(getContext());
        nicknameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Press and hold to delete...", Toast.LENGTH_SHORT).show();
            }
        });
        nicknameView.setOnLongClickListener((View v) -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Delete Colleague?");

            builder.setPositiveButton("Delete", (dialog, which) -> {
                colleagues.remove(entry.getKey());
                updateEverything();
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.cancel();
            });

            builder.show();

            return false;
        });
        //Check for duplicates
        if(isNotAlone(entry.getValue()))
            nicknameView.setText(entry.getValue() + " (" + entry.getKey() +")");
        else nicknameView.setText(entry.getValue());

        setupColleagueTextView(nicknameView);
        singleColleagueLayout.addView(nicknameView);
    }

    private void setupColleagueTextView(TextView textView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(35, 0, 0, 0);
        textView.setLayoutParams(params);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 100);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(null, Typeface.BOLD);
    }

    private void openDialog() {
        AddColleagueDialogFragment dialog = new AddColleagueDialogFragment();
        dialog.show(getFragmentManager(), "Add colleague dialog");
        dialog.setDialogResult(new AddColleagueDialogFragment.AddColleagueDialogListener() {
            @Override
            public void applyTexts(String ID, String nickname) {
                if(colleagues.putIfAbsent(ID, nickname) != null)
                    Toast.makeText(getContext(),"Colleague with the same ID already exists", Toast.LENGTH_SHORT).show();
                updateEverything();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        writeData();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        writeData();
    }

    private void writeData() {
        try {
            FileOutputStream fos = getContext().openFileOutput(Constants.COLLEAGUES_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(colleagues);
            o.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpData() {

        if (colleagues == null)
            colleagues = new HashMap<>();

        try {
            FileInputStream fis = getContext().openFileInput(Constants.COLLEAGUES_FILE);
            ObjectInputStream oi = new ObjectInputStream(fis);
            colleagues = (Map<String, String>) oi.readObject();
            fis.close();
            oi.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        refreshColleagues();
    }

    public void updateEverything() {
        writeData();
        setUpData();
        refreshColleagues();
    }

    private boolean isNotAlone(String nickname){
        short count = 0;
        for(String temp: colleagues.values()){
            if(temp.equals(nickname))
                count++;
            if(count == 2)
                return true;
        }
        return false;
    }
}
