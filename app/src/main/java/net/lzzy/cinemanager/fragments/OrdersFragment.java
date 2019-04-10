package net.lzzy.cinemanager.fragments;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.models.OrderFactory;
import net.lzzy.cinemanager.utils.AppUtils;
import net.lzzy.sqllib.GenericAdapter;
import net.lzzy.sqllib.ViewHolder;

import java.util.List;

/**
 *
 * @author lzzy_gxy
 * @date 2019/3/26
 * Description:
 */
public class OrdersFragment extends BaseFragment {
    public static final String ORDER = "order";
    private Order order;
    private List<Order> orders;
    private ListView lv;
    private GenericAdapter<Order>adapter;
    private OrderFactory factory=OrderFactory.getInstance();
    float touchX1;
    float touchX2;
    private final float MNT_DISTANCE = 100;
    boolean isDelete = false;

    public static OrdersFragment newInstance(Order order) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putParcelable(ORDER,order);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Order order = getArguments().getParcelable(ORDER);
            this.order = order;
        }

    }
    @Override
    protected void populate() {
        lv=find(R.id.activity_cinema_content_lv);
        View empty=find(R.id.item_zero);
        lv.setEmptyView(empty);
        orders=factory.get();
        adapter = new GenericAdapter<Order>(getActivity(), R.layout.cinema_item, orders) {
            @Override
            public void populate(ViewHolder holder, Order order) {
                String location = CinemaFactory.getInstance()
                        .getById(order.getCinemaId().toString()).toString();
                holder.setTextView(R.id.activity_cinema_item_name, order.getMovie())
                        .setTextView(R.id.activity_cinema_item_area, location);
                Button btn = holder.getView(R.id.activity_cinema_btn);
                btn.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                        .setTitle("删除确认")
                        .setMessage("要删除订单吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确认", (dialog, which) ->
                                adapter.remove(order)).show());
                int visible=isDelete?View.VISIBLE:View.GONE;
                btn.setVisibility(visible);
                holder.getConvertView().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        slideToDelete(event, order, btn);
                        return true;
                    }

                    private void slideToDelete(MotionEvent event, Order order, Button btn) {

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                touchX1 = event.getX();
                                break;
                            case MotionEvent.ACTION_UP:
                                touchX2 = event.getX();
                                if (touchX1 - touchX2 > MNT_DISTANCE) {
                                    if (!isDelete) {
                                        btn.setVisibility(View.VISIBLE);
                                        isDelete = true;
                                    }
                                } else {
                                    if (btn.isShown()) {
                                        btn.setVisibility(View.GONE);
                                        isDelete = false;
                                    } else {
                                        clickOrder(order);
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
            }

            @Override
            public boolean persistInsert(Order order) {
                return factory.addOrder(order);
            }

            @Override
            public boolean persistDelete(Order order) {
                return factory.delete(order);
            }

        };
        lv.setAdapter(adapter);
        if (order!=null){
            save(order);
        }
    }

    private void clickOrder(Order order) {
        Cinema cinema= CinemaFactory.getInstance().getById(order.getCinemaId().toString());
        String content = "[" + order.getMovie() + "]" + order.getMovieTime() + "\n" + cinema.toString() + "票价" + order.getPrice() + "元";
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_qrcode,null);
        ImageView img=view.findViewById(R.id.dialog_qrcode_img);
        img.setImageBitmap((AppUtils.createQRCodeBitmap(content, 300, 300)));
        new AlertDialog.Builder(getActivity())
                .setView(view).show();
    }

    public void save (Order order){
        adapter.add(order);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragent_orders;
    }

    @Override
    public void search(String kw) {
        orders.clear();
        if (TextUtils.isEmpty(kw)){
            orders.addAll(factory.get());
        }else {
            orders.addAll(factory.searchOrders(kw));
        }
        adapter.notifyDataSetChanged();

    }
}
