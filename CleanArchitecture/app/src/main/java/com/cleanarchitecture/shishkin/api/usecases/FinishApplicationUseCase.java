package com.cleanarchitecture.shishkin.api.usecases;

import com.cleanarchitecture.shishkin.api.controller.Admin;
import com.cleanarchitecture.shishkin.api.controller.AdminUtils;
import com.cleanarchitecture.shishkin.api.controller.IMailController;
import com.cleanarchitecture.shishkin.api.controller.IPresenterController;
import com.cleanarchitecture.shishkin.api.controller.MailController;
import com.cleanarchitecture.shishkin.api.event.FinishApplicationEvent;
import com.cleanarchitecture.shishkin.api.event.ui.HideKeyboardEvent;
import com.cleanarchitecture.shishkin.api.storage.IStorage;

/**
 * Команда - выход из приложения
 */
public class FinishApplicationUseCase extends AbstractUseCase {
    public static final String NAME = FinishApplicationUseCase.class.getName();

    public static synchronized void onFinishApplication() {

        AdminUtils.postEvent(new HideKeyboardEvent());

        // finish all activities и LiveLongBackgroundIntentService
        AdminUtils.postEvent(new FinishApplicationEvent());

        // очистить кэш в памяти
        final IStorage memoryCache = AdminUtils.getMemoryCache();
        if (memoryCache != null) {
            memoryCache.clearAll();
        }

        // очистить все состояния в PresenterController
        final IPresenterController presenterController = AdminUtils.getPresenterController();
        if (presenterController != null) {
            presenterController.clearStateData();
        }

        // очистить всю почту
        final IMailController mailController = Admin.getInstance().get(MailController.NAME);
        if (mailController != null) {
            mailController.clearMail();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
