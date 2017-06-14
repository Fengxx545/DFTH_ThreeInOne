package com.dftaihua.dfth_threeinone.manager;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.file.ECGFileUploadManager;
import com.dfth.sdk.listener.ECGFileUploadListener;
import com.dfth.sdk.model.ecg.ECGResult;
import com.dfth.sdk.upload.ECGFileUploadTask;

public class NewUploadManager {
    private static NewUploadManager instance;
    private NewUploadManager() {
    }

    public static NewUploadManager getInstance() {
        if (instance == null) {
            instance = new NewUploadManager();
        }
        return instance;
    }
    public String getUploadStatus(ECGFileUploadTask task){
        if(task.getResult().getPost() >= ECGResult.UPLOAD_RESULT_SUCCESS){
            return ThreeInOneApplication.getStringRes(R.string.upload_complete);
        }
        if(task != null){
            if(!task.isRunner()){
                return ThreeInOneApplication.getStringRes(R.string.upload_wait);
            }else{
                return task.getCurrentProgress()+"%";
            }
        }
        return ThreeInOneApplication.getStringRes(R.string.upload_no);
    }
    public ECGFileUploadTask getStartTask(ECGResult result, ECGFileUploadListener listener){
        return ECGFileUploadManager.getManager().getUploadTask(result,listener);
    }

    public ECGFileUploadTask addOrRemoveTask(ECGResult result, ECGFileUploadListener listener){
        if(result.getPost() >= ECGResult.UPLOAD_RESULT_SUCCESS){
            return null;
        }
        ECGFileUploadTask task = ECGFileUploadManager.getManager().getUploadTask(result,listener);
        if(task == null){
            DfthSDKManager.getManager().getDfthService().uploadECGData(result,listener).asyncExecute(null);
        }
        return getStartTask(result,listener);
    }
}