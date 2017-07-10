package com.cleanarchitecture.shishkin.api.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.cleanarchitecture.shishkin.api.controller.ErrorController;
import com.cleanarchitecture.shishkin.common.utils.StringUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class BaseItem implements Parcelable {
    private static final String LOG_TAG = "BaseItem:";

    private Map<String, Object> mFields = Collections.synchronizedMap(new ConcurrentHashMap<String, Object>());
    private ReentrantLock mLock = new ReentrantLock();

    public BaseItem() {
    }

    public void put(final String field, final Object value) {
        if (StringUtils.isNullOrEmpty(field)) {
            return;
        }

        mLock.lock();
        try {
            if (value instanceof List) {
                final Object[] array = ((List) value).toArray();
            } else {
                mFields.put(field, value);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    public Object get(final String field) {
        if (StringUtils.isNullOrEmpty(field)) {
            return null;
        }

        mLock.lock();
        try {
            if (mFields.containsKey(field)) {
                final Object object = mFields.get(field);
                if (object instanceof JSONArray) {
                    return ((JSONArray) object).toArray();
                }
                return object;
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
        return null;
    }

    public void remove(final String field) {
        if (StringUtils.isNullOrEmpty(field)) {
            return;
        }

        mLock.lock();
        try {
            if (mFields.containsKey(field)) {
                mFields.remove(field);
            }
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
    }

    public String toJson() {
        mLock.lock();
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.putAll(mFields);
            return jsonObject.toJSONString();
        } catch (Exception e) {
            ErrorController.getInstance().onError(LOG_TAG, e);
        } finally {
            mLock.unlock();
        }
        return null;
    }

    public BaseItem fromJson(final String json) {
        if (!StringUtils.isNullOrEmpty(json)) {
            mLock.lock();
            try {
                final JSONObject object = (JSONObject) JSONValue.parse(json);
                final Set<String> keySet = object.keySet();
                for (String key : keySet) {
                    mFields.put(key, object.get(key));
                }
            } catch (Exception e) {
                ErrorController.getInstance().onError(LOG_TAG, e);
            } finally {
                mLock.unlock();
            }
        }
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toJson());
    }

    protected BaseItem(Parcel in) {
        this.fromJson(in.readString());
    }

    public static final Parcelable.Creator<BaseItem> CREATOR = new Parcelable.Creator<BaseItem>() {
        @Override
        public BaseItem createFromParcel(Parcel source) {
            return new BaseItem(source);
        }

        @Override
        public BaseItem[] newArray(int size) {
            return new BaseItem[size];
        }
    };
}
