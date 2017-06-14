package com.dftaihua.dfth_threeinone.listener;


import com.dfth.sdk.file.ecg.ECGResultFile;

public interface WaveChangeListener {
        public void waveChange(int progress);
        public ECGResultFile getFile();
    }