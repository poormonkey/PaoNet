package com.ditclear.paonet.view.mine

import android.content.Context
import android.databinding.ObservableBoolean
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import com.ditclear.paonet.R
import com.ditclear.paonet.aop.annotation.SingleClick
import com.ditclear.paonet.databinding.RefreshFragmentBinding
import com.ditclear.paonet.di.scope.FragmentScope
import com.ditclear.paonet.lib.extention.dpToPx
import com.ditclear.paonet.lib.extention.navigateToActivity
import com.ditclear.paonet.model.data.Article
import com.ditclear.paonet.vendor.recyclerview.ItemClickPresenter
import com.ditclear.paonet.view.BaseFragment
import com.ditclear.paonet.view.MainActivity
import com.ditclear.paonet.view.article.ArticleDetailActivity
import com.ditclear.paonet.view.article.PagedAdapter
import com.ditclear.paonet.view.helper.ListPresenter
import com.ditclear.paonet.view.mine.viewmodel.MyArticleViewModel
import javax.inject.Inject

/**
 * 页面描述：MyArticleActivity
 *
 * Created by ditclear on 2017/10/15.
 */
@FragmentScope
class MyArticleFragment :BaseFragment<RefreshFragmentBinding>(), ItemClickPresenter<Article> , ListPresenter {
    override val loadMore: ObservableBoolean
        get() = viewModel.loadMore


    @Inject
    lateinit var viewModel: MyArticleViewModel

    lateinit var mAdapter : PagedAdapter<Article>

    var showTab:Boolean=false

    override fun getLayoutId(): Int = R.layout.refresh_fragment

    companion object {

        private val SHOW_TAB="show_tab"

        fun newInstance(showTab:Boolean=false):MyArticleFragment{
            val bundle=Bundle()
            bundle.putBoolean(SHOW_TAB,showTab)
            val fragment=MyArticleFragment()
            fragment.arguments=bundle
            return fragment
        }
    }



    override fun loadData(isRefresh: Boolean) {
        viewModel.loadData(isRefresh).compose(bindToLifecycle())
                .doOnError { t: Throwable? -> t?.let { toastFailure(it) } }.subscribe()
    }
    override fun initArgs(savedInstanceState: Bundle?) {
        showTab=arguments.getBoolean(SHOW_TAB,false)
    }

    @SingleClick
    override fun onItemClick(v:View?,t: Article) {
        activity.navigateToActivity(ArticleDetailActivity::class.java,t)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)

    }

    override fun initView() {
        (activity as MainActivity).needShowTab(showTab)

        mAdapter= PagedAdapter<Article>(activity, R.layout.article_list_item,viewModel.obserableList
                ,object : DiffCallback<Article>(){
            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.id==newItem.id
            }

            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.id==newItem.id
            }

        }).apply { presenter=this@MyArticleFragment }
        mBinding.vm=viewModel
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.addItemDecoration(object : DividerItemDecoration(activity, DividerItemDecoration.VERTICAL){
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect?.top=activity.dpToPx(R.dimen.xdp_12_0)
            }})
        show()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden){
            show()
        }
    }
    fun show(){
        (activity as MainActivity).needShowTab(false)
    }

}