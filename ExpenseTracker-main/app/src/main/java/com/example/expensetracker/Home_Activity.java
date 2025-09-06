package com.example.expensetracker;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class Home_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private DrawerLayout drawerLayout;

    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Expense Manager");
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
        frameLayout = findViewById(R.id.main_frame);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_View);
        navigationView.setNavigationItemSelectedListener(this);

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();

        setFragment(dashboardFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isFinishing() || isDestroyed()) return false;

            int itemId=item.getItemId();
            if(itemId==R.id.dashboard){
                setFragment(dashboardFragment);
                bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
                return true;
            }else if(itemId==R.id.income){
                setFragment(incomeFragment);
                bottomNavigationView.setItemBackgroundResource(R.color.income_color);
                return true;
            } else if (itemId==R.id.expense) {
                setFragment(expenseFragment);
                bottomNavigationView.setItemBackgroundResource(R.color.expanse_color);
                return true;
            }else{
                return false;
            }


//            switch (item.getItemId()) {
//                case R.id.dashboard:
//                    setFragment(dashboardFragment);
//                    bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
//                    return true;
//                case R.id.income:
//                    setFragment(incomeFragment);
//                    bottomNavigationView.setItemBackgroundResource(R.color.income_color);
//                    return true;
//                case R.id.expense:
//                    setFragment(expenseFragment);
//                    bottomNavigationView.setItemBackgroundResource(R.color.expanse_color);
//                    return true;
//                default:
//                    return false;
//            }
        });
    }

    private void setFragment(Fragment fragment) {
        if (isFinishing() || isDestroyed()) return;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId) {
        if (isFinishing() || isDestroyed()) return;

        Fragment fragment = null;

        if(itemId==R.id.dashboard){
            fragment=dashboardFragment;
            bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
            return;
        }else if(itemId==R.id.income){
            fragment=incomeFragment;
            bottomNavigationView.setItemBackgroundResource(R.color.income_color);
            return;
        } else if (itemId==R.id.expense) {
            fragment=expenseFragment;
            bottomNavigationView.setItemBackgroundResource(R.color.expanse_color);
            return;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commitAllowingStateLoss();
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }
}
