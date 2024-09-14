package com.design.appproject.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.base.CommonBean
import com.design.appproject.bean.*
import com.design.appproject.bean.config.*
import com.design.appproject.bean.news.*
import com.design.appproject.databinding.FragmentIndexLayoutBinding
import com.design.appproject.ext.load
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.viewmodel.HomeViewModel
import com.design.appproject.utils.ArouterUtils
import com.design.appproject.utils.Utils
import com.design.appproject.widget.BottomSpinner
import com.youth.banner.indicator.CircleIndicator
import android.text.Html
import androidx.core.view.isVisible
import android.view.LayoutInflater
import com.design.appproject.widget.MyFlexBoxLayout
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.union.union_basic.ext.*
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.Banner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.Color
import com.design.appproject.base.EventBus
import com.design.appproject.ext.observeEvent
import com.lxj.xpopup.XPopup
/**
 * 首页fragment
 * */
@Route(path = CommonArouteApi.PATH_FRAGMENT_INDEX)
class IndexFragment : BaseBindingFragment<FragmentIndexLayoutBinding>() {

    private val mHomeViewModel by viewModels<HomeViewModel>()

    override fun initEvent() {
        binding.apply {
            homeSrl.setOnRefreshListener {
                initHomeView()
                GlobalScope.launch {
                    delay(2000) /*延时2秒*/
                    homeSrl.isRefreshing =false
                }
            }
            initHomeView()
            observeEvent<Boolean>(EventBus.LOGIN_SUCCESS){
                initHomeView()
            }
        }
    }

    private fun FragmentIndexLayoutBinding.initHomeView(){/*初始化首页内容*/
        initBanner()
        initMenu()
        initNews()
        initRecommendView()
    }

    private fun FragmentIndexLayoutBinding.initRecommendView() { /*商品推荐初始化*/
        val map = mapOf("page" to "1", "limit" to "6")
    if (Utils.isLogin()) {
        HomeRepository.autoSort2<KefangxinxiItemBean>("kefangxinxi", map).observeKt {
            it.getOrNull()?.let {
                kefangxinxiGl.removeAllViews()
                kefangxinxiGl.addView(LayoutInflater.from(context).inflate(R.layout.kefangxinxi_item_recommend_layout,kefangxinxiGl,false).apply{
                var recommendList = it.data.list
                if (recommendList.size>0){
                    findViewById<TextView>(R.id.fangjianhao_top_title_tv).text = "房间号:"+ recommendList[0].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_top_img_iv).load(context,recommendList[0].fangjiantupian.split(",")[0], needPrefix = !(recommendList[0].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_top_title_tv).text = "每晚/元:"+ recommendList[0].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_top_title_tv).text = "房间状态:"+ recommendList[0].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_top_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_top_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[0].id))
                    }
                }
                if (recommendList.size>1){
                    findViewById<TextView>(R.id.fangjianhao_center_left_top_title_tv).text = "房间号:"+ recommendList[1].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_left_top_iv).load(context,recommendList[1].fangjiantupian.split(",")[0], needPrefix = !(recommendList[1].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_left_top_title_tv).text = "每晚/元:"+ recommendList[1].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_left_top_title_tv).text = "房间状态:"+ recommendList[1].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_left_top_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_left_top_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[1].id))
                    }
                }
                if (recommendList.size>2){
                    findViewById<TextView>(R.id.fangjianhao_center_left_bottom_title_tv).text = "房间号:"+ recommendList[2].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_left_bottom_iv).load(context,recommendList[2].fangjiantupian.split(",")[0], needPrefix = !(recommendList[2].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_left_bottom_title_tv).text = "每晚/元:"+ recommendList[2].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_left_bottom_title_tv).text = "房间状态:"+ recommendList[2].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_left_bottom_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_left_bottom_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[2].id))
                    }
                }
                if (recommendList.size>3){
                    findViewById<TextView>(R.id.fangjianhao_center_right_top_title_tv).text = "房间号:"+ recommendList[3].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_right_top_img_iv).load(context,recommendList[3].fangjiantupian.split(",")[0], needPrefix = !(recommendList[3].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_right_top_title_tv).text = "每晚/元:"+ recommendList[3].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_right_top_title_tv).text = "房间状态:"+ recommendList[3].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_right_top_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_right_top_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[3].id))
                    }
                }
                if (recommendList.size>4){
                    findViewById<TextView>(R.id.fangjianhao_center_right_bottom_title_tv).text = "房间号:"+ recommendList[4].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_right_bottom_img_iv).load(context,recommendList[4].fangjiantupian.split(",")[0], needPrefix = !(recommendList[4].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_right_bottom_title_tv).text = "每晚/元:"+ recommendList[4].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_right_bottom_title_tv).text = "房间状态:"+ recommendList[4].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_right_bottom_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_right_bottom_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[4].id))
                    }
                }
                if (recommendList.size>5){
                    findViewById<TextView>(R.id.fangjianhao_bottom_title_tv).text = "房间号:"+ recommendList[5].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_bottom_img_iv).load(context,recommendList[5].fangjiantupian.split(",")[0], needPrefix = !(recommendList[5].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_bottom_title_tv).text = "每晚/元:"+ recommendList[5].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_bottom_title_tv).text = "房间状态:"+ recommendList[5].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_bottom_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_bottom_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[5].id))
                    }
                }
            })
            }
        }
    } else {
        HomeRepository.autoSort<KefangxinxiItemBean>("kefangxinxi", map).observeKt {
            it.getOrNull()?.let {
                kefangxinxiGl.removeAllViews()
                kefangxinxiGl.addView(LayoutInflater.from(context).inflate(R.layout.kefangxinxi_item_recommend_layout,kefangxinxiGl,false).apply{
                var recommendList = it.data.list
                if (recommendList.size>0){
                    findViewById<TextView>(R.id.fangjianhao_top_title_tv).text = "房间号:"+ recommendList[0].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_top_img_iv).load(context,recommendList[0].fangjiantupian.split(",")[0], needPrefix = !(recommendList[0].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_top_title_tv).text = "每晚/元:"+ recommendList[0].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_top_title_tv).text = "房间状态:"+ recommendList[0].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_top_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_top_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[0].id))
                    }
                }
                if (recommendList.size>1){
                    findViewById<TextView>(R.id.fangjianhao_center_left_top_title_tv).text = "房间号:"+ recommendList[1].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_left_top_iv).load(context,recommendList[1].fangjiantupian.split(",")[0], needPrefix = !(recommendList[1].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_left_top_title_tv).text = "每晚/元:"+ recommendList[1].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_left_top_title_tv).text = "房间状态:"+ recommendList[1].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_left_top_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_left_top_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[1].id))
                    }
                }
                if (recommendList.size>2){
                    findViewById<TextView>(R.id.fangjianhao_center_left_bottom_title_tv).text = "房间号:"+ recommendList[2].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_left_bottom_iv).load(context,recommendList[2].fangjiantupian.split(",")[0], needPrefix = !(recommendList[2].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_left_bottom_title_tv).text = "每晚/元:"+ recommendList[2].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_left_bottom_title_tv).text = "房间状态:"+ recommendList[2].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_left_bottom_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_left_bottom_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[2].id))
                    }
                }
                if (recommendList.size>3){
                    findViewById<TextView>(R.id.fangjianhao_center_right_top_title_tv).text = "房间号:"+ recommendList[3].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_right_top_img_iv).load(context,recommendList[3].fangjiantupian.split(",")[0], needPrefix = !(recommendList[3].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_right_top_title_tv).text = "每晚/元:"+ recommendList[3].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_right_top_title_tv).text = "房间状态:"+ recommendList[3].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_right_top_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_right_top_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[3].id))
                    }
                }
                if (recommendList.size>4){
                    findViewById<TextView>(R.id.fangjianhao_center_right_bottom_title_tv).text = "房间号:"+ recommendList[4].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_center_right_bottom_img_iv).load(context,recommendList[4].fangjiantupian.split(",")[0], needPrefix = !(recommendList[4].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_center_right_bottom_title_tv).text = "每晚/元:"+ recommendList[4].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_center_right_bottom_title_tv).text = "房间状态:"+ recommendList[4].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_right_bottom_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_right_bottom_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[4].id))
                    }
                }
                if (recommendList.size>5){
                    findViewById<TextView>(R.id.fangjianhao_bottom_title_tv).text = "房间号:"+ recommendList[5].fangjianhao.toString()
                    findViewById<ImageView>(R.id.fangjiantupian_bottom_img_iv).load(context,recommendList[5].fangjiantupian.split(",")[0], needPrefix = !(recommendList[5].fangjiantupian.startsWith("http")))
                    findViewById<TextView>(R.id.jiage_bottom_title_tv).text = "每晚/元:"+ recommendList[5].jiage.toString()
                    findViewById<TextView>(R.id.fangjianzhuangtai_bottom_title_tv).text = "房间状态:"+ recommendList[5].fangjianzhuangtai.toString()
                    findViewById<ViewGroup>(R.id.rec_center_bottom_fbl).isVisible =true
                    findViewById<ViewGroup>(R.id.rec_center_bottom_fbl).setOnClickListener {
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map = mapOf("mId" to recommendList[5].id))
                    }
                }
            })
            }
        }
    }
}

    private fun FragmentIndexLayoutBinding.initNews() {/*新闻咨询*/
        newsMoreTv.setOnClickListener {/*查看更多*/
            ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_LIST_NEWS)
        }
        mHomeViewModel.newsList(mapOf("page" to "1", "limit" to "6"))
        mHomeViewModel.newsListLiveData.observeKt {
            it.getOrNull()?.let {
                newsLl.removeAllViews()
                it.data.list.forEachIndexed { index, newsListBean ->
                    newsLl.addView(creatNewItemView(newsListBean))
                }
            }
        }
    }

    private fun creatNewItemView(newsListBean: NewsItemBean) =LayoutInflater.from(context).inflate(R.layout.item_news_layout,binding.newsLl,false).apply{
        findViewById<TextView>(R.id.news_title_tv).text = newsListBean.title
        findViewById<TextView>(R.id.news_category_tv).text = newsListBean.introduction
        findViewById<TextView>(R.id.news_time_tv).text = newsListBean.addtime?.split(" ")?.get(0)?:""
        setOnClickListener {
            ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_NEWS, map = mapOf("mId" to newsListBean.id))
        }
    }
    /**轮播图*/
    private fun FragmentIndexLayoutBinding.initBanner() {
        HomeRepository.list<ConfigItemBean>("config", mapOf("page" to "1", "limit" to "3")).observeKt{
            it.getOrNull()?.let {
                banner.setAdapter(object :
                    BannerImageAdapter<ConfigItemBean>(it.data.list.filter { it.name.contains("swiper") }) {
                    override fun onBindView(
                        holder: BannerImageHolder,
                        data: ConfigItemBean,
                        position: Int,
                        size: Int
                    ) {
                        activity?.let { holder.imageView.load(it, data.value.split(",")[0], radius = 5.dp) }
                    }
                }).setOnBannerListener { data, position ->
                    data.toConversion<ConfigItemBean>()?.let {
                        it.name.showToast()
                    }
                }
            }
        }
    }
    /**菜单*/
    private fun FragmentIndexLayoutBinding.initMenu() {
        val menuList = mutableListOf<MenuBean>()
        menuGl.removeAllViews()
            roleMenusList.filter { it.tableName == CommonBean.tableName }.forEach {/*筛选可查看的菜单*/
                it.frontMenu.forEach {
                    val menuBean = MenuBean(
                        child = it.child.filter {child->child.buttons.contains("查看")},
                        menu = it.menu?:"", fontClass = it.fontClass?:"", unicode = it.unicode?:"")
                    menuList.add(menuBean)
                }
            }
        menuList.forEachIndexed { index, menu ->
            if (menu.child.size>0 && !listOf("yifahuodingdan","yituikuandingdan","yiquxiaodingdan","weizhifudingdan","yizhifudingdan","yiwanchengdingdan").contains(menu.child[0].tableName)){
                val itemView = creatMenuItemView(menu)
                menuGl.addView(itemView)
            }
        }
    }

    private fun creatMenuItemView(menu: MenuBean) = LayoutInflater.from(context).inflate(R.layout.item_index_menu_layout,binding.menuGl,false).apply{
        findViewById<TextView>(R.id.menu_title_tv).text = menu.menu.split("列表")[0]
        findViewById<TextView>(R.id.menu_icon_tv).text = Html.fromHtml(menu.unicode?:"")
        setOnClickListener {
            if (menu.child.size>1){//二级菜单
                XPopup.Builder(requireActivity()).asBottomList("",
                    menu.child.map {it.menu}.toTypedArray()
                ) { position, text ->
                    ArouterUtils.startFragment("/ui/fragment/${menu.child[position].tableName}/list")
                }.show()
            }else{// 一级菜单
                ArouterUtils.startFragment("/ui/fragment/${menu.child[0].tableName}/list")
            }
        }
    }

}