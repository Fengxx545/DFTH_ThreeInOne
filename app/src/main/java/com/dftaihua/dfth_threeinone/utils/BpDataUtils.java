package com.dftaihua.dfth_threeinone.utils;

import android.content.Context;

import com.dftaihua.dfth_threeinone.R;
import com.dftaihua.dfth_threeinone.application.ThreeInOneApplication;
import com.dftaihua.dfth_threeinone.constant.Constant;
import com.dftaihua.dfth_threeinone.constant.SharePreferenceConstant;
import com.dftaihua.dfth_threeinone.entity.BpGroupData;
import com.dftaihua.dfth_threeinone.entity.BpPlanData;
import com.dftaihua.dfth_threeinone.manager.UserManager;
import com.dfth.sdk.DfthSDKManager;
import com.dfth.sdk.config.BpAnalysisConfig;
import com.dfth.sdk.config.DfthConfig;
import com.dfth.sdk.model.bp.BpPlan;
import com.dfth.sdk.model.bp.BpResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
public class BpDataUtils {

    public static List<BpResult> mGroups = new ArrayList<>();

    public enum QUERY_RULE {
        DAY, WEEK, MONTH, AMB
    }

    public enum BP_RESULT_LEVEL {
        BP_RESULT_NORMAL, BP_RESULT_HIGH,
        BP_RESULT_HIGHER, BP_RESULT_HIGHEST,
        BP_RESULT_LOW, BP_RESULT_LOWER;

        public int getStringRes() {
            if (this.equals(BP_RESULT_NORMAL)) {
                return R.string.bp_result_normal;
            } else if (this.equals(BP_RESULT_HIGH)) {
                return R.string.bp_result_high;
            } else if (this.equals(BP_RESULT_HIGHER)) {
                return R.string.bp_result_higher;
            } else if (this.equals(BP_RESULT_HIGHEST)) {
                return R.string.bp_result_highest;
            } else if (this.equals(BP_RESULT_LOW)) {
                return R.string.bp_result_low;
            } else {
                return R.string.bp_result_lower;
            }
        }
    }

    public static boolean isHighValue(List<BpResult> mResults){
        for (int i = 0; i < mResults.size(); i++) {
            BpResult result = mResults.get(i);
            if (result.isValidData()) {
                if(result.getSbp() > 180 || result.getDbp() < 40
                        || result.getPulseRate() > 180
                        || result.getPulseRate() < 40){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param mResults
     * @return int[] size= 3
     * 0为高压平均值
     * 1为低压平均值
     * 2为脉率平均值
     */
    public static int[] getAverData(List<BpResult> mResults) {
        int highSum = 0;
        int lowSum = 0;
        int rateSum = 0;
        int effectTimes = 0;
        for (int i = 0; i < mResults.size(); i++) {
            BpResult result = mResults.get(i);
            if (result.isValidData()) {
                highSum += result.getSbp();
                lowSum += result.getDbp();
                rateSum += result.getPulseRate();
                effectTimes++;
            }
        }
        if (effectTimes == 0) {
            return new int[]{0, 0, 0, 0};
        }
        int[] avers = new int[4];
        avers[0] = (int) Math.rint(Math.rint(Math.nextUp(highSum / effectTimes)));
        avers[1] = (int) Math.rint(Math.rint(Math.nextUp(lowSum / effectTimes)));
        avers[2] = (int) Math.rint(Math.rint(Math.nextUp(rateSum / effectTimes)));
        avers[3] = effectTimes;
        return avers;
    }

    /**
     * 获得高压的等级
     *
     * @param value
     * @return
     */
    private static BP_RESULT_LEVEL getHighLevel(int value) {
        BpAnalysisConfig config = DfthConfig.getConfig().bpConfig.analysisConfig;
        int normalHigh = config.normalStandardJudgeConfig.maxHighPressure;
        int lowHigh = config.lowJudgeConfig.maxHighPressure;
        int lowerHigh = config.lowerJudgeConfig.maxHighPressure;
        int highHigh = config.highJudgeConfig.maxHighPressure;
        int higherHigh = config.higherJudgeConfig.maxHighPressure;
        int highestHigh = config.highestJudgeConfig.maxHighPressure;

        int normalLow = config.normalStandardJudgeConfig.minHighPressure;
        int lowLow = config.lowJudgeConfig.minHighPressure;
        int lowerLow = config.lowerJudgeConfig.minHighPressure;
        int highLow = config.highJudgeConfig.minHighPressure;
        int higherLow = config.higherJudgeConfig.minHighPressure;
        int highestLow = config.highestJudgeConfig.minHighPressure;

        BP_RESULT_LEVEL highLevel;
        if (value >= normalLow && value < normalHigh) {
            highLevel = BP_RESULT_LEVEL.BP_RESULT_NORMAL;
        } else if (value >= highLow && value <= highHigh) {
            highLevel = BP_RESULT_LEVEL.BP_RESULT_HIGH;
        } else if (value >= higherLow && value <= higherHigh) {
            highLevel = BP_RESULT_LEVEL.BP_RESULT_HIGHER;
        } else if (value >= highestLow && value < highestHigh) {
            highLevel = BP_RESULT_LEVEL.BP_RESULT_HIGHEST;
        } else if (value > lowLow && value < lowHigh) {
            highLevel = BP_RESULT_LEVEL.BP_RESULT_LOW;
        } else if (value > lowerLow && value <= lowerHigh){
            highLevel = BP_RESULT_LEVEL.BP_RESULT_LOWER;
        } else{
            highLevel = BP_RESULT_LEVEL.BP_RESULT_NORMAL;
        }
        return highLevel;
    }

    /**
     * 获得低压的等级
     *
     * @param value
     * @return
     */
    private static BP_RESULT_LEVEL getLowLevel(int value) {
        BpAnalysisConfig config = DfthConfig.getConfig().bpConfig.analysisConfig;
        int normalHigh = config.normalStandardJudgeConfig.maxLowPressure;
        int lowHigh = config.lowJudgeConfig.maxLowPressure;
        int lowerHigh = config.lowerJudgeConfig.maxLowPressure;
        int highHigh = config.highJudgeConfig.maxLowPressure;
        int higherHigh = config.higherJudgeConfig.maxLowPressure;
        int highestHigh = config.highestJudgeConfig.maxLowPressure;

        int normalLow = config.normalStandardJudgeConfig.minLowPressure;
        int lowLow = config.lowJudgeConfig.minLowPressure;
        int lowerLow = config.lowerJudgeConfig.minLowPressure;
        int highLow = config.highJudgeConfig.minLowPressure;
        int higherLow = config.higherJudgeConfig.minLowPressure;
        int highestLow = config.highestJudgeConfig.minLowPressure;

        BP_RESULT_LEVEL lowLevel;
        if (value >= normalLow && value < normalHigh) {
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_NORMAL;
        } else if (value >= highLow && value <= highHigh) {
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_HIGH;
        } else if (value >= higherLow && value <= higherHigh) {
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_HIGHER;
        } else if (value >= highestLow && value < highestHigh) {
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_HIGHEST;
        } else if (value > lowLow && value < lowHigh) {
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_LOW;
        } else if (value > lowerLow && value <= lowerHigh){
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_LOWER;
        } else{
            lowLevel = BP_RESULT_LEVEL.BP_RESULT_NORMAL;
        }
        return lowLevel;
    }

    public static int getLevel(int[] averValues) {
        BP_RESULT_LEVEL level = BP_RESULT_LEVEL.BP_RESULT_NORMAL;
        BP_RESULT_LEVEL highLevel = getHighLevel(averValues[0]);
        BP_RESULT_LEVEL lowLevel = getLowLevel(averValues[1]);
        switch (highLevel) {
            case BP_RESULT_NORMAL:
                level = lowLevel;
                break;
            case BP_RESULT_HIGH:
                if (lowLevel.equals(BP_RESULT_LEVEL.BP_RESULT_HIGHER) || lowLevel.equals(BP_RESULT_LEVEL.BP_RESULT_HIGHEST)) {
                    level = lowLevel;
                } else {
                    level = highLevel;
                }
                break;
            case BP_RESULT_HIGHER:
                if (lowLevel.equals(BP_RESULT_LEVEL.BP_RESULT_HIGHEST)) {
                    level = lowLevel;
                } else {
                    level = highLevel;
                }
                break;
            case BP_RESULT_HIGHEST:
                level = highLevel;
                break;
            case BP_RESULT_LOW:
                if (lowLevel.equals(BP_RESULT_LEVEL.BP_RESULT_LOWER)) {
                    level = lowLevel;
                } else {
                    level = highLevel;
                }
                break;
            case BP_RESULT_LOWER:
                level = highLevel;
                break;
            default:
                break;
        }
        return level.getStringRes();
    }

    /**
     * 初始化一年的血压数据
     */
    public static void initDatas() {
        mGroups.clear();
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        mGroups.addAll(DfthSDKManager.getManager().getDatabase().getBPPlanResult(userId));
    }

    public static void getGroupData(BpGroupData groupData, List<BpPlan> plans) {
        String userId = UserManager.getInstance().getDefaultUser().getUserId();
        for (int i = 0; i < plans.size(); i++) {
            BpPlan plan = plans.get(i);
            boolean isValid = false;
            List<BpResult> results;
            if(plan.getStatus() == BpPlan.STATE_RUNNING){
                results = DfthSDKManager.getManager().getDatabase().getBPResultByPlanTime(userId, plan);
                isValid = true;
            } else{
                results = DfthSDKManager.getManager().getDatabase().getBPResultByPlanTime(userId, plan);
                if(results == null || results.size() == 0){
                    continue;
                }
                for(int j = 0; j < results.size(); j++){
                    if(results.get(j).isValidData()){
                        isValid = true;
                        break;
                    }
                }
            }
            if(isValid){
                int[] avers = getAverData(results);
                BpPlanData planData = new BpPlanData();
                planData.setAverSSY(avers[0]);
                planData.setAverSZY(avers[1]);
                planData.setAverBeat(avers[2]);
                planData.setEffectTimes(avers[3]);
                planData.setBeginTime(plan.getStartTime());
                planData.setEndTime(plan.getEndTime());
                planData.setSetPlanTime(plan.getSetPlanTime());
                planData.setStandard(plan.getStandard());
                planData.setPattern(plan.getPattern());
                groupData.add(planData);
            }
        }
    }

    public static String getDefaultUnitValue(Context context, int value){
        int unit = (int) SharePreferenceUtils.get(context, SharePreferenceConstant.BP_VALUE_UNIT, Constant.BP_UNIT_MMHG);
        if(unit == Constant.BP_UNIT_KPA)
            return MathUtils.mmHgToKpa(value) + "";
        return value + "";
    }

    public static String getDefaultUnit(Context context){
        int unit = (int) SharePreferenceUtils.get(context, SharePreferenceConstant.BP_VALUE_UNIT, Constant.BP_UNIT_MMHG);
        if(unit == Constant.BP_UNIT_MMHG)
            return ThreeInOneApplication.getStringRes(R.string.setting_mmHg);
        return ThreeInOneApplication.getStringRes(R.string.setting_kPa);
    }

    public static List<BpResult> getCorrectData(List<BpResult> results){
        List<BpResult> bpResults = new ArrayList<>();
        for(int i = 0; i < results.size(); i++){
            BpResult result = results.get(i);
            if(result.isCorrectData()){
                bpResults.add(result);
            }
        }
        return bpResults;
    }

    public static List<BpResult> getInvalidData(List<BpResult> results){
        List<BpResult> bpResults = new ArrayList<>();
        for(int i = 0; i < results.size(); i++){
            BpResult result = results.get(i);
            if(result.isValidData()){
                bpResults.add(result);
            }
        }
        return bpResults;
    }
}
