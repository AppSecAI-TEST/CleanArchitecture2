package com.cleanarchitecture.shishkin.api.service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.Constant;
import com.cleanarchitecture.shishkin.api.controller.IPresenterController;
import com.cleanarchitecture.shishkin.api.mail.HideBoardMail;
import com.cleanarchitecture.shishkin.api.mail.SetTextBoardMail;
import com.cleanarchitecture.shishkin.api.mail.ShowBoardMail;
import com.cleanarchitecture.shishkin.api.presenter.ExpandableBoardPresenter;
import com.cleanarchitecture.shishkin.api.storage.CacheUtils;
import com.cleanarchitecture.shishkin.common.utils.IntentUtils;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Сервис вывода сообщений в зону Expandable Board
 */
@SuppressWarnings("unused")
public class BoardService extends ShortlyLiveBackgroundIntentService {

    public static final String NAME = BoardService.class.getName();

    private List<String> mMessages;
    private int mMessagesCount = 100;
    private static final TimeUnit TIMEUNIT = TimeUnit.MINUTES;
    private static final long TIMEUNIT_DURATION = 5L;

    public BoardService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setShutdownTimeout(TIMEUNIT.toMillis(TIMEUNIT_DURATION));
    }

    private static void sendIntent(final Context context, final String action, final String message) {
        if (context != null && !StringUtils.isNullOrEmpty(message)) {
            final Intent intent = IntentUtils.createActionIntent(context, BoardService.class, action);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            context.startService(intent);
        }
    }

    private static void sendIntent(final Context context, final String action) {
        if (context != null) {
            final Intent intent = IntentUtils.createActionIntent(context, BoardService.class, action);
            context.startService(intent);
        }
    }

    public static void addMessage(final Context context, final String message) {
        sendIntent(context, Constant.ACTION_ADD_MESSAGE, message);
    }

    public static void addDistinctMessage(final Context context, final String message) {
        sendIntent(context, Constant.ACTION_ADD_DISTINCT_MESSAGE, message);
    }

    public static void replaceMessage(final Context context, final String message) {
        sendIntent(context, Constant.ACTION_REPLACE_MESSAGE, message);
    }

    public static void clear(final Context context) {
        sendIntent(context, Constant.ACTION_CLEAR_MESSAGES);
    }

    public static void setMessagesCount(final Context context, final int count) {
        if (count > 0) {
            sendIntent(context, Constant.ACTION_SET_MESSAGES_COUNT, String.valueOf(count));
        }
    }

    @Override
    @WorkerThread
    public void onHandleIntent(@Nullable final Intent intent) {
        if (intent == null) {
            return;
        }

        final String action = intent.getAction();

        if (Constant.ACTION_ADD_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddMessageAction(text);

        } else if (Constant.ACTION_ADD_DISTINCT_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleAddDistinctMessageAction(text);

        } else if (Constant.ACTION_REPLACE_MESSAGE.equals(action)) {
            final String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            onHandleReplaceMessageAction(text);

        } else if (Constant.ACTION_CLEAR_MESSAGES.equals(action)) {
            onHandleClearAction();

        } else if (Constant.ACTION_SET_MESSAGES_COUNT.equals(action)) {
            final int count = StringUtils.toInt(intent.getStringExtra(Intent.EXTRA_TEXT));
            onHandleSetMessagesCount(count);
        }
    }

    private synchronized void sendNotification() {
        final StringBuilder sb = new StringBuilder();
        int cnt = mMessagesCount;
        if (mMessagesCount == 0) {
            cnt = mMessages.size();
        }
        if (cnt > mMessages.size()) {
            cnt = mMessages.size();
        }
        for (int i = 0; i < cnt; i++) {
            if (i > 0) {
                sb.append("\n\n");
            }
            sb.append(mMessages.get(i));
        }

        final IPresenterController controller = AdminUtils.getPresenterController();
        if (controller != null) {
            final ExpandableBoardPresenter presenter = (ExpandableBoardPresenter) controller.getPresenter(ExpandableBoardPresenter.NAME);
            if (presenter != null) {
                if (!mMessages.isEmpty()) {
                    AdminUtils.addMail(new SetTextBoardMail(sb.toString()));
                    AdminUtils.addMail(new ShowBoardMail());
                } else {
                    AdminUtils.addMail(new SetTextBoardMail(""));
                    AdminUtils.addMail(new HideBoardMail());
                }
            }
        }
    }

    private void getCache() {
        final String s = (String) CacheUtils.get(NAME, CacheUtils.USE_ONLY_DISK_CACHE);
        if (StringUtils.isNullOrEmpty(s)) {
            mMessages = Collections.synchronizedList(new LinkedList<String>());
        } else {
            mMessages = AdminUtils.getTransformDataModule().fromJson(s, new com.google.gson.reflect.TypeToken<List<String>>() {
            });
            if (mMessages == null) {
                mMessages = Collections.synchronizedList(new LinkedList<String>());
            }
        }
    }

    @WorkerThread
    private void onHandleAddMessageAction(final String message) {
        getCache();
        mMessages.add(0, message);
        while (mMessages.size() > mMessagesCount) {
            mMessages.remove(mMessages.get(mMessages.size() - 1));
        }

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification();
    }

    @WorkerThread
    private void onHandleAddDistinctMessageAction(final String message) {
        getCache();
        if (!mMessages.contains(message)) {
            mMessages.add(0, message);
            while (mMessages.size() > mMessagesCount) {
                mMessages.remove(mMessages.get(mMessages.size() - 1));
            }

            CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
            sendNotification();
        } else {
            onHandleRefreshAction();
        }
    }

    @WorkerThread
    private void onHandleReplaceMessageAction(final String message) {
        getCache();
        mMessages.clear();
        mMessages.add(0, message);

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification();
    }

    @WorkerThread
    private void onHandleRefreshAction() {
        getCache();

        sendNotification();
    }

    @WorkerThread
    private void onHandleClearAction() {
        getCache();
        mMessages.clear();

        CacheUtils.put(NAME, CacheUtils.USE_ONLY_DISK_CACHE, AdminUtils.getTransformDataModule().toJson(mMessages), 0);
        sendNotification();
    }

    @WorkerThread
    private void onHandleSetMessagesCount(final int count) {
        if (count >= 0) {
            mMessagesCount = count;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
