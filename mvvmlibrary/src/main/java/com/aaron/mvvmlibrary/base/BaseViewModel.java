package com.aaron.mvvmlibrary.base;

import android.app.Application;
import android.os.Bundle;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseViewModel extends AndroidViewModel {
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;

    // 一些界面相关的观察者
    private MutableLiveData<String> showDialogEvent;
    private MutableLiveData<Void> dismissDialogEvent;
    private MutableLiveData<Map<String, Object>> startActivityEvent;
    private MutableLiveData<Map<String, Object>> startContainerActivityEvent;
    private MutableLiveData<Void> finishEvent;
    private MutableLiveData<Void> onBackPressedEvent;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mCompositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<String> getShowDialogEvent() {
        if (showDialogEvent == null) {
            showDialogEvent = new MutableLiveData();
        }
        return showDialogEvent;
    }

    public MutableLiveData<Void> getDismissDialogEvent() {
        if (dismissDialogEvent == null) {
            dismissDialogEvent = new MutableLiveData();
        }
        return dismissDialogEvent;
    }

    public MutableLiveData<Map<String, Object>> getStartActivityEvent() {
        if (startActivityEvent == null) {
            startActivityEvent = new MutableLiveData();
        }
        return startActivityEvent;
    }

    public MutableLiveData<Map<String, Object>> getStartContainerActivityEvent() {
        if (startContainerActivityEvent == null) {
            startContainerActivityEvent = new MutableLiveData();
        }
        return startContainerActivityEvent;
    }

    public MutableLiveData<Void> getFinishEvent() {
        if (finishEvent == null) {
            finishEvent = new MutableLiveData();
        }
        return finishEvent;
    }

    public MutableLiveData<Void> getOnBackPressedEvent() {
        if (onBackPressedEvent == null) {
            onBackPressedEvent = new MutableLiveData();
        }
        return onBackPressedEvent;
    }

    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        //ViewModel销毁时会执行，同时取消所有异步任务
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    public void registerRxBus() {
    }

    public void removeRxBus() {

    }

    @MainThread
    public void showDialog() {
        showDialog("请稍后...");
    }

    @MainThread
    public void showDialog(String title) {
        showDialogEvent.postValue(title);
    }

    @MainThread
    public void dismissDialog() {
        dismissDialogEvent.postValue(null);
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Map<String, Object> params = new HashMap<>();
        params.put(ParameterField.CLASS, clz);
        if (bundle != null) {
            params.put(ParameterField.BUNDLE, bundle);
        }
        startActivityEvent.postValue(params);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Map<String, Object> params = new HashMap<>();
        params.put(ParameterField.CANONICAL_NAME, canonicalName);
        if (bundle != null) {
            params.put(ParameterField.BUNDLE, bundle);
        }
        startContainerActivityEvent.postValue(params);
    }

    /**
     * 关闭界面
     */
    public void finish() {
        finishEvent.postValue(null);
    }

    /**
     * 返回上一层
     */
    public void onBackPressed() {
        onBackPressedEvent.postValue(null);
    }

public static final class ParameterField {
    public static String CLASS = "CLASS";
    public static String CANONICAL_NAME = "CANONICAL_NAME";
    public static String BUNDLE = "BUNDLE";
}
}
