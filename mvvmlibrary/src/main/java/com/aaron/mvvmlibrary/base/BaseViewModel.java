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

/**
 * 基础的ViewModel，继承AndroidViewModel
 * 实现的功能：
 * （1）提供显示对话框，取消对话框，Activity跳转，返回按钮事件，结束页面事件的LiveData，调用相关方法就可以
 * （2）addSubscribe(Disposable disposable) 添加Disposable对象到CompositeDisposable，统一管理
 */
public class BaseViewModel extends AndroidViewModel {
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private CompositeDisposable mCompositeDisposable;
    // 一些界面相关的观察者
    private MutableLiveData<DialogData> showDialogEvent;
    private MutableLiveData<Void> dismissDialogEvent;
    private MutableLiveData<Map<String, Object>> startActivityEvent;
    private MutableLiveData<Map<String, Object>> startContainerActivityEvent;
    private MutableLiveData<Void> finishEvent;
    private MutableLiveData<Void> onBackPressedEvent;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mCompositeDisposable = new CompositeDisposable();
    }

    public MutableLiveData<DialogData> getShowDialogEvent() {
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

    /**
     * 添加Disposable对象到CompositeDisposable，统一管理
     * @param disposable
     */
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
        removeRxBus();
    }

    /**
     * 注册RxBus,订阅事件
     * 在Activity的onCreate方法中调用
     */
    public void registerRxBus() {
        // 订阅事件
    }

    /**
     * 移除RxBus订阅
     */
    public void removeRxBus() {

    }

    /**
     * 进度对话框
     */
    @MainThread
    public void showProgressDialog() {
        showDialog("请稍后...",true);
    }

    /**
     * 带信息对话框
     * @param title 展示的文字内容
     * @param isProcessDialog 是否进度对话框
     */
    @MainThread
    public void showDialog(String title,boolean isProcessDialog) {
        DialogData dialogData = new DialogData();
        dialogData.title = title;
        dialogData.isProcessDialog = false;
        showDialogEvent.postValue(dialogData);
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
