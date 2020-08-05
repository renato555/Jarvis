/*
NAVIGATION BAR library
MIT License
Copyright (c) 2019 Ismael Di Vita
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

package com.example.jarvis;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private static final int WRITE_PERMISSION = 1001;

    private ChipNavigationBar chipNavigationBar;
    private OnSwipeTouchListener swipeListener;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Resources resources;

    private ToDoListFragment toDoListFragment;
    private CalendarFragment calendarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resources = getResources();

        chipNavigationBar = findViewById(R.id.chipNavigation);
        setUpNavigationBar();
        swipeListener = new OnSwipeTouchListener( this, chipNavigationBar);

        askPermissions();
        calendarFragmentPressed();
        weatherFragmentPressed();
        toDoFragmentPressed();
        homeFragmentPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeListener.onTouch( null, event);
    }

    private void setUpNavigationBar(){
        //set starting fragment to homeFragment
        chipNavigationBar.setItemSelected( R.id.home, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {

            @Override
            public void onItemSelected(int i) {
                switch( i){
                    case R.id.home:
                        homeFragmentPressed();
                        break;

                    case R.id.calendar:
                        calendarFragmentPressed();
                        break;

                    case R.id.pong:
                        PongFragmentPressed();
                        break;

                    case R.id.toDo:
                        toDoFragmentPressed();
                        break;

                    case R.id.weather:
                        weatherFragmentPressed();
                        break;
                }
            }
        });
    }

    private void weatherFragmentPressed() {
        String tag = resources.getString(R.string.weather);
        if(fragmentManager.findFragmentByTag(tag) != null) {
            //if the fragment exists, show it.
            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag(tag))).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.container, new WeatherFragment( swipeListener), tag).commit();
        }
        checkHome();
        checkColleague();
        checkTodo();
        checkCalendar();
    }

    private void toDoFragmentPressed() {
        String tag = resources.getString(R.string.to_do);
        if(fragmentManager.findFragmentByTag(tag) != null) {
            //if the fragment exists, show it.
            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag(tag))).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.container,toDoListFragment = new ToDoListFragment( swipeListener), tag).commit();
        }
        checkHome();
        checkColleague();
        checkCalendar();
        checkWeather();
    }

    private void PongFragmentPressed() {
        String tag = resources.getString(R.string.pong);
        if(fragmentManager.findFragmentByTag(tag) != null) {
            //if the fragment exists, show it.
            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag(tag))).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.container, new PongFragment( swipeListener), tag).commit();
        }
        checkHome();
        checkCalendar();
        checkTodo();
        checkWeather();
    }

    public void homeFragmentPressed(){
        String tag = resources.getString(R.string.home);
        if(fragmentManager.findFragmentByTag(tag) != null) {
            //if the fragment exists, show it.
            ((HomeFragment) fragmentManager.findFragmentByTag(tag)).printTasks();
            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag(tag))).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            if(toDoListFragment != null && calendarFragment != null) {
                fragmentManager.beginTransaction().add(R.id.container, new HomeFragment(swipeListener, toDoListFragment, calendarFragment), tag).commit();
            }
        }
        checkCalendar();
        checkColleague();
        checkTodo();
        checkWeather();
    }

    public void calendarFragmentPressed(){
        String tag = resources.getString(R.string.calendar);
        if(fragmentManager.findFragmentByTag(tag) != null) {
            //if the fragment exists, show it
            fragmentManager.beginTransaction().show(Objects.requireNonNull(fragmentManager.findFragmentByTag(tag))).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.container, calendarFragment = new CalendarFragment( swipeListener), tag).commit();
        }
        checkHome();
        checkColleague();
        checkTodo();
        checkWeather();
    }

    public void checkHome(){
        if(fragmentManager.findFragmentByTag(resources.getString(R.string.home)) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag(resources.getString(R.string.home)))).commit();
        }
    }

    public void checkTodo(){
        if(fragmentManager.findFragmentByTag(resources.getString(R.string.to_do)) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag(resources.getString(R.string.to_do)))).commit();
            ((ToDoListFragment) fragmentManager.findFragmentByTag(resources.getString(R.string.to_do))).writeData();
        }
    }

    public void checkWeather(){
        if(fragmentManager.findFragmentByTag(resources.getString(R.string.weather)) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag(resources.getString(R.string.weather)))).commit();
        }
    }

    public void checkColleague(){
        if(fragmentManager.findFragmentByTag(resources.getString(R.string.pong)) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag(resources.getString(R.string.pong)))).commit();
        }
    }

    public void checkCalendar(){
        if(fragmentManager.findFragmentByTag(resources.getString(R.string.calendar)) != null){
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(Objects.requireNonNull(fragmentManager.findFragmentByTag(resources.getString(R.string.calendar)))).commit();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private void askPermissions() {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if( ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.zet:
                Intent intent = new Intent(getApplicationContext(), DontPressMeActivity.class);
                startActivity(intent);
                return true;
            case R.id.settings:
                return true;
            case R.id.logout:
                LoginActivity.logout( getApplicationContext(), this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
