package com.cleanarchitecture.shishkin.common.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import java.util.List;

public class PhoneUtils {
    private PhoneUtils() {
    }

    public static boolean isPhone(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    @SuppressLint("HardwareIds")
    public static String getIMSI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSubscriberId();
    }

    public static String getPhoneStatus(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        final StringBuilder sb = new StringBuilder();
        sb.append("DeviceId(IMEI) = " + tm.getDeviceId() + "\n");
        sb.append("DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n");
        sb.append("Line1Number = " + tm.getLine1Number() + "\n");
        sb.append("NetworkCountryIso = " + tm.getNetworkCountryIso() + "\n");
        sb.append("NetworkOperator = " + tm.getNetworkOperator() + "\n");
        sb.append("NetworkOperatorName = " + tm.getNetworkOperatorName() + "\n");
        sb.append("NetworkType = " + tm.getNetworkType() + "\n");
        sb.append("honeType = " + tm.getPhoneType() + "\n");
        sb.append("SimCountryIso = " + tm.getSimCountryIso() + "\n");
        sb.append("SimOperator = " + tm.getSimOperator() + "\n");
        sb.append("SimOperatorName = " + tm.getSimOperatorName() + "\n");
        sb.append("SimSerialNumber = " + tm.getSimSerialNumber() + "\n");
        sb.append("SimState = " + tm.getSimState() + "\n");
        sb.append("SubscriberId(IMSI) = " + tm.getSubscriberId() + "\n");
        sb.append("VoiceMailNumber = " + tm.getVoiceMailNumber() + "\n");
        return sb.toString();
    }

    public static void dial(Context context, String phoneNumber) {
        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    public static void call(Context context, String phoneNumber) {
        context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNumber)));
    }

    public static void sendSms(Context context, String phoneNumber, String content) {
        final Uri uri = Uri.parse("smsto:" + (StringUtils.isNullOrEmpty(phoneNumber) ? "" : phoneNumber));
        final Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", StringUtils.isNullOrEmpty(content) ? "" : content);
        context.startActivity(intent);
    }

    public static void sendSmsSilent(Context context, String phoneNumber, String content) {
        if (StringUtils.isNullOrEmpty(content)) return;
        final PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        final SmsManager smsManager = SmsManager.getDefault();
        if (content.length() >= 70) {
            final List<String> ms = smsManager.divideMessage(content);
            for (String str : ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null);
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null);
        }
    }
}
