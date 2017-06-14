package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.entity.FamilyDoctor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class FamilyDoctorView extends LinearLayout {

    private ListView mDoctorsLv;
    private DoctorAdapter mDoctorAdapter;
    private List<FamilyDoctor> mDoctors;

    public FamilyDoctorView(Context context) {
        super(context);
        initView(context);
    }

    public FamilyDoctorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FamilyDoctorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        addDoctor();
        LayoutInflater.from(context).inflate(R.layout.fragment_family_doctors, this, true);
        mDoctorsLv = (ListView) findViewById(R.id.fragment_family_doctor_lv);
        mDoctorAdapter = new DoctorAdapter(context);
        mDoctorsLv.setAdapter(mDoctorAdapter);
        mDoctorAdapter.notifyDataSetChanged();
    }

    class DoctorAdapter extends BaseAdapter {

        private Context mmContext;

        public DoctorAdapter(Context context) {
            mmContext = context;
        }

        @Override
        public int getCount() {
            return mDoctors == null ? 0 : mDoctors.size();
        }

        @Override
        public Object getItem(int position) {
            return mDoctors == null ? null : mDoctors.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mmContext).inflate(R.layout.item_family_doctor, null);
                holder.mmDoctorNameTv = (TextView) convertView.findViewById(R.id.item_family_doctor_name_tv);
                holder.mmDoctorTitleTv = (TextView) convertView.findViewById(R.id.item_family_doctor_title_tv);
                holder.mmDoctorHospitalTv = (TextView) convertView.findViewById(R.id.item_family_doctor_hospital_tv);
                holder.mmDoctorDepartmentTv = (TextView) convertView.findViewById(R.id.item_family_doctor_department_tv);
                holder.mmDoctorInfoTv = (TextView) convertView.findViewById(R.id.item_family_doctor_info_tv);
                holder.mmDoctorHeadIv = (ImageView) convertView.findViewById(R.id.item_family_doctor_head_iv);
                holder.mmGoDetailIv = (ImageView) convertView.findViewById(R.id.item_family_doctor_go_detail_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FamilyDoctor doctor = (FamilyDoctor) getItem(position);
            if (doctor != null) {
                holder.mmDoctorNameTv.setText(doctor.getDoctorName());
                holder.mmDoctorTitleTv.setText(doctor.getDoctorTitle());
                holder.mmDoctorHospitalTv.setText(doctor.getDoctorHospital());
                holder.mmDoctorDepartmentTv.setText(doctor.getDoctorDepartment());
                holder.mmDoctorInfoTv.setText(doctor.getDoctorInfo());
            }
            return convertView;
        }
    }

    class ViewHolder {
        TextView mmDoctorNameTv;
        TextView mmDoctorTitleTv;
        TextView mmDoctorHospitalTv;
        TextView mmDoctorDepartmentTv;
        TextView mmDoctorInfoTv;
        ImageView mmDoctorHeadIv;
        ImageView mmGoDetailIv;
    }

    public void setDoctors(List<FamilyDoctor> doctors) {
        mDoctors = doctors;
        mDoctorAdapter.notifyDataSetChanged();
    }

    private void addDoctor(){
        mDoctors = new ArrayList<>();

        FamilyDoctor doctor1 = new FamilyDoctor();
        doctor1.setDoctorName("胡八一");
        doctor1.setDoctorTitle("主任医师");
        doctor1.setDoctorHospital("武警二院");
        doctor1.setDoctorDepartment("肿瘤生物中心");
        doctor1.setDoctorInfo("擅长：新疗法,最新技术,国家专利,包治包好,一针见效,无副作用,假一赔十,良心疗法,欢迎垂询！");

        FamilyDoctor doctor2 = new FamilyDoctor();
        doctor2.setDoctorName("胡八一");
        doctor2.setDoctorTitle("主任医师");
        doctor2.setDoctorHospital("武警二院");
        doctor2.setDoctorDepartment("肿瘤生物中心");
        doctor2.setDoctorInfo("擅长：新疗法,最新技术,国家专利");

        FamilyDoctor doctor3 = new FamilyDoctor();
        doctor3.setDoctorName("胡八一");
        doctor3.setDoctorTitle("主任医师");
        doctor3.setDoctorHospital("武警二院");
        doctor3.setDoctorDepartment("肿瘤生物中心");
        doctor3.setDoctorInfo("擅长：新疗法,最新技术,国家专利");

        FamilyDoctor doctor4 = new FamilyDoctor();
        doctor4.setDoctorName("胡八一");
        doctor4.setDoctorTitle("主任医师");
        doctor4.setDoctorHospital("武警二院");
        doctor4.setDoctorDepartment("肿瘤生物中心");
        doctor4.setDoctorInfo("擅长：新疗法,最新技术,国家专利");

        FamilyDoctor doctor5 = new FamilyDoctor();
        doctor5.setDoctorName("胡八一");
        doctor5.setDoctorTitle("主任医师");
        doctor5.setDoctorHospital("武警二院");
        doctor5.setDoctorDepartment("肿瘤生物中心");
        doctor5.setDoctorInfo("新疗法,最新技术,国家专利");

        mDoctors.add(doctor1);
        mDoctors.add(doctor2);
        mDoctors.add(doctor3);
        mDoctors.add(doctor4);
        mDoctors.add(doctor5);
    }
}
