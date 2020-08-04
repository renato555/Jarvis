package com.example.jarvis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class PongFragment extends Fragment {
    private OnSwipeTouchListener swipeListener;

    public PongFragment(OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pong, container, false);

        return view;
    }
}
