package com.cleanarchitecture.shishkin.api.event;

/**
 * Событие - приложению предоставлено запрошенное право
 */
public class OnPermisionDeniedEvent extends AbstractEvent {
    private String mPermission;

    public OnPermisionDeniedEvent(final String permission) {
        mPermission = permission;
    }

    public String getPermission() {
        return mPermission;
    }


}
