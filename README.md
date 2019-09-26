# AndroidMyMVVMDemo
使用databinding livedata双向绑定的MVVM模式的通用开发库

## 引入依赖：
`${latest.version}` is [[V1.0.0]](https://dl.bintray.com/aaronstars/android-library/AndroidBaseMVVMLibrary)

```groovy
dependencies {
  implementation 'com.aaron:android-base-MVVM-library:${latest.version}'
}
```

## 功能列表
- base包    --- activity，fragment，application等基础类，提供日志，生命周期，常用工具，databinding双向绑定的支持
- bus包     --- RxBus实现，viewmodel通信类Messenger
- binding包 --- 控件绑定自定义扩展
- crash包   --- 崩溃处理
- net 包    --- Retrofit+Rxjava网络访问封装
- utils包   --- 常用工具

## 自定义BaseApplication子类
### 例子：

```java
public class AppApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(LoginActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }
}
```

## Rxbus 使用

```java
Disposable mSubscription = RxBus.getDefault().toObservable(DownLoadStateBean.class)
        .observeOn(AndroidSchedulers.mainThread()) //回调到主线程更新UI
        .subscribe(new Consumer<DownLoadStateBean>() {
            @Override
            public void accept(final DownLoadStateBean progressLoadBean) throws Exception {
                progress(progressLoadBean.getBytesLoaded(), progressLoadBean.getTotal());
            }
        });
//将订阅者加入管理站
RxSubscriptions.add(mSubscription);

// 取消订阅
RxSubscriptions.remove(mSubscription);

RxBus.getDefault().post(new DownLoadStateBean(contentLength(), bytesReaded, tag));
```

## Messenger 使用

Messenger是一个轻量级全局的消息通信工具，在我们的复杂业务中，难免会出现一些交叉的业务，比如
ViewModel与ViewModel之间需要有数据交换，这时候可以轻松地使用Messenger发送一个实体或一个空消息，
将事件从一个ViewModel回调到另一个ViewModel中。

### 使用方法：
定义一个静态String类型的字符串token
```java
public static final String TOKEN_LOGINVIEWMODEL_REFRESH = "token_loginviewmodel_refresh";

在ViewModel中注册消息监听

//注册一个空消息监听
//参数1：接受人（上下文）
//参数2：定义的token
//参数3：执行的回调监听
Messenger.getDefault().register(
this
, LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH
, new BindingAction() {
    @Override
    public void call() {

    }
});

//注册一个带数据回调的消息监听
//参数1：接受人（上下文）
//参数2：定义的token
//参数3：实体的泛型约束
//参数4：执行的回调监听
Messenger.getDefault().register(
this
, LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH
, String.class, new BindingConsumer<String>() {
    @Override
    public void call(String s) {

    }
});

在需要回调的地方使用token发送消息

//发送一个空消息
//参数1：定义的token
Messenger.getDefault().sendNoMsg(LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH);

//发送一个带数据回调消息
//参数1：回调的实体
//参数2：定义的token
Messenger.getDefault().send("refresh",LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH);
```

token最好不要重名，不然可能就会出现逻辑上的bug，为了更好的维护和清晰逻辑，建议以aa_bb_cc的格式来定义token。aa：TOKEN，bb：ViewModel的类名，cc：动作名（功能名）。
为了避免大量使用Messenger，建议只在ViewModel与ViewModel之间使用，View与ViewModel之间采用ObservableField去监听UI上的逻辑，可在继承了Base的Activity或Fragment中重写initViewObservable()方法来初始化UI的监听。注册了监听，当然也要解除它。在BaseActivity、BaseFragment的onDestroy()方法里已经调用Messenger.getDefault().unregister(viewModel);解除注册，所以不用担心忘记解除导致的逻辑错误和内存泄漏。

## Retrofit+Rxjava 使用

提供了一个RetrofitClient封装单例类, 实现网络请求，可以自己写一个

```java
RetrofitClientDemo.getInstance().getService(DemoApiService.class)
 .login(name,password)
 .subscribeOn(Schedulers.io())
 .observeOn(AndroidSchedulers.mainThread())
 .subscribe(subscriber);
 
 提供了一些自定义的Interceptor
 LoggingInterceptor 日志拦截器
 OkHttpClient.Builder builder = new OkHttpClient.Builder()；
 builder.addInterceptor(new LoggingInterceptor
                    .Builder()
                    .loggable(true) //是否开启日志打印
                    .setLevel(Level.BASIC) //打印的等级
                    .log(Platform.INFO) // 打印类型
                    .request("Request") // request的Tag
                    .response("Response")// Response的Tag
                    .addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
                    .build()
            );
 
 
 CacheInterceptor 缓存拦截器，提供离线缓存
 ChangeUrlInterceptor 动态修改请求地址拦截器
 HeaderInterceptor 请求头统一修改拦截器
 RetryInterceptor 重试限定次数拦截器
 
 DownLoadManager 下载文件
 ApiDisposableObserver 请求响应错误处理例子
```
