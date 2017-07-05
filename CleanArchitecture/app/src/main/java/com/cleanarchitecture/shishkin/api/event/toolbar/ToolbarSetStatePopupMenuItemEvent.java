package com.cleanarchitecture.shishkin.api.event.toolbar;

import com.cleanarchitecture.shishkin.api.event.AbstractEvent;
import com.cleanarchitecture.shishkin.api.presenter.ToolbarPresenter;

/**
 * Событие - установить состояние элемента меню
 */
public class ToolbarSetStatePopupMenuItemEvent extends AbstractEvent {

    private Integer mMenuItemId = 0;
    private Integer mState = ToolbarPresenter.POPOP_MENU_ITEM_STATE_ENABLED;

    public ToolbarSetStatePopupMenuItemEvent(final int menuItemId, final int state) {
        mMenuItemId = menuItemId;
        mState = state;
    }

    public Integer getMenuItemId() {
        return mMenuItemId;
    }

    public Integer getState() {
        return mState;
    }

}
