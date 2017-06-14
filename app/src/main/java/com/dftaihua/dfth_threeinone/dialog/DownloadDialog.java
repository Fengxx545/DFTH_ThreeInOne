package com.dftaihua.dfth_threeinone.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.activity.EcgHistoryActivity;

/**
 * Created by Administrator on 2017/3/16 0016.
 */
public class DownloadDialog extends Dialog{

    private static DownloadDialog dialog = null;
    private ProgressBar mProgress;

    public DownloadDialog(Context context) {
        super(context,R.style.connect_dialog_style);
        initialize(context);
    }

    public static DownloadDialog getDialog(Context context){
        if(dialog == null){
            return new DownloadDialog(context);
        }
        return dialog;
    }

    private void initialize(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        mProgress = (ProgressBar) view.findViewById(R.id.dialog_download_progress);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        int width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = width * 4 / 5;
        setContentView(view, params);
    }

    public void setProgress(int progress){
        mProgress.setProgress(progress);
    }
}
