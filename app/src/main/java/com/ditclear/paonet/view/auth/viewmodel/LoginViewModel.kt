package com.ditclear.paonet.view.auth.viewmodel

import android.databinding.Bindable
import android.view.View
import com.ditclear.paonet.BR
import com.ditclear.paonet.lib.extention.getOriginData
import com.ditclear.paonet.model.data.User
import com.ditclear.paonet.model.data.UserModel
import com.ditclear.paonet.model.remote.api.UserService
import com.ditclear.paonet.viewmodel.BaseViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject


/**
 * 页面描述：LoginViewModel
 *
 * Created by ditclear on 2017/10/10.
 */
class LoginViewModel @Inject
constructor(private val repo: UserService) : BaseViewModel() {


    private val PASSWORD_PATTERN = "^[a-zA-Z0-9_]{6,16}$"
    private val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"

    private val emailPattern = Pattern.compile(EMAIL_PATTERN)
    private val passwordPattern = Pattern.compile(PASSWORD_PATTERN)

    var showLogin = true
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.showLogin)
        }

    var email = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }
    var password = ""
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    @Bindable("password", "email")
    fun isBtnEnabled(): Boolean {
        return !(!emailPattern.matcher(email).matches()
                || !passwordPattern.matcher(password).matches())
    }

    var loginVisibility = View.VISIBLE
        @Bindable("showLogin") get () = if (showLogin) View.VISIBLE else View.GONE

    fun attemptToLogIn():Single<User?> {

        return repo.login(email, password)
                .subscribeOn(Schedulers.io())
                .delay(1,TimeUnit.SECONDS)
                .getOriginData()
                .flatMap { repo.myProfile().map { t: UserModel -> t.model } }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showLogin = false }
                .doFinally { showLogin = true }

    }
}