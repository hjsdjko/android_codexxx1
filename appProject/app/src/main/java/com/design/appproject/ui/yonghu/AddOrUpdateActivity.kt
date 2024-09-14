package com.design.appproject.ui.yonghu

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
import com.design.appproject.bean.YonghuItemBean
import com.design.appproject.databinding.YonghuaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 用户新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_YONGHU)
class AddOrUpdateActivity:BaseBindingActivity<YonghuaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mYonghuItemBean = YonghuItemBean()

    override fun initEvent() {
        setBarTitle("用户")
        setBarColor("#C6000F","white")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mYonghuItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mYonghuItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mYonghuItemBean,mRefid)
                }
            }
            if (mYonghuItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mYonghuItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mYonghuItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mYonghuItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mYonghuItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mYonghuItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun YonghuaddorupdateLayoutBinding.initView(){
             touxiangLl.setOnClickListener {
            SmartPictureSelector.openPicture(this@AddOrUpdateActivity) {
                val path = it[0]
                showLoading("上传中...")
                UserRepository.upload(File(path), "touxiang").observeKt{
                    it.getOrNull()?.let {
                        touxiangIfv.load(this@AddOrUpdateActivity, "file/"+it.file)
                        mYonghuItemBean.touxiang = "file/" + it.file
                    }
                }
            }
        }
            xingbieBs.setOptions("男,女".split(","),"请选择性别",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    xingbieBs.text = content
                    mYonghuItemBean.xingbie =content
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
            HomeRepository.info<YonghuItemBean>("yonghu",mId).observeKt {
                it.getOrNull()?.let {
                    mYonghuItemBean = it.data
                    mYonghuItemBean.id = mId
                    binding.setData()
                }
            }
        }
        binding.setData()
    }

    /**验证*/
    private fun YonghuaddorupdateLayoutBinding.submit() {
        mYonghuItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mYonghuItemBean.yonghumima = yonghumimaEt.text.toString()
        mYonghuItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        mYonghuItemBean.shoujihaoma = shoujihaomaEt.text.toString()
        if(mYonghuItemBean.yonghuzhanghao.toString().isNullOrEmpty()){
            "用户账号不能为空".showToast()
            return
        }
        if(mYonghuItemBean.yonghumima.toString().isNullOrEmpty()){
            "用户密码不能为空".showToast()
            return
        }
        if(mYonghuItemBean.yonghuxingming.toString().isNullOrEmpty()){
            "用户姓名不能为空".showToast()
            return
        }
        RegexUtils.isMobileExact(mYonghuItemBean.shoujihaoma).no {
            "手机号码应输入手机格式".showToast()
            return
        }
        addOrUpdate()

}
    private fun addOrUpdate(){/*更新或添加*/
        if (mYonghuItemBean.id>0){
            UserRepository.update("yonghu",mYonghuItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<YonghuItemBean>("yonghu",mYonghuItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun YonghuaddorupdateLayoutBinding.setData(){
        if (mYonghuItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mYonghuItemBean.yonghuzhanghao.toString())
        }
        if (mYonghuItemBean.yonghumima.isNotNullOrEmpty()){
            yonghumimaEt.setText(mYonghuItemBean.yonghumima.toString())
        }
        if (mYonghuItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mYonghuItemBean.yonghuxingming.toString())
        }
        if (mYonghuItemBean.touxiang.isNotNullOrEmpty()){
            touxiangIfv.load(this@AddOrUpdateActivity, mYonghuItemBean.touxiang)
        }
        if (mYonghuItemBean.xingbie.isNotNullOrEmpty()){
            xingbieBs.text =mYonghuItemBean.xingbie
        }
        if (mYonghuItemBean.shoujihaoma.isNotNullOrEmpty()){
            shoujihaomaEt.setText(mYonghuItemBean.shoujihaoma.toString())
        }
    }
}