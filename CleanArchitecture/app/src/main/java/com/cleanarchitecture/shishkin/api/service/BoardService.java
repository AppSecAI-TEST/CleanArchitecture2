package com.cleanarchitecture.shishkin.api.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.WorkerThread;
import android.support.v4.app.NotificationCompat;

import com.cleanarchitecture.shishkin.BuildConfig;
import com.cleanarchitecture.shishkin.R;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.api.controller.IPresenterController;
import com.cleanarchitecture.shishkin.api.controller.NotificationModule;
import com.cleanarchitecture.shishkin.api.event.toolbar.ToolbarSetBadgeEvent;
import com.cleanarchitecture.shishkin.api.presenter.ExpandableBoardPresenter;
import com.cleanarchitecture.shishkin.application.ui.activity.MainActivity;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Сервис вывода сообщений в зону уведомлений
 */
@SuppressWarnings("unused")
public class BoardService extends LiveLongBackgroundIntentService {

    public static final String NAME = BoardService.class.getName();

    private List<String> mMessages;
    private int mMessagesCount = 1;
    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public BoardService() {
        super(NAME);

        mMessages = Collections.synchronizedList(new LinkedList<String>());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    @Override
    @WorkerThread
    public void onHandleIntent(final Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        if (NotificationModule.ACTION_ADD_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (NotificationModule.ACTION_ADD_DISTINCT_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddDistinctMessageAction(text);

        } else if (NotificationModule.ACTION_REPLACE_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleReplaceMessageAction(text);

        } else if (NotificationModule.ACTION_REFRESH_MESSAGES.equals(action)) {
            onHandleRefreshAction();

        } else if (NotificationModule.ACTION_CLEAR_MESSAGES.equals(action)) {
            onHandleClearAction();

        } else if (NotificationModule.ACTION_SET_MESSAGES_COUNT.equals(action)) {
            final int count = StringUtils.toInt(intent.getStringExtra(Intent.EXTRA_TEXT));
            onHandleSetMessagesCount(count);
        }
    }

    private synchronized void sendNotification(final String message) {
        final int count = mMessages.size();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(mMessages.get(i));
            sb.append("\n\n");
        }

        final IPresenterController controller = AdminUtils.getPresenterController();
        if (controller != null) {
            final ExpandableBoardPresenter presenter = (ExpandableBoardPresenter)controller.getPresenter(ExpandableBoardPresenter.NAME);
            if (presenter != null) {
                if (count > 0) {
                    presenter.setText(sb.toString());
                    presenter.showBoard();
                } else {
                    presenter.setText("");
                    presenter.hideBoard();
                }
            }
        }
    }

    @WorkerThread
    private void onHandleAddMessageAction(final String message) {
        mMessages.add(0, message);
        while (mMessages.size() > mMessagesCount) {
            mMessages.remove(mMessages.get(mMessages.size() - 1));
        }

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleAddDistinctMessageAction(final String message) {
        if (!mMessages.contains(message)) {
            mMessages.add(0, message);
            while (mMessages.size() > mMessagesCount) {
                mMessages.remove(mMessages.get(mMessages.size() - 1));
            }

            sendNotification(message);
        } else {
            onHandleRefreshAction();
        }
    }

    @WorkerThread
    private void onHandleReplaceMessageAction(final String message) {
        mMessages.clear();
        mMessages.add(0, message);

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleRefreshAction() {
        if (mMessages.isEmpty()) {
            onHandleClearAction();
            return;
        }

        final String message = mMessages.get(0);

        sendNotification(message);
    }

    @WorkerThread
    private void onHandleClearAction() {
        mMessages.clear();
    }

    @WorkerThread
    private void onHandleSetMessagesCount(final int count) {
        mMessagesCount = count;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
