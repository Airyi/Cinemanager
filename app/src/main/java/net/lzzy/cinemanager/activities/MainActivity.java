package net.lzzy.cinemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.TextView;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.fragments.BaseFragment;
import net.lzzy.cinemanager.fragments.CinemasFragment;
import net.lzzy.cinemanager.fragments.CinenmasAddFragment;
import net.lzzy.cinemanager.fragments.OnFragmentInteractionListener;
import net.lzzy.cinemanager.fragments.OrderAddFragment;
import net.lzzy.cinemanager.fragments.OrdersFragment;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.utils.ViewUtils;

/**
 * @author Administrator
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , OnFragmentInteractionListener,CinenmasAddFragment.OnCinemaCreatedListener, OrderAddFragment.OnOrderCreatedListener {
    private FragmentManager manager = getSupportFragmentManager();
    private TextView tvTitle;
    private View layoutMenu;
    private SearchView search;
    private SparseArray<String> titleArray = new SparseArray<>();
    private SparseArray<Fragment> fragmentArray=new SparseArray<>();
    public static final String EXTRA_CINEMA_ID="cinemaId";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setTitleMenu();
        search.setOnQueryTextListener(new ViewUtils.AbstractQueryHandler() {
            @Override
            public boolean handleQuery(String kw) {
                Fragment fragment=manager.findFragmentById(R.id.fragment_container);
                if (fragment!=null){
                    if (fragment instanceof  BaseFragment){
                        ((BaseFragment)fragment).search(kw);
                    }
                }
                return true;
            }
        });
    }

    private void setTitleMenu() {
        layoutMenu = findViewById(R.id.bar_menu);
        layoutMenu.setVisibility(View.GONE);
        titleArray.put(R.id.bar_add_cinema, "添加影院");
        titleArray.put(R.id.bar_see_cinema, "影院列表");
        titleArray.put(R.id.bar_add_order, "添加订单");
        titleArray.put(R.id.bar_order, "我的订单");


        tvTitle = findViewById(R.id.bar_title_tv_title);
        findViewById(R.id.bar_img_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visible = layoutMenu.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                layoutMenu.setVisibility(visible);
            }
        });
        tvTitle.setText("我的订单");
        search = findViewById(R.id.bar_searchView);
        findViewById(R.id.bar_order).setOnClickListener(this);
        findViewById(R.id.bar_add_cinema).setOnClickListener(this);
        findViewById(R.id.bar_add_order).setOnClickListener(this);
        findViewById(R.id.bar_see_cinema).setOnClickListener(this);
        findViewById(R.id.bar_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }

    @Override
    public void onClick(View v) {
        layoutMenu.setVisibility(View.GONE);
        tvTitle.setText(titleArray.get(v.getId()));
        FragmentTransaction transaction=manager.beginTransaction();
        Fragment fragment=fragmentArray.get(v.getId());
        if (fragment==null){
            fragment=createFragment(v.getId());
            fragmentArray.put(v.getId(),fragment);
            transaction.add(R.id.fragment_container,fragment);
        }
        for (Fragment f:manager.getFragments()){
            transaction.hide(f);
        }
        transaction.show(fragment).commit();
    }

    private Fragment createFragment(int id) {
        switch (id) {
            case R.id.bar_add_cinema:
                return new CinenmasAddFragment();
            case R.id.bar_see_cinema:
                return  new CinemasFragment();
            case R.id.bar_add_order:
                return new OrderAddFragment();
            case R.id.bar_order:
                return new OrdersFragment();
            default:
                break;
        }
        return null;
    }

    @Override
    public void hideSearch() {
        search.setVisibility(View.VISIBLE);
    }

    @Override
    public void cancelAddCinema() {
        Fragment addCinemaFragment=fragmentArray.get(R.id.bar_add_cinema);
        if (addCinemaFragment==null){
            return;
        }
        Fragment cinemasFragment=fragmentArray.get(R.id.bar_see_cinema);
        FragmentTransaction transaction = manager.beginTransaction();
        if (cinemasFragment==null){
            cinemasFragment=new CinemasFragment();
            fragmentArray.put(R.id.bar_see_cinema,cinemasFragment);
            transaction.add(R.id.fragment_container,cinemasFragment);
        }
        transaction.hide(addCinemaFragment).show(cinemasFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_see_cinema));
    }

    @Override
    public void saveCinema(Cinema cinema) {
        Fragment addCinemasFragment=fragmentArray.get(R.id.bar_add_cinema);
        if (addCinemasFragment==null){
            return;
        }
        Fragment cinemasFragment=fragmentArray.get(R.id.bar_see_cinema);
        FragmentTransaction transaction=manager.beginTransaction();
        if (cinemasFragment==null){
                //创建CinemasFragment同时要传Cinema对象进来
            cinemasFragment=new CinemasFragment(cinema);
            fragmentArray.put(R.id.bar_see_cinema,cinemasFragment);
            transaction.add(R.id.fragment_container,cinemasFragment);
        }else {
            ((CinemasFragment)cinemasFragment).save(cinema);
        }
        transaction.hide(addCinemasFragment).show(cinemasFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_see_cinema));
    }

    @Override
    public void cancelAddOrder() {
        Fragment addOrderFragment=fragmentArray.get(R.id.bar_add_order);
        if (addOrderFragment==null){
            return;
        }
        Fragment OrderFragment=fragmentArray.get(R.id.bar_order);
        FragmentTransaction transaction = manager.beginTransaction();
        if (OrderFragment==null){
            OrderFragment=new OrdersFragment();
            fragmentArray.put(R.id.bar_order,OrderFragment);
            transaction.add(R.id.fragment_container,OrderFragment);
        }
        transaction.hide(addOrderFragment).show(OrderFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_order));
    }

    @Override
    public void saveOrder(Order order) {
        Fragment addOrdersFragment=fragmentArray.get(R.id.bar_add_order);
        if (addOrdersFragment==null){
            return;
        }
        Fragment OrdersFragment=fragmentArray.get(R.id.bar_order);
        FragmentTransaction transaction=manager.beginTransaction();
        if (OrdersFragment==null){
            //创建CinemasFragment同时要传Cinema对象进来
            OrdersFragment=new OrdersFragment(order);
            fragmentArray.put(R.id.bar_order,OrdersFragment);
            transaction.add(R.id.fragment_container,OrdersFragment);
        }else {
            ((OrdersFragment)OrdersFragment).save(order);
        }
        transaction.hide(addOrdersFragment).show(OrdersFragment).commit();
        tvTitle.setText(titleArray.get(R.id.bar_see_cinema));
    }
}
