package com.cleanarchitecture.shishkin.base.event;

/**
 * Событие - приложению предоставлено запрошенное право
 */
public class OnPermisionGrantedEvent extends AbstractEvent {
    private String mPermission;

    public OnPermisionGrantedEvent(final String permission) {
        mPermission = permission;
    }

    public String getPermission() {
        return mPermission;
    }


}
