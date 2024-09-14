package com.design.appproject.ui.genghuanfangjian

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
import com.design.appproject.logic.viewmodel.genghuanfangjian.DetailsViewModel
import androidx.activity.viewModels
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.databinding.GenghuanfangjiancommonDetailsLayoutBinding
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
 * 更换房间详情页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_GENGHUANFANGJIAN)
class DetailsFragment : BaseBindingFragment<GenghuanfangjiancommonDetailsLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    private val mDetailsViewModel by viewModels<DetailsViewModel>()

    private var mGenghuanfangjianItemBean=GenghuanfangjianItemBean()/*详情内容*/


    @SuppressLint("SuspiciousIndentation")
    override fun initEvent() {
        setBarTitle("更换房间详情页")
        setBarColor("#C6000F","white")
        binding.apply{
            srv.setOnRefreshListener {
                loadData()
            }
            mIsBack.yes {
                sfshBtn.isVisible = Utils.isAuthBack("genghuanfangjian","审核")
            }.otherwise {
                sfshBtn.isVisible = Utils.isAuthFront("genghuanfangjian","审核")
            }
            sfshBtn.setOnClickListener {
                XPopup.Builder(context).asInputConfirm("",""){
                    if (mGenghuanfangjianItemBean.sfsh.isNullOrEmpty()){
                        "请选择审核状态".showToast()
                        return@asInputConfirm
                    }
                    when(it){
                        "通过"-> mGenghuanfangjianItemBean.sfsh = "是"
                        "不通过"->mGenghuanfangjianItemBean.sfsh = "否"
                        else->mGenghuanfangjianItemBean.sfsh = it
                    }
                    if (mGenghuanfangjianItemBean.sfsh.isNullOrEmpty()){
                        "请填写审核回复".showToast()
                    }else{
                        mDetailsViewModel.update("genghuanfangjian",mGenghuanfangjianItemBean,"shhf")
                    }
                }.show()
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
                mGenghuanfangjianItemBean = info.data
                 binding.setInfo()
            }
        }
        mDetailsViewModel.updateLiveData.observeKt {
            it.getOrNull()?.let {
                if (it.callBackData=="shhf"){
                    "审核成功".showToast()
                }
            }

        }
    }

    private fun loadData(){
        mDetailsViewModel.info("genghuanfangjian",mId.toString())
    }


    private fun GenghuanfangjiancommonDetailsLayoutBinding.setInfo(){
        yudingbianhaoTv.text = "${mGenghuanfangjianItemBean.yudingbianhao}"
        fangjianhaoTv.text = "${mGenghuanfangjianItemBean.fangjianhao}"
        kefangleixingTv.text = "${mGenghuanfangjianItemBean.kefangleixing}"
        jiageTv.text = "${mGenghuanfangjianItemBean.jiage}"
        yonghuzhanghaoTv.text = "${mGenghuanfangjianItemBean.yonghuzhanghao}"
        yonghuxingmingTv.text = "${mGenghuanfangjianItemBean.yonghuxingming}"
        shoujihaomaTv.text = "${mGenghuanfangjianItemBean.shoujihaoma}"
        loucengTv.text = "${mGenghuanfangjianItemBean.louceng}"
        genghuanyuanyinTv.text = "${mGenghuanfangjianItemBean.genghuanyuanyin}"
        xinfanghaoTv.text = "${mGenghuanfangjianItemBean.xinfanghao}"
        var sfshStatus = if(mGenghuanfangjianItemBean.sfsh =="是"){
            "通过"
        }else if(mGenghuanfangjianItemBean.sfsh =="否"){
            "不通过"
        }else{
            "待审核"
        }
        sfshTv.isVisible = mIsBack
        sfshFbl.isVisible = mIsBack
        sfshContentTv.isVisible = mIsBack
        sfshContentFbl.isVisible = mIsBack
        sfshTv.text = "${sfshStatus}"
        sfshContentTv.text =mGenghuanfangjianItemBean.shhf
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK && requestCode==101){
            loadData()
        }
    }

}