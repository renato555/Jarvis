package com.example.jarvis;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class PongFragment extends Fragment {
    private OnSwipeTouchListener swipeListener;

    public PongFragment(OnSwipeTouchListener swipeListener){
        this.swipeListener = swipeListener;
    }

    private Button host;
    private Button join;
    private Button players2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pong, container, false);

        //load views
        loadViews( view);

        //setUp button listeners
        setUpListers();
        return view;
    }

    private void loadViews( View view){
        host = ( Button) view.findViewById( R.id.buttonHost);
        join = ( Button) view.findViewById( R.id.buttonJoin);
        players2 = ( Button) view.findViewById( R.id.button2Players);
    }

    private void setUpListers(){
        host.setOnClickListener( (View v) -> {

        });

        join.setOnClickListener( (View v) -> {

        });

        players2.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getContext(), PongActivity.class);
            intent.putExtra( "mode", 3);
            startActivity( intent);
        });
    }

}
