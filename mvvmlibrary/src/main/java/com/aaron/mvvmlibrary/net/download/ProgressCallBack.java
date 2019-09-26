package com.aaron.mvvmlibrary.net.download;

import android.util.Log;

import com.aaron.mvvmlibrary.bus.RxBus;
import com.aaron.mvvmlibrary.bus.RxSubscriptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 * 进度回调
 */
public abstract class ProgressCallBack<T> {

    private String destFileDir; // 本地文件存放路径
    private String destFileName; // 文件名
    private Disposable mSubscription;

    public ProgressCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
        subscribeLoadProgress();
    }

    /**
     * 下载成功回调
     *
     * @param t
     */
    public abstract void onSuccess(T t);

    /**
     * 进度回调
     * 注意：在主线程中回调
     *
     * @param progress 当前进度
     * @param total    总下载量
     */
    public abstract void progress(long progress, long total);

    /**
     * 下载开始回调
     */
    public void onStart() {
    }

    /**
     * 下载完成回调
     */
    public void onCompleted() {
    }

    /**
     * 下载出错回调
     *
     * @param e
     */
    public abstract void onError(Throwable e);

    /**
     * 保存下载文件
     *
     * @param body
     */
    public void saveFile(ResponseBody body) {
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        FileOutputStream fos = null;
        try {
            is = body.byteStream();
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName);
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            // 取消订阅
            unsubscribe();
            //onCompleted();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.e("saveFile", e.getMessage());
            }
        }
    }

    /**
     * 订阅ProgressInterceptor拦截器ProgressResponseBody中RxBus发送的进度消息
     * 下载状态封装在DownLoadStateBean类中
     */
    public void subscribeLoadProgress() {
        mSubscription = RxBus.getDefault().toObservable(DownLoadStateBean.class)
                .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
                .subscribe(new Consumer<DownLoadStateBean>() {
                    @Override
                    public void accept(final DownLoadStateBean progressLoadBean) throws Exception {
                        progress(progressLoadBean.getBytesLoaded(), progressLoadBean.getTotal());
                    }
                });
        //将订阅者加入管理站
        RxSubscriptions.add(mSubscription);
    }

    /**
     * 取消订阅，防止内存泄漏
     */
    public void unsubscribe() {
        RxSubscriptions.remove(mSubscription);
    }
}