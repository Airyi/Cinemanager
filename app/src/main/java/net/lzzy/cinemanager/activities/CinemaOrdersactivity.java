package net.lzzy.cinemanager.activities;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.fragments.FragmentCinemaOrders;

import static net.lzzy.cinemanager.activities.MainActivity.EXTRA_CINEMA_ID;

public class CinemaOrdersactivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_cinema_ordersactivity);
        String cinemaId=getIntent().getStringExtra(EXTRA_CINEMA_ID);
        FragmentManager manager=getSupportFragmentManager();
        Fragment fragment=manager.findFragmentById(R.id.activity_fragment_order_content);
        if (fragment==null){
            fragment= FragmentCinemaOrders.newInstance(cinemaId);
            manager.beginTransaction().add(R.id.activity_fragment_order_content,fragment).commit();
        }
    }
}
