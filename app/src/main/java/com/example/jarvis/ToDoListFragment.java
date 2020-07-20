package com.example.jarvis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ToDoListFragment extends Fragment {

    private Spinner dropdown;
    private Button addList;
    private Button deleteList;
    private FloatingActionButton addTask;

    private static String currentList = "All tasks";
    private static Map<String, List<String>> todoTasks; //< listaName, tasks>
    private LinearLayout taskLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        //link views with attributes
        dropdown = (Spinner) view.findViewById( R.id.spinner);
        addList = (Button) view.findViewById( R.id.addList);
        deleteList = (Button) view.findViewById( R.id.deleteList);
        addTask = (FloatingActionButton) view.findViewById( R.id.floatingActionButton);
        taskLayout = (LinearLayout) view.findViewById( R.id.tasks_layout);

        //setup dropdown menu
        todoTasks = new LinkedHashMap<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>( container.getContext(), android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter( adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                currentList = adapter.getItem( pos);
                printTasks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //privremeno dok ne napravimo spremanje------------
        todoTasks.put( "All tasks", new LinkedList<>());
        adapter.add( "All tasks");
        //-------------------------------------------------


        //setup buttons
        setUpButtons();

        printTasks();
        return view;
    }

    private void setUpButtons(){
        deleteList.setOnClickListener(( View v) -> {
            if( !currentList.equals( "All tasks")){
               deleteList();
            }else{
                Toast.makeText( getContext(), "You can't delete this list.", Toast.LENGTH_SHORT).show();
            }
        });

        addList.setOnClickListener(( View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
            builder.setTitle( "Enter list name.");

            EditText input = new EditText( getContext());
            builder.setView( input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String inputString = input.getText().toString();
                if( !inputString.isEmpty()){
                    addList( inputString);
                }
            });

            builder.setNegativeButton("Cancel", ( dialog, which) -> {
                dialog.cancel();
            });

            builder.show();
        });


        addTask.setOnClickListener(( View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext());
            builder.setTitle( "Enter task/tasks.");

            EditText input = new EditText( getContext());
            builder.setView( input);

            builder.setPositiveButton("Add", (dialog, which) -> {
                String task = input.getText().toString();
                if( !task.isEmpty()){
                    for( String t : task.split( "\n"))
                        todoTasks.get( currentList).add( t);
                    if( !currentList.equals( "All tasks")){
                        for( String t : task.split( "\n"))
                            todoTasks.get( "All tasks").add( t);
                    }
                    //TODO mozda dodati da se samo updejta a da se ispisuje ispocetka lista
                    printTasks();
                }
            });

            builder.setNegativeButton("Cancel", ( dialog, which) -> {
                dialog.cancel();
            });

            builder.show();
        });

    }

    private void addList( String list){
        if( !todoTasks.containsKey( list)){
            ((ArrayAdapter) dropdown.getAdapter()).add( list);
            todoTasks.put( list, new LinkedList<>());
            currentList = list;
            printTasks();

            dropdown.setSelection( dropdown.getCount() - 1); //show last added on dropdown
            Toast.makeText( getContext(), "added " + list, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteList(){
        ((ArrayAdapter) dropdown.getAdapter()).remove( currentList);
        todoTasks.remove( currentList);
        String deletedList = currentList;
        currentList = "All tasks";
        printTasks();

        dropdown.setSelection( 0); //show All tasks on dropdown
        Toast.makeText( getContext(), "deleted " + deletedList, Toast.LENGTH_SHORT).show();
    }

    private void printTasks(){
        taskLayout.removeAllViews();
        List<String> currentTasks = todoTasks.get( currentList);
        for( String task : currentTasks){
            CheckBox check = new CheckBox( getContext());
            check.setText( task);
            //TODO dodati animaciju
            //deletes task from task layout and deletes strings in database
            check.setOnClickListener((View v) -> {
                taskLayout.removeView( v);
                String taskText = ((CheckBox) v).getText().toString();
                //if true find if another list has that tasks. If so, delete it
                //else remove task from All tasks and current list
                if( currentList.equals( "All tasks")){
                    todoTasks.get( "All tasks").remove( taskText);
                    for( Map.Entry< String, List<String>> entry : todoTasks.entrySet() ){
                        String key = entry.getKey(); List<String> list = entry.getValue();
                        if( !key.equals( "All tasks") && list.contains( taskText)){
                            list.remove( taskText);
                        }
                    }
                }else{
                    todoTasks.get( "All tasks").remove( taskText);
                    todoTasks.get( currentList).remove( taskText);
                }
            });
            taskLayout.addView( check);
        }
    }
}
