package com.cleanarchitecture.shishkin.base.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.cleanarchitecture.shishkin.IAidlInterface;

public class AidlService extends Service {
    public static final String NAME = "AidlService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new IAidlInterface.Stub() {
            public int getId() throws RemoteException {
                return -1;
            }

        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}