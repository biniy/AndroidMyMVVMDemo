package com.aaron.mymvvm

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.Observer
import com.aaron.mvvmlibrary.base.BaseActivity
import com.aaron.mymvvm.databinding.ActivityLoginBinding

/**
 * 登录页面
 */
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
    override fun initParam() {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        // 全屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_login
    }

    override fun initVariableId(): Int {
        return BR.loginViewModel
    }

    override fun initViewObservable() {
        viewModel.passwordShowSwitch.observe(this, Observer {
            if (it == true) {
                //密码可见
                //在xml中定义id后,使用binding可以直接拿到这个view的引用,不再需要findViewById去找控件了
                binding.ivSwichPasswrod.setImageResource(R.mipmap.ic_login_show_psw)
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                //密码不可见
                binding.ivSwichPasswrod.setImageResource(R.mipmap.ic_login_no_show_psw)
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        })
    }
}
