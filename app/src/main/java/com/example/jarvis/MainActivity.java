/*
NAVIGATION BAR library
MIT License
Copyright (c) 2019 Ismael Di Vita
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.*/

package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace( R.id.container, fragment).commit();

        chipNavigationBar = findViewById(R.id.chipNavigation);
        setUpNavigationBar();

    }


    private void setUpNavigationBar(){
        //set starting fragment to homeFragment
        chipNavigationBar.setItemSelected( R.id.home, true);

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch( i){
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.calendar:
                        fragment = new CalendarFragment();
                        break;
                    case R.id.colleague:
                        fragment = new ColleagueFragment();
                        break;
                    case R.id.toDo:
                        fragment = new ToDoListFragment();
                        break;
                    case R.id.weather:
                        fragment = new WeatherFragment();
                        break;
                }
                if( fragment != null){
                    getSupportFragmentManager().beginTransaction().replace( R.id.container, fragment).commit();
                }
            }
        });

        chipNavigationBar.showBadge( R.id.colleague, 1);
    }
}
