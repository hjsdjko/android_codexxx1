package com.design.appproject.ui.kefangxinxi

import android.Manifest
import com.union.union_basic.permission.PermissionUtil
import com.design.appproject.ext.UrlPrefix
import androidx.core.widget.addTextChangedListener
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.core.view.isVisible
import androidx.core.view.children
import com.design.appproject.utils.Utils
import com.design.appproject.bean.BaiKeBean
import androidx.core.app.ActivityCompat.startActivityForResult
import com.blankj.utilcode.util.UriUtils
import android.content.Intent
import com.alibaba.android.arouter.launcher.ARouter
import com.google.gson.internal.LinkedTreeMap
import com.union.union_basic.ext.*
import com.blankj.utilcode.util.RegexUtils
import com.union.union_basic.utils.StorageUtil
import com.github.gzuliyujiang.wheelpicker.DatimePicker
import com.design.appproject.widget.BottomSpinner
import com.design.appproject.base.CommonBean
import com.blankj.utilcode.util.TimeUtils
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity
import com.github.gzuliyujiang.wheelpicker.impl.BirthdayFormatter
import com.github.gzuliyujiang.wheelpicker.impl.UnitTimeFormatter
import java.text.SimpleDateFormat
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.repository.UserRepository
import com.union.union_basic.image.selector.SmartPictureSelector
import java.io.File
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.bean.KefangxinxiItemBean
import com.design.appproject.databinding.KefangxinxiaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 客房信息新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_KEFANGXINXI)
class AddOrUpdateActivity:BaseBindingActivity<KefangxinxiaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mKefangxinxiItemBean = KefangxinxiItemBean()

    override fun initEvent() {
        setBarTitle("客房信息")
        setBarColor("#C6000F","white")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mKefangxinxiItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mKefangxinxiItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mKefangxinxiItemBean,mRefid)
                }
            }
            if (mKefangxinxiItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mKefangxinxiItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mKefangxinxiItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mKefangxinxiItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mKefangxinxiItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mKefangxinxiItemBean,Utils.getUserId())
            }
        }
        binding.initView()

        binding.fangjianxiangqingRichLayout.apply{
            actionBold.setOnClickListener {
                richEt.setBold()
            }
            actionItalic.setOnClickListener {
                richEt.setItalic()
            }
            actionStrikethrough.setOnClickListener {
                richEt.setStrikeThrough()
            }
            actionUnderline.setOnClickListener {
                richEt.setUnderline()
            }
            actionHeading1.setOnClickListener {
                richEt.setHeading(1)
            }
            actionHeading2.setOnClickListener {
                richEt.setHeading(2)
            }
            actionHeading3.setOnClickListener {
                richEt.setHeading(3)
            }
            actionHeading4.setOnClickListener {
                richEt.setHeading(4)
            }
            actionHeading5.setOnClickListener {
                richEt.setHeading(5)
            }
            actionIndent.setOnClickListener {
                richEt.setIndent()
            }
            actionOutdent.setOnClickListener {
                richEt.setOutdent()
            }
            actionAlignCenter.setOnClickListener {
                richEt.setAlignCenter()
            }
            actionAlignLeft.setOnClickListener {
                richEt.setAlignLeft()
            }
            actionAlignRight.setOnClickListener {
                richEt.setAlignRight()
            }
            actionInsertBullets.setOnClickListener {
                richEt.setBullets()
            }
            actionInsertNumbers.setOnClickListener {
                richEt.setNumbers()
            }
            actionInsertImage.setOnClickListener {
                SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                    val path = it[0]
                    UserRepository.upload(File(path),"").observeKt {
                        it.getOrNull()?.let {
                            richEt.insertImage(UrlPrefix.URL_PREFIX+"file/" + it.file, "dachshund", 320)
                        }
                    }
                }
            }
        }
    }

    fun KefangxinxiaddorupdateLayoutBinding.initView(){
             fangjiantupianLl.setOnClickListener {
            SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                val path = it[0]
                showLoading("上传中...")
                UserRepository.upload(File(path), "fangjiantupian").observeKt{
                    it.getOrNull()?.let {
                        fangjiantupianIfv.load(this@AddOrUpdateActivity, "file/"+it.file)
                        mKefangxinxiItemBean.fangjiantupian = "file/" + it.file
                    }
                }
            }
        }
            kefangleixingBs.let { spinner ->
            spinner.setOnItemSelectedListener(object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    spinner.text = content
                    mKefangxinxiItemBean.kefangleixing =content
                }
            })
            spinner.setOnClickListener {
                spinner.options.isNullOrEmpty().yes {
                    UserRepository.option("kefangleixing", "kefangleixing", "",null,"",false).observeKt{
                        it.getOrNull()?.let {
                            spinner.setOptions(it.data, "请选择客房类型", false)
                            spinner.dialogShow()
                        }
                    }
                }.otherwise {
                    spinner.dialogShow()
                }
            }
        }
            fangjianzhuangtaiBs.setOptions("已预约,未预约".split(","),"请选择房间状态",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    fangjianzhuangtaiBs.text = content
                    mKefangxinxiItemBean.fangjianzhuangtai =content
                }
            })
            submitBtn.setOnClickListener{/*提交*/
                submit()
            }
            setData()
    }

    lateinit var mUserBean:LinkedTreeMap<String, Any>/*当前用户数据*/

    override fun initData() {
        super.initData()
        UserRepository.session<Any>().observeKt {
            it.getOrNull()?.let {
                it.data.toConversion<LinkedTreeMap<String, Any>>()?.let {
                    mUserBean = it
                    it["touxiang"]?.let { it1 -> StorageUtil.encode(CommonBean.HEAD_URL_KEY, it1) }
                    /**ss读取*/
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<KefangxinxiItemBean>("kefangxinxi",mId).observeKt {
                it.getOrNull()?.let {
                    mKefangxinxiItemBean = it.data
                    mKefangxinxiItemBean.id = mId
                    binding.setData()
                }
            }
        }
        mKefangxinxiItemBean.fangjianzhuangtai = "未预约"
        mKefangxinxiItemBean.storeupnum = 0
        mKefangxinxiItemBean.clicknum = 0
        binding.setData()
    }

    /**验证*/
    private fun KefangxinxiaddorupdateLayoutBinding.submit() {
        mKefangxinxiItemBean.fangjianhao = fangjianhaoEt.text.toString()
        jiageEt.inputType = InputType.TYPE_CLASS_NUMBER
        mKefangxinxiItemBean.jiage = jiageEt.text.toString().toDoubleOrNull()?:0.0
        mKefangxinxiItemBean.fangjiansheshi = fangjiansheshiEt.text.toString()
        mKefangxinxiItemBean.fangjianxiangqing = fangjianxiangqingRichLayout.richEt.html
        storeupnumEt.inputType = InputType.TYPE_CLASS_NUMBER
        mKefangxinxiItemBean.storeupnum = storeupnumEt.text.toString().toInt()
        mKefangxinxiItemBean.louceng = loucengEt.text.toString()
        if(mKefangxinxiItemBean.fangjianhao.toString().isNullOrEmpty()){
            "房间号不能为空".showToast()
            return
        }
        if(mKefangxinxiItemBean.kefangleixing.toString().isNullOrEmpty()){
            "客房类型不能为空".showToast()
            return
        }
        if(mKefangxinxiItemBean.jiage<=0){
            "每晚/元不能为空".showToast()
            return
        }
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mKefangxinxiItemBean.id>0){
            UserRepository.update("kefangxinxi",mKefangxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<KefangxinxiItemBean>("kefangxinxi",mKefangxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun KefangxinxiaddorupdateLayoutBinding.setData(){
        if (mKefangxinxiItemBean.fangjianhao.isNotNullOrEmpty()){
            fangjianhaoEt.setText(mKefangxinxiItemBean.fangjianhao.toString())
        }
        if (mKefangxinxiItemBean.fangjiantupian.isNotNullOrEmpty()){
            fangjiantupianIfv.load(this@AddOrUpdateActivity, mKefangxinxiItemBean.fangjiantupian)
        }
        if (mKefangxinxiItemBean.kefangleixing.isNotNullOrEmpty()){
            kefangleixingBs.text =mKefangxinxiItemBean.kefangleixing
        }
        if (mKefangxinxiItemBean.jiage>=0){
            jiageEt.setText(mKefangxinxiItemBean.jiage.toString())
        }
        if (mKefangxinxiItemBean.fangjianzhuangtai.isNotNullOrEmpty()){
            fangjianzhuangtaiBs.text =mKefangxinxiItemBean.fangjianzhuangtai
        }
        if (mKefangxinxiItemBean.storeupnum>=0){
            storeupnumEt.setText(mKefangxinxiItemBean.storeupnum.toString())
        }
        if (mKefangxinxiItemBean.louceng.isNotNullOrEmpty()){
            loucengEt.setText(mKefangxinxiItemBean.louceng.toString())
        }
        if (mKefangxinxiItemBean.fangjiansheshi.isNotNullOrEmpty()){
            fangjiansheshiEt.setText(mKefangxinxiItemBean.fangjiansheshi.toString())
        }
        fangjianxiangqingRichLayout.richEt.setHtml(mKefangxinxiItemBean.fangjianxiangqing)
    }
}