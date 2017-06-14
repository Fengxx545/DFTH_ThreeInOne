package com.dftaihua.dfth_threeinone.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class FamilyDoctor implements Serializable{
    private String mDoctorId;            //医生ID
    private String mDoctorName;          //医生姓名
    private String mDoctorHospital;      //医生所属医院
    private String mDoctorDepartment;    //医生所属部门
    private String mDoctorTitle;         //医生职称
    private String mDoctorHeadPath;      //医生头像地址
    private String mDoctorInfo;          //医生简介

    public String getDoctorId() {
        return mDoctorId;
    }

    public void setDoctorId(String mDoctorId) {
        this.mDoctorId = mDoctorId;
    }

    public String getDoctorHospital() {
        return mDoctorHospital;
    }

    public void setDoctorHospital(String mDoctorHospital) {
        this.mDoctorHospital = mDoctorHospital;
    }

    public String getDoctorName() {
        return mDoctorName;
    }

    public void setDoctorName(String mDoctorName) {
        this.mDoctorName = mDoctorName;
    }

    public String getDoctorDepartment() {
        return mDoctorDepartment;
    }

    public void setDoctorDepartment(String mDoctorDepartment) {
        this.mDoctorDepartment = mDoctorDepartment;
    }

    public String getDoctorTitle() {
        return mDoctorTitle;
    }

    public void setDoctorTitle(String mDoctorTitle) {
        this.mDoctorTitle = mDoctorTitle;
    }

    public String getDoctorHeadPath() {
        return mDoctorHeadPath;
    }

    public void setDoctorHeadPath(String mDoctorHeadPath) {
        this.mDoctorHeadPath = mDoctorHeadPath;
    }

    public String getDoctorInfo() {
        return mDoctorInfo;
    }

    public void setDoctorInfo(String mDoctorInfo) {
        this.mDoctorInfo = mDoctorInfo;
    }
}
