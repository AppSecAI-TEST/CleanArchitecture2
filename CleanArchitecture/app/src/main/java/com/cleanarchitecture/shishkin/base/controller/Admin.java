package com.cleanarchitecture.shishkin.base.controller;

import com.cleanarchitecture.shishkin.base.repository.IRepository;
import com.cleanarchitecture.shishkin.base.repository.Repository;
import com.cleanarchitecture.shishkin.base.usecases.UseCasesController;

@SuppressWarnings("unused")
public class Admin extends AbstractAdmin {
    public static final String NAME = "Admin";

    private static volatile Admin sInstance;

    public static Admin getInstance() {
        if (sInstance == null) {
            synchronized (Admin.class) {
                if (sInstance == null) {
                    sInstance = new Admin();
                }
            }
        }
        return sInstance;
    }

    private Admin() {
        super();

        registerModule(ErrorController.getInstance());
        registerModule(EventBusController.getInstance());
        registerModule(new CrashController());
        registerModule(new ActivityController());
        registerModule(new LifecycleController());
        registerModule(new PresenterController());
        registerModule(new NavigationController());
        registerModule(new UseCasesController());
        registerModule(new MailController());

        final IRepository repository = new Repository();
        registerModule(repository);
        registerModule(repository.getDbProvider());
        registerModule(repository.getNetProvider());
        registerModule(repository.getContentProvider());

    }

    @Override
    public String getName() {
        return NAME;
    }
}
