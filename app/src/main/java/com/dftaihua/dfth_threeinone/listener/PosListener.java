package com.dftaihua.dfth_threeinone.listener;


import com.dfth.sdk.file.ecg.ECGResultFile;

/**
 * Created by Administrator on 2015-01-07.
 */
public interface PosListener {
    public void posChange(int pos, ECGResultFile file, int len, int hr);
}
