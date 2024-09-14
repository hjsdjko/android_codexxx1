package com.design.appproject.ui.kefangyuding

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
import com.design.appproject.logic.viewmodel.kefangyuding.DetailsViewModel
import androidx.activity.viewModels
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.databinding.KefangyudingcommonDetailsLayoutBinding
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
 * 客房预订详情页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGYUDING)
class DetailsFragment : BaseBindingFragment<KefangyudingcommonDetailsLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0 /*id*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    private val mDetailsViewModel by viewModels<DetailsViewModel>()

    private var mKefangyudingItemBean=KefangyudingItemBean()/*详情内容*/


    @SuppressLint("SuspiciousIndentation")
    override fun initEvent() {
        setBarTitle("客房预订详情页")
        setBarColor("#C6000F","white")
        binding.apply{
            srv.setOnRefreshListener {
                loadData()
            }
            mIsBack.yes {
                sfshBtn.isVisible = Utils.isAuthBack("kefangyuding","审核")
            }.otherwise {
                sfshBtn.isVisible = Utils.isAuthFront("kefangyuding","审核")
            }
            sfshBtn.setOnClickListener {
                XPopup.Builder(context).asInputConfirm("",""){
                    if (mKefangyudingItemBean.sfsh.isNullOrEmpty()){
                        "请选择审核状态".showToast()
                        return@asInputConfirm
                    }
                    when(it){
                        "通过"-> mKefangyudingItemBean.sfsh = "是"
                        "不通过"->mKefangyudingItemBean.sfsh = "否"
                        else->mKefangyudingItemBean.sfsh = it
                    }
                    if (mKefangyudingItemBean.sfsh.isNullOrEmpty()){
                        "请填写审核回复".showToast()
                    }else{
                        mDetailsViewModel.update("kefangyuding",mKefangyudingItemBean,"shhf")
                    }
                }.show()
            }
        val yuyuezhuangtaiCrossSelect = "已入住,未入住".split(",")
            mIsBack.yes {
                crossOptButtonBtn0.isVisible = Utils.isAuthBack("kefangyuding","入住")
            }.otherwise {
                crossOptButtonBtn0.isVisible = Utils.isAuthFront("kefangyuding","入住")
            }
            crossOptButtonBtn0.setOnClickListener{/*跨表*/
            if (mKefangyudingItemBean.sfsh!="是"){
                "请审核通过后再操作".showToast()
                return@setOnClickListener
            }
            if (mKefangyudingItemBean.yuyuezhuangtai=="已入住"){
                "已入住".showToast()
                return@setOnClickListener
            }
            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_RUZHUXINXI)
                .withString("mCrossTable","kefangyuding")
                .withObject("mCrossObj",mKefangyudingItemBean)
                .withString("mStatusColumnName","yuyuezhuangtai")
                .withString("mStatusColumnValue","已入住")
                .withString("mTips","已入住")
                .navigation()
        }
            mIsBack.yes {
                crossOptButtonBtn1.isVisible = Utils.isAuthBack("kefangyuding","更换房间")
            }.otherwise {
                crossOptButtonBtn1.isVisible = Utils.isAuthFront("kefangyuding","更换房间")
            }
            crossOptButtonBtn1.setOnClickListener{/*跨表*/
            if (mKefangyudingItemBean.sfsh!="是"){
                "请审核通过后再操作".showToast()
                return@setOnClickListener
            }
            ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_GENGHUANFANGJIAN)
                .withString("mCrossTable","kefangyuding")
                .withObject("mCrossObj",mKefangyudingItemBean)
                .withString("mStatusColumnName","")
                .withString("mStatusColumnValue","")
                .withString("mTips","")
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
                mKefangyudingItemBean = info.data
                 binding.setInfo()
                binding.payBtn.text = if(info.data.ispay != "已支付") "支付" else info.data.ispay
                binding.payBtn.setOnClickListener {
                    if (info.data.ispay != "已支付"){
                        ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_PAY_CONFIRM_KEFANGYUDING,mapOf("paytable" to "kefangyuding","payObject" to info.data),requireActivity(),101)
                    }
                }
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
        mDetailsViewModel.info("kefangyuding",mId.toString())
    }


    private fun KefangyudingcommonDetailsLayoutBinding.setInfo(){
        yudingbianhaoTv.text = "${mKefangyudingItemBean.yudingbianhao}"
        fangjianhaoTv.text = "${mKefangyudingItemBean.fangjianhao}"
        kefangleixingTv.text = "${mKefangyudingItemBean.kefangleixing}"
        jiageTv.text = "${mKefangyudingItemBean.jiage}"
        ruzhutianshuTv.text = "${mKefangyudingItemBean.ruzhutianshu}"
        zongjiageTv.text = "${mKefangyudingItemBean.zongjiage}"
        yuyuezhuangtaiTv.text = "${mKefangyudingItemBean.yuyuezhuangtai}"
        ruzhuriqiTv.text = "${mKefangyudingItemBean.ruzhuriqi}"
        yuyueshijianTv.text = "${mKefangyudingItemBean.yuyueshijian}"
        yonghuzhanghaoTv.text = "${mKefangyudingItemBean.yonghuzhanghao}"
        yonghuxingmingTv.text = "${mKefangyudingItemBean.yonghuxingming}"
        shoujihaomaTv.text = "${mKefangyudingItemBean.shoujihaoma}"
        loucengTv.text = "${mKefangyudingItemBean.louceng}"
        var sfshStatus = if(mKefangyudingItemBean.sfsh =="是"){
            "通过"
        }else if(mKefangyudingItemBean.sfsh =="否"){
            "不通过"
        }else{
            "待审核"
        }
        sfshTv.isVisible = mIsBack
        sfshFbl.isVisible = mIsBack
        sfshContentTv.isVisible = mIsBack
        sfshContentFbl.isVisible = mIsBack
        sfshTv.text = "${sfshStatus}"
        sfshContentTv.text =mKefangyudingItemBean.shhf
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== AppCompatActivity.RESULT_OK && requestCode==101){
            loadData()
        }
    }

}