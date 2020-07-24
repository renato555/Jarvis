package com.example.jarvis;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;


public class AddColleagueDialogFragment extends AppCompatDialogFragment {

    private EditText colleagueIdText;

    private EditText colleagueNicknameText;
    private AddColleagueDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.add_colleague_layout, null);

        builder.setView(view).setTitle(R.string.add_colleague)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String ID = colleagueIdText.getText().toString();
                        String nickname = colleagueNicknameText.getText().toString();
                        if (listener != null)
                            listener.applyTexts(ID, nickname);
                    }
                });

        colleagueIdText = (EditText) view.findViewById(R.id.colleague_id);
        colleagueNicknameText = (EditText) view.findViewById(R.id.colleague_nickname);

        return builder.create();
    }

    public void setDialogResult(AddColleagueDialogListener result) {
        listener = result;
    }

    public interface AddColleagueDialogListener {
        void applyTexts(String ID, String nickname);
    }

}