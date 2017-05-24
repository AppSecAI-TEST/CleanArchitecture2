package com.cleanarchitecture.shishkin.base.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.cleanarchitecture.shishkin.base.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.base.event.OnScreenOffEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * LiveLongAndProsperIntentService is a base class for {@link Service}s that handle
 * asynchronous * requests (expressed as {@link Intent}s) on demand.  Clients send
 * requests through {@link android.content.Context#startService(Intent)} calls; the
 * service is started as needed, handles each Intent in turn using a worker
 * thread, and <b>doesn't stop</b> itself when it runs out of work.
 * <p/>
 * <p>This "work queue processor" pattern is commonly used to offload tasks
 * from an application's main thread.  The LiveLongAndProsperIntentService class exists to
 * simplify this pattern and take care of the mechanics.  To use it, extend
 * LiveLongAndProsperIntentService and implement {@link #onHandleIntent(Intent)}.
 * LiveLongAndProsperIntentService will receive the Intents, and launch a worker thread.
 * <p/>
 * <p>All requests are handled on a single worker thread -- they may take as
 * long as necessary (and will not block the application's main loop), but
 * only one request will be processed at a time.
 * <p/>
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For a detailed discussion about how to create services, read the
 * <a href="{@docRoot}guide/topics/fundamentals/services.html">Services</a> developer guide.</p>
 * </div>
 *
 * @see android.os.AsyncTask
 */
@SuppressWarnings("unused")
public abstract class LiveLongBackgroundIntentService extends Service
        implements AutoCompleteHandler.OnHandleEventListener<Intent>,
        AutoCompleteHandler.OnShutdownListener {

    private final String mName;

    private AutoCompleteHandler<Intent> mServiceHandler;
    private boolean mLiveLong = false;

    /**
     * Creates an LiveLongAndProsperIntentService. Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LiveLongBackgroundIntentService(final String name) {
        super();
        mName = name;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventController.getInstance().register(this);

        mServiceHandler = new AutoCompleteHandler<>("LiveLongAndProsperIntentService [" + mName + "]");
        mServiceHandler.setOnHandleEventListener(this);
        mServiceHandler.setOnShutdownListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventController.getInstance().unregister(this);
    }

    /**
     * Set service should live when intents queue is empty or not.
     * Live long service can be used to listen to system or app callbacks even when
     * there are no user interface available.
     *
     * @param liveLong true if service should live when intents queue is empty, false otherwise.
     * @see #setShutdownTimeout(long)
     */
    public void setLiveLong(final boolean liveLong) {
        mLiveLong = liveLong;
    }

    /**
     * Set shutdown timeout in milliseconds when messages queue will be stopped
     * after queue is empty.
     *
     * @param shutdownTimeout The timeout in milliseconds.
     * @see #setLiveLong(boolean)
     */
    public void setShutdownTimeout(final long shutdownTimeout) {
        if (shutdownTimeout > 0){
            setLiveLong(false);
            mServiceHandler.setShutdownTimeout(shutdownTimeout);
        }
    }

    @SuppressWarnings("deprecated")
    @Override
    public final void onStart(final Intent intent, final int startId) {
        mServiceHandler.post(intent);
    }

    /**
     * You should not override this method for your LiveLongAndProsperIntentService. Instead,
     * override {@link #onHandleIntent}, which the system calls when the
     * LiveLongAndProsperIntentService receives a start request.
     *
     * @see Service#onStartCommand
     */
    @Override
    public final int onStartCommand(final Intent intent, final int flags, final int startId) {
        onStart(intent, startId);
        return START_STICKY;
    }

    /**
     * Unless you provide binding for your service, you don't need to implement this
     * method, because the default implementation returns null.
     *
     * @see Service#onBind
     */
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    /**
     * Callback method for async auto complete queue
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(Intent)}.
     */
    @Override
    public final void onHandleEvent(final Intent intent) {
        if (intent != null) {
            onHandleIntent(intent);
        }
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same LiveLongAndProsperIntentService, but it will not hold up anything else.
     * When all requests have been handled, the LiveLongAndProsperIntentService
     * will not stop itself, so you can use it to observer data changes.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(Intent)}.
     */
    @WorkerThread
    protected abstract void onHandleIntent(@NonNull final Intent intent);

    @Override
    public void onShutdown(final AutoCompleteHandler handler) {
        if (!mLiveLong) {
            stopSelf();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onScreenOffEvent(final OnScreenOffEvent event) {
        if (!mLiveLong) {
            stopSelf();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onFinishApplicationEvent(final FinishApplicationEvent event) {
        if (!mLiveLong) {
            stopSelf();
        }
    }

}
