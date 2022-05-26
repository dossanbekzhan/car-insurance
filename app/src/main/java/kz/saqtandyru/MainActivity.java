package kz.saqtandyru;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import kz.saqtandyru.database.DBHelper;
import kz.saqtandyru.fragments.CarDataFragment;
import kz.saqtandyru.fragments.DocumentsFragment;
import kz.saqtandyru.fragments.NewInsuranceFragment;
import kz.saqtandyru.fragments.ReplacementsFragment;
import kz.saqtandyru.model.Car;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(getBaseContext());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CarDataFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CarDataFragment()).commit();
        } else if (id == R.id.nav_replacements) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReplacementsFragment()).commit();
        } else if (id == R.id.nav_documents) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DocumentsFragment()).commit();
        } else if (id == R.id.nav_new_insurance) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewInsuranceFragment()).commit();
        } else if (id == R.id.nav_share) {
            sendCarData();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendCarData() {
        Car mainCar = dbHelper.getMainCar();

        if (mainCar != null) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String carInfo = getResources().getString(R.string.data_to_send_format,
                    mainCar.getMarka(),
                    mainCar.getModel(),
                    mainCar.getRok_produkcji(),
                    mainCar.getPojemnosc(),
                    mainCar.getMoc()
            );
            sendIntent.putExtra(Intent.EXTRA_TEXT, carInfo);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        } else {
            Toast.makeText(this, getResources().getString(R.string.set_as_default_car_hint), Toast.LENGTH_LONG).show();
        }
    }
}
