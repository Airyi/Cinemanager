package net.lzzy.cinemanager.fragments;


import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.style.cityjd.JDCityPicker;

import net.lzzy.cinemanager.R;
import net.lzzy.cinemanager.models.Cinema;

/**
 *
 * @author lzzy_gxy
 * @date 2019/3/27
 * Description:
 */
public class CinenmasAddFragment extends BaseFragment {
    private String city = "柳州市";
    private String province = "广西壮族自治区";
    private String area = "鱼峰区";
    private TextView tvArea;
    private EditText edtName;

    @Override
    protected void populate() {
        tvArea = find(R.id.activity_dialog_location);
        edtName = find(R.id.activity_dialog_edt_name);
        showDialog();
    }
    public void showDialog() {

        find(R.id.activity_cinema_content_layoutArea).setOnClickListener(v -> {
            JDCityPicker cityPicker = new JDCityPicker();
            cityPicker.init(getActivity());
            cityPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                @Override
                public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {

                    CinenmasAddFragment.this.province = province.getName();
                    CinenmasAddFragment.this.city = city.getName();
                    CinenmasAddFragment.this.area = district.getName();
                    String loc = province.getName() + city.getName() + district.getName();
                    tvArea.setText(loc);
                }
                @Override
                public void onCancel() {
                }
            });
            cityPicker.showCityPicker();
        });
        find(R.id.activity_dialog_save).setOnClickListener(v -> {
            Cinema cinema = new Cinema();
            String name = edtName.getText().toString();
            cinema.setCity(city);
            cinema.setName(name);
            cinema.setArea(area);
            cinema.setProvince(province);
            cinema.setLocation(tvArea.getText().toString());

        });
        find(R.id.activity_dialog_cancel).setOnClickListener(v -> {

        });
    }

    @Override
    public int getLayoutRes() {
        return R.layout.cinemas_add_fragment;
    }
}
