package com.ditclear.paonet.helper.binds

import android.databinding.BindingAdapter
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import com.ditclear.paonet.R
import com.ditclear.paonet.helper.ImageUtil
import com.ditclear.paonet.helper.ScrimUtil
import com.ditclear.paonet.helper.extens.setMarkdown
import com.ditclear.paonet.view.base.Presenter
import com.ditclear.paonet.viewmodel.PagedViewModel
import us.feras.mdv.MarkdownView

/**
 * 页面描述：normal bind class
 *
 * Created by ditclear on 2017/10/2.
 */

@BindingAdapter(value = ["url", "avatar"], requireAll = false)
fun bindUrl(imageView: ImageView, url: String?, isAvatar: Boolean?) {

    ImageUtil.load(url, imageView, isAvatar = isAvatar ?: false)
}

@BindingAdapter(value = ["start_color", "icon"], requireAll = false)
fun bindTransitionArgs(v: View, color: Int, icon: Int?) {
    v.setTag(R.integer.start_color, color)
    if (v is FloatingActionButton) {
        icon?.let { v.setTag(R.integer.fab_icon, icon) }
    }
}

@BindingAdapter(value = ["markdown"])
fun bindMarkDown(v: MarkdownView, markdown: String?) {
    markdown?.let {
        v.setMarkdown(markdown)
    }
}

@BindingAdapter(value = ["visible"])
fun bindVisibility(v: View, visible: Boolean) {
    v.visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["loadMore","loadMorePresenter"])
fun bindLoadMore(v: RecyclerView, vm: PagedViewModel?,presenter: Presenter) {
    v.layoutManager = LinearLayoutManager(v.context)
    v.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (recyclerView.layoutManager is LinearLayoutManager) {
                //表示是否能向上滚动，false表示已经滚动到底部
                //防止多次拉取同样的数据
                if (!recyclerView.canScrollVertically(1)) {
                    vm?.let {
                        if (vm.loadMore.get() && !vm.loading.get()) {
                            presenter.loadData(false)
                        }
                    }
                }
            }
        }
    })
}

@BindingAdapter(value = ["onRefresh"])
fun bindOnRefresh(v: SwipeRefreshLayout, presenter: Presenter) {
    v.setOnRefreshListener { presenter.loadData(true) }
}

@BindingAdapter(value = ["vertical"], requireAll = false)
fun bindSlider(v: RecyclerView, vertical: Boolean = true) {

    if (vertical) {
        v.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
    } else {
        if (v.onFlingListener == null) {
            PagerSnapHelper().attachToRecyclerView(v)
        }
        v.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL, false)
    }
}

//渐变式阴影
@BindingAdapter(value = ["shadow_color", "num_step", "gravity"], requireAll = false)
fun setShadow(view: View, mColor: Int, mNumSteps: Int, mGravity: Int) {
    var color = mColor
    var numSteps = mNumSteps
    var gravity = mGravity
    if (color == 0) {
        color = ContextCompat.getColor(view.context, R.color.shadow)
    }
    if (numSteps == 0) {
        numSteps = 6
    }
    if (gravity == 0) {
        gravity = Gravity.TOP
    }
    view.background = ScrimUtil.makeCubicGradientScrimDrawable(color, numSteps,
            gravity)

}