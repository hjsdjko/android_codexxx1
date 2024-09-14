package com.design.appproject.ui.ruzhuxinxi

import com.design.appproject.logic.repository.UserRepository
import com.qmuiteam.qmui.widget.QMUIRadiusImageView
import android.annotation.SuppressLint
import com.union.union_basic.utils.StorageUtil
import com.design.appproject.utils.ArouterUtils
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ThreadUtils.runOnUiThread
import com.design.appproject.base.*
import com.design.appproject.ext.postEvent
import android.media.MediaPlayer
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import com.google.gson.Gson
import android.view.Gravity
import android.view.ViewGroup
import com.design.appproject.widget.DetailBannerAdapter
import android.widget.*
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.union.union_basic.ext.*
import androidx.core.view.setMargins
import com.alibaba.android.arouter.launcher.ARouter
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.*
import com.union.union_basic.network.DownloadListener
import com.union.union_basic.network.DownloadUtil
import java.io.File
import androidx.core.view.setPadding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.utils.Utils
import java.util.*
import kotlin.concurrent.timerTask
import com.design.appproject.ext.load
import com.design.appproject.logic.viewmodel.ruzhuxinxi.DetailsViewModel
import androidx.activity.viewModels
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.databinding.RuzhuxinxicommonDetailsLayoutBinding
import com.design.appproject.bean.*
import com.design.appproject.ui.CommentsAdatper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.view.isVisible
import android.text.Html
import com.design.appproject.widget.MyTextView
import com.design.appproject.widget.MyFlexBoxLayout
import com.design.appproject.widget.MyImageView
import android.view.ContextThemeWrapper
import com.google.android.flexbox.FlexWrap
import com.union.union_basic.image.loader.GlideLoader.load
/**
 * 入住信息详情页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_RUZHUXINXI)
class DetailsFragment : BaseBindingFragment<RuzhuxinxicommonDetailsLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    private val mDetailsViewModel by viewModels<DetailsViewModel>()

    private var mRuzhuxinxiItemBean=RuzhuxinxiItemBean()/*详情内容*/


    @SuppressLint("SuspiciousIndentation")
    override fun initEvent() {
        setBarTitle("入住信息详情页")
        setBarColor("#C6000F","white")
        binding.apply{
            srv.setOnRefreshListener {
                loadData()
            }
        val tuifangzhuangtaiCrossSelect = "已退房,未退房".split(",")
            mIsBack.yes {
                crossOptButtonBtn0.isVisible = Utils.isAuthBack("ruzhuxinxi","退房")
            }.otherwise {
                crossOptButtonBtn0.isVisible = Utils.isAuthFront("ruzhuxinxi","退房")
            }
            crossOptButtonBtn0.setOnClickListener{/*跨表*/
            if (mRuzhuxinxiItemBean.tuifangzhuangtai=="已退房"){
                "已退房".showToast()
                return@setOnClickListener
            }
            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_TUIFANGXINXI)
                .withString("mCrossTable","ruzhuxinxi")
                .withObject("mCrossObj",mRuzhuxinxiItemBean)
                .withString("mStatusColumnName","tuifangzhuangtai")
                .withString("mStatusColumnValue","已退房")
                .withString("mTips","已退房")
                .navigation()
        }
    }
    }

    override fun initData() {
        super.initData()
        showLoading()
        loadData()
        mDetailsViewModel.infoLiveData.observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let { info->
                binding.srv.isRefreshing =false
                mRuzhuxinxiItemBean = info.data
                 binding.setInfo()
            }
        }
        mDetailsViewModel.updateLiveData.observeKt {
            it.getOrNull()?.let {
            }

        }
    }

    private fun loadData(){
        mDetailsViewModel.info("ruzhuxinxi",mId.toString())
    }


    private fun RuzhuxinxicommonDetailsLayoutBinding.setInfo(){
        yudingbianhaoTv.text = "${mRuzhuxinxiItemBean.yudingbianhao}"
        fangjianhaoTv.text = "${mRuzhuxinxiItemBean.fangjianhao}"
        kefangleixingTv.text = "${mRuzhuxinxiItemBean.kefangleixing}"
        ruzhuriqiTv.text = "${mRuzhuxinxiItemBean.ruzhuriqi}"
        ruzhutianshuTv.text = "${mRuzhuxinxiItemBean.ruzhutianshu}"
        yonghuzhanghaoTv.text = "${mRuzhuxinxiItemBean.yonghuzhanghao}"
        yonghuxingmingTv.text = "${mRuzhuxinxiItemBean.yonghuxingming}"
        shoujihaomaTv.text = "${mRuzhuxinxiItemBean.shoujihaoma}"
        ruzhushijianTv.text = "${mRuzhuxinxiItemBean.ruzhushijian}"
        tuifangzhuangtaiTv.text = "${mRuzhuxinxiItemBean.tuifangzhuangtai}"
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK && requestCode==101){
            loadData()
        }
    }

}