package com.dftaihua.dfth_threeinone.activity;

import android.app.Dialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.EventNameMessage;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.dialog.DialogFactory;
import com.dftaihua.dfth_threeinone.manager.NetworkCheckReceiver;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dftaihua.dfth_threeinone.utils.DelayPerformMethod;
import com.dftaihua.dfth_threeinone.utils.DisplayUtils;
import com.dftaihua.dfth_threeinone.utils.SharePreferenceUtils;
import com.dftaihua.dfth_threeinone.utils.ToastUtils;
import com.dfth.pay.PayManager;
import com.dfth.pay.goods.DfthGoods;
import com.dfth.pay.listener.ClaimCallBack;
import com.dfth.pay.model.DfthClaimGoods;
import com.dfth.pay.network.RealPayService;
import com.dfth.pay.utils.DfthPayCode;
import com.dfth.sdk.Others.Utils.Logger.Logger;
import com.dfth.sdk.dispatch.DfthServiceCallBack;
import com.dfth.sdk.event.DfthMessageEvent;
import com.dfth.sdk.network.response.DfthServiceResult;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Administrator on 2017/1/23 0023.
 */
public class FreeServiceActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBackIv;
    private RealPayService mService;
    private String mUserId;
    private List<DfthClaimGoods> mGoods;
    private Dialog mLoadingDialog;

    @Override
    public View initView() {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_free_service, null);
        mBackIv = (ImageView) view.findViewById(R.id.activity_free_service_iv);
        int height = ThreeInOneApplication.getScreenHeight() - DisplayUtils.dip2px(this, 50);
        Button getBtn = new Button(this);
        getBtn.setMinWidth(DisplayUtils.dip2px(this, 120));
        getBtn.setId(R.id.activity_free_service_get_btn);
        getBtn.setTextColor(Color.BLACK);
        getBtn.setText(R.string.free_service_get);
        getBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        getBtn.setBackgroundResource(R.drawable.shape_free_service_back);
        getBtn.setOnClickListener(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0, 0);
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = DisplayUtils.dip2px(this, 40);
        params.topMargin = (int) (height * 0.80);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        view.addView(getBtn, params);

        TextView desTv = new TextView(this);
        desTv.setTextColor(Color.WHITE);
        desTv.setText(R.string.free_service_deadline);
        desTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.activity_free_service_get_btn);
        params.topMargin = DisplayUtils.dip2px(this, 10);
        params.leftMargin = DisplayUtils.dip2px(this, 40);
        params.rightMargin = DisplayUtils.dip2px(this, 40);
        view.addView(desTv, params);

        mService = (RealPayService) PayManager.getPayManager().getPayService();
        mUserId = UserManager.getInstance().getDefaultUser().getUserId();
        mLoadingDialog = DialogFactory.getLoadingDialog(this, ThreeInOneApplication.getStringRes(R.string.free_service_getting), "");
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_free_service_get_btn:
                if (!NetworkCheckReceiver.getNetwork()) {
                    ToastUtils.showShort(this, R.string.network_not_connect);
                    return;
                }
                mLoadingDialog.show();
                mService.getClaimGoods(mUserId).asyncExecute(new DfthServiceCallBack<List<DfthClaimGoods>>() {
                    @Override
                    public void onResponse(DfthServiceResult<List<DfthClaimGoods>> dfthServiceResult) {
                        mGoods = dfthServiceResult.mData;
                        if (mGoods != null) {
//                            for (int i = 0; i < mGoods.size(); i++) {
//                                getGoods(mGoods.get(i));
//                            }
                            if (mGoods.get(0).isExists == 0) {
                                getGoods(mGoods.get(0));
                            } else {
                                getServiceSuccess();
                            }
                        } else {
                            ToastUtils.showShort(FreeServiceActivity.this, R.string.free_service_not_have);
                        }
                    }
                });
                break;
        }
    }

    public void getGoods(DfthClaimGoods goods) {
        PayManager.getPayManager().claim(this, goods, mUserId, new ClaimCallBack() {
            @Override
            public void onResponse(DfthClaimGoods goods, int code, String errorMessage) {
                if (code == DfthPayCode.CLAIM_GOODS_SUCCESS) {
                    getServiceSuccess();
                } else {
                    ToastUtils.showShort(FreeServiceActivity.this, errorMessage);
                }
            }
        });
    }

    private void getServiceSuccess() {
        SharePreferenceUtils.put(FreeServiceActivity.this, SharePreferenceConstant.FREE_SERVICE_STATUS, true);
        EventBus.getDefault().post(DfthMessageEvent.create(EventNameMessage.FREE_SERVICE_GET_SUCCESS));
        mLoadingDialog.dismiss();
        Dialog dialog = DialogFactory.getFreeServiceDialog(FreeServiceActivity.this);
        dialog.show();
        DelayPerformMethod.getMethod().performMethodDelayTime(3000, FreeServiceActivity.this, "back");
    }

    private void back() {
        FreeServiceActivity.this.finish();
    }

    public void logGoods() {
        for (int i = 0; i < mGoods.size(); i++) {
            DfthClaimGoods goods = mGoods.get(i);
            StringBuilder builder = new StringBuilder();
            builder.append("name = ").append(goods.name).append('\n')
                    .append("code = ").append(goods.code).append('\n')
                    .append("desc = ").append(goods.desc).append('\n')
                    .append("price = ").append(goods.price).append('\n')
                    .append("type = ").append(goods.type).append('\n')
                    .append("category = ").append(goods.category).append('\n')
                    .append("brand = ").append(goods.brand).append('\n')
                    .append("isExists = ").append(goods.isExists).append('\n')
                    .append("id = ").append(goods.id);
            Logger.e(builder.toString());
        }
    }
}
