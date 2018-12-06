package com.ditclear.paonet.view.article.viewmodel

import android.arch.lifecycle.MutableLiveData
import com.ditclear.paonet.helper.Constants
import com.ditclear.paonet.helper.SpUtil
import com.ditclear.paonet.helper.Utils
import com.ditclear.paonet.helper.extens.async
import com.ditclear.paonet.helper.extens.getOriginData
import com.ditclear.paonet.helper.extens.init
import com.ditclear.paonet.helper.extens.set
import com.ditclear.paonet.model.data.Article
import com.ditclear.paonet.model.data.BaseResponse
import com.ditclear.paonet.model.repository.PaoRepository
import com.ditclear.paonet.model.repository.UserRepository
import com.ditclear.paonet.viewmodel.BaseViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * 页面描述：ArticleDetailViewModel
 *
 * Created by ditclear on 2017/10/3.
 */
class  ArticleDetailViewModel @Inject
constructor(private val repo: PaoRepository, private val userRepo: UserRepository) : BaseViewModel() {

    val loading = MutableLiveData<Boolean>().init(true)

    val markdown = MutableLiveData<String>()

    lateinit var article: Article
        set

    //加载详情
    fun loadData() = repo.getArticle(article.id).subscribeOn(Schedulers.io())
            .doOnSuccess {
                it?.content?.let {
                    val data = Utils.processImgSrc(it, Constants.HOST_PAO)
                    markdown.set(data)
                }
                loading.set(false)
            }.flatMap {
        if (SpUtil.user == null) {
            return@flatMap Single.just(false)
        } else {
            return@flatMap userRepo.isStow(article.id).getOriginData()
                    .map { t: BaseResponse? -> t?.data?.contentEquals("1") }
        }
    }.observeOn(AndroidSchedulers.mainThread())


    //关注
    fun attentionTo()=Single.create<Int> {submit->
        article.user?.id?.let {
            submit.onSuccess(it)
        }?:submit.onError(Throwable("用户不存在"))
    }.flatMap { userRepo.followUser(it) }.getOriginData().async()



    //收藏
    fun stow() = userRepo.stow(article.id).getOriginData().async()


}
