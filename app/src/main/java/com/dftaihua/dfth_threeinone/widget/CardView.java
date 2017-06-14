package com.dftaihua.dfth_threeinone.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dftaihua.dfth_threeinone.R;

/*    *****      *     *
**    *    *      *   * 
**    *****         *    
**    *   *         *  
**    *    *        *
**    *     *       *
* 创建时间：2017/1/16 13:12
*/
public class CardView extends LinearLayout implements View.OnClickListener {


//    private final DFTH_MsgReceiver mMsgReceiver;
    private LinearLayout mCardView;
    private Button mCardbtn;
    private LinearLayout mCardecgshow;
    private TextView mCardtime;
    private TextView mCardtitleview;
    private View mView;
    private int mCardType;
    private Context mContext;

    public CardView(Context context) {
        this(context, null);

    }

    public CardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //注册东方泰华的广播监听
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(DfthBroadcast.Action);
//        mMsgReceiver = new DFTH_MsgReceiver();
//        context.registerReceiver(mMsgReceiver, filter);
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.item_card_view, this);
//        View v = LayoutInflater.from(context).inflate(R.layout.item_card_view, this);
        mCardbtn = (Button) mView.findViewById(R.id.card_btn);
        mCardecgshow = (LinearLayout) mView.findViewById(R.id.card_ecg_show);
        mCardtime = (TextView) mView.findViewById(R.id.card_time);
        mCardtitleview = (TextView) mView.findViewById(R.id.card_title_view);
        mCardView = (LinearLayout) mView.findViewById(R.id.card_view);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Card);
        int textRes = a.getResourceId(R.styleable.Card_name,-1);
        mCardType = a.getInt(R.styleable.Card_type,0);

        if (mCardType==0 && textRes != -1){
            mCardtitleview.setText(textRes);
            mCardbtn.setText("点击搜索设备");
        }else if(mCardType==1&& textRes != -1){
            mCardtitleview.setText(textRes);
            mCardbtn.setText("进入心电");
        }else if (mCardType==2&& textRes != -1){
            mCardtitleview.setText(textRes);
        }else if (mCardType==3&& textRes != -1){
            mCardtitleview.setText(textRes);
        }
        a.recycle();
        mCardView.setOnClickListener(this);
        mCardbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.card_view:

                break;
            case R.id.card_btn: {
                if (mCardType==0){
//                    DfthSDKManager.getManager().discoverDevice("", DfthDevice.EcgDevice, 20);
                }
                if (mCardType==1){

                }
            }
            break;
        }
    }

//    public class DFTH_MsgReceiver extends BroadcastReceiver {
//        @Override
//        public synchronized void onReceive(Context context, Intent intent) {
//            String eventName = intent.getStringExtra(DfthBroadcast.EventName);
//            Bundle bundle = intent.getBundleExtra(DfthBroadcast.EventBundle);
//            switch (eventName) {
//                case  DfthEvent.DeviceDiscovered: {
//                        DfthSDKManager.getManager().stopScanDevice();
//                        String mac = intent.getStringExtra(DfthBroadcast.DeviceMac);
//                        Logger.e("DfthEvent.DeviceDiscovered : " + mac);
//                    Toast.makeText(mContext,"搜索到设备"+mac,Toast.LENGTH_SHORT).show();
//                    break;
//                }
//            }
//        }
//    }
}
