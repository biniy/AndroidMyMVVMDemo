package com.aaron.mymvvm

import android.app.Application
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aaron.mvvmlibrary.base.BaseViewModel
import com.aaron.mvvmlibrary.binding.command.BindingAction
import com.aaron.mvvmlibrary.binding.command.BindingCommand
import com.aaron.mvvmlibrary.binding.command.BindingConsumer
import com.aaron.mvvmlibrary.utils.ToastUtils


class LoginViewModel(application: Application) :
    BaseViewModel(application) {
    // 用户名的绑定
    var userName = MutableLiveData<String?>("")
    // 密码的绑定
    var password = MutableLiveData<String?>("")
    // 用户名清除按钮的显示隐藏绑定
    var clearBtnVisibility = MutableLiveData<Int?>(0)
    // 密码显示
    var passwordShowSwitch = MutableLiveData<Boolean?>(false)

    init {
        //从本地取得数据绑定到View层
        userName.value = "ww"
        password.value = "123456"
    }

    //清除用户名的点击事件, 逻辑从View层转换到ViewModel层
    var clearUserNameOnClickCommand = BindingCommand<String>(BindingAction { userName.value = "" })
    //密码显示开关  (你可以尝试着狂按这个按钮,会发现它有防多次点击的功能)
    var passwordShowSwitchOnClickCommand: BindingCommand<Boolean> = BindingCommand(BindingAction {
        passwordShowSwitch.value = passwordShowSwitch.value == null || !(passwordShowSwitch.value)!!
    })

    //用户名输入框焦点改变的回调事件
    var onFocusChangeCommand: BindingCommand<Boolean?> =
        BindingCommand(BindingConsumer {
            it?.let {
                if (it) {
                    clearBtnVisibility.value = View.VISIBLE
                } else {
                    clearBtnVisibility.value = View.INVISIBLE
                }
            }
        })
    //登录按钮的点击事件
    var loginOnClickCommand = BindingCommand<Any?>(BindingAction {
        if (TextUtils.isEmpty(userName.value)) {
            return@BindingAction
        }
        if (TextUtils.isEmpty(password.value)) {
            ToastUtils.showShort("请输入密码！")
            return@BindingAction
        }
        showDialog("正在加载中...")

//        startActivity(MainActivity::class.java)
    })
}
