package com.ditclear.paonet.view.mine.viewmodel

import android.databinding.ObservableArrayList
import com.ditclear.paonet.di.scope.FragmentScope
import com.ditclear.paonet.lib.extention.async
import com.ditclear.paonet.model.data.Article
import com.ditclear.paonet.model.remote.api.UserService
import com.ditclear.paonet.viewmodel.PagedViewModel
import javax.inject.Inject

/**
 * 页面描述：MyArticleViewModel
 *
 * Created by ditclear on 2017/10/3.
 */
@FragmentScope
class CodeListViewModel
@Inject
constructor( private val repo: UserService) : PagedViewModel() {

    val obserableList = ObservableArrayList<Article>()

    fun loadData(isRefresh: Boolean) =
        repo.collectionArticle(getPage(isRefresh),-19).async(1000)
                .map { articleList ->
                    with(articleList) {
                        if (isRefresh) {
                            obserableList.clear()
                        }
                        loadMore.set(!incomplete_results)
                        return@map items?.let { obserableList.addAll(it) }
                    }
                }.doOnSubscribe { startLoad() }.doFinally { stopLoad() }

}