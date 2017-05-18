package com.cleanarchitecture.shishkin.base.repository;

import com.cleanarchitecture.shishkin.base.event.repository.RepositoryRequestGetImageEvent;
import com.cleanarchitecture.shishkin.base.repository.net.requests.GetImageRequest;

public class RepositoryNetProvider {

    private RepositoryNetProvider() {
    }

    public static synchronized void requestGetImage(final RepositoryRequestGetImageEvent event){
        NetProvider.getInstance().request(new GetImageRequest(event));
    }


}
