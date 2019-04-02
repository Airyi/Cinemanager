package net.lzzy.cinemanager.fragments;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;
import net.lzzy.cinemanager.models.CinemaFactory;
import net.lzzy.cinemanager.models.Order;
import net.lzzy.cinemanager.utils.AppUtils;
import net.lzzy.simpledatepicker.CustomDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author lzzy_gxy
 * @date 2019/3/27
 * Description:
 */
public class OrderAddFragment extends BaseFragment {
    private OnOrderCreatedListener onOrderCreatedListener;
    private OnFragmentInteractionListener listener;
    private EditText edtMovie;
    private RelativeLayout relativeLayout;
    private TextView tvTime;
    private ImageView imgQRcode;
    public CustomDatePicker datePicker;
    private List<Cinema> cinemas;
    private EditText edtPrice;
    private Spinner spCinema;


    @Override
    protected void populate() {

        edtMovie = find(R.id.order_dialog_edt_movie);
        relativeLayout = find(R.id.order_dialog_layoutTime);
        tvTime = find(R.id.order_dialog_movieTime);
        edtPrice = find(R.id.order_dialog_edt_price);
        imgQRcode = find(R.id.order_dialog_Iv);
        spCinema = find(R.id.order_dialog_btn_spinnerArea);
        initDatePicker();
        relativeLayout.setOnClickListener(v -> datePicker.show(tvTime.getText().toString()));
        find(R.id.order_dialog_btn_ok).setOnClickListener(v -> {
            Order order = new Order();
            cinemas = CinemaFactory.getInstance().get();
            spCinema.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, cinemas));
            String movie = edtMovie.getText().toString();
            String time = tvTime.getText().toString();
            if (TextUtils.isEmpty(movie) || TextUtils.isEmpty(time)) {
                Toast.makeText(getActivity(), "信息不全", Toast.LENGTH_SHORT).show();
                return;
            }
            Float price;
            try {
                price = Float.parseFloat(edtPrice.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "数字格式不正确", Toast.LENGTH_SHORT).show();
                return;
            }
            Cinema cinema = cinemas.get(spCinema.getSelectedItemPosition());
            order.setMovie(movie);
            order.setMovieTime(time);
            order.setPrice(price);
            order.setCinemaId(cinema.getId());
            onOrderCreatedListener.saveOrder(order);
            edtMovie.setText("");
            edtPrice.setText("");
        });
        find(R.id.order_dialog_btn_er).setOnClickListener(v -> {
            String name = edtMovie.getText().toString();
            String price = edtPrice.getText().toString();
            String location = spCinema.getSelectedItem().toString();
            String time = tvTime.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(price)) {
                Toast.makeText(getActivity(), "信息不全", Toast.LENGTH_SHORT).show();
                return;
            }
            String content = "[" + name + "]" + time + "\n" + location + "票价" + price + "元";
            imgQRcode.setImageBitmap(AppUtils.createQRCodeBitmap(content, 200, 200));
        });
        imgQRcode.setOnLongClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) imgQRcode.getDrawable()).getBitmap();
            Toast.makeText(getActivity(), AppUtils.readQRCode(bitmap), Toast.LENGTH_SHORT).show();
            return true;
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        listener.hideSearch();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.order_add_fragment;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            onOrderCreatedListener= (OnOrderCreatedListener) context;
            listener= (OnFragmentInteractionListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"必须实现OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        listener=null;
        onOrderCreatedListener=null;

    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        tvTime.setText(now);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        String end = sdf.format(calendar.getTime());
        datePicker = new CustomDatePicker(getActivity(), s -> tvTime.setText(s), now, end);
        datePicker.showSpecificTime(true);
        datePicker.setIsLoop(true);
    }

    public interface OnOrderCreatedListener{
        void cancelAddOrder();

        void saveOrder(Order order);
    }
}
