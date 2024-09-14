package com.design.appproject.ui.genghuanfangjian

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
import com.design.appproject.bean.GenghuanfangjianItemBean
import com.design.appproject.ext.afterTextChanged
import com.design.appproject.bean.KefangyudingItemBean
import com.design.appproject.databinding.GenghuanfangjianaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 更换房间新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_GENGHUANFANGJIAN)
class AddOrUpdateActivity:BaseBindingActivity<GenghuanfangjianaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mCrossTable: String = "" /*跨表表名*/

    @JvmField
    @Autowired
    var mCrossObj: KefangyudingItemBean = KefangyudingItemBean() /*跨表表内容*/

    @JvmField
    @Autowired
    var mStatusColumnName: String = "" /*列名*/

    @JvmField
    @Autowired
    var mStatusColumnValue: String = "" /*列值*/

    @JvmField
    @Autowired
    var mTips: String = "" /*提示*/
    @JvmField
    @Autowired
    var mRefid: Long = 0 /*refid数据*/

    /**上传数据*/
    var mGenghuanfangjianItemBean = GenghuanfangjianItemBean()

    override fun initEvent() {
        setBarTitle("更换房间")
        setBarColor("#C6000F","white")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mGenghuanfangjianItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mGenghuanfangjianItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mGenghuanfangjianItemBean,mRefid)
                }
            }
            if (mGenghuanfangjianItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mGenghuanfangjianItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mGenghuanfangjianItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mGenghuanfangjianItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mGenghuanfangjianItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mGenghuanfangjianItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun GenghuanfangjianaddorupdateLayoutBinding.initView(){
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
                    if (mGenghuanfangjianItemBean.yonghuzhanghao.isNullOrEmpty()){
                        mGenghuanfangjianItemBean.yonghuzhanghao = it["yonghuzhanghao"].toString()
                    }
                    binding.yonghuzhanghaoEt.keyListener = null
                    if (mGenghuanfangjianItemBean.yonghuxingming.isNullOrEmpty()){
                        mGenghuanfangjianItemBean.yonghuxingming = it["yonghuxingming"].toString()
                    }
                    binding.yonghuxingmingEt.keyListener = null
                    if (mGenghuanfangjianItemBean.shoujihaoma.isNullOrEmpty()){
                        mGenghuanfangjianItemBean.shoujihaoma = it["shoujihaoma"].toString()
                    }
                    binding.shoujihaomaEt.keyListener = null
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<GenghuanfangjianItemBean>("genghuanfangjian",mId).observeKt {
                it.getOrNull()?.let {
                    mGenghuanfangjianItemBean = it.data
                    mGenghuanfangjianItemBean.id = mId
                    binding.setData()
                }
            }
        }
        if (mCrossTable.isNotNullOrEmpty()){/*跨表*/
            mCrossObj.javaClass.declaredFields.any{it.name == "yudingbianhao"}.yes {
                mGenghuanfangjianItemBean.yudingbianhao = mCrossObj.javaClass.getDeclaredField("yudingbianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "fangjianhao"}.yes {
                mGenghuanfangjianItemBean.fangjianhao = mCrossObj.javaClass.getDeclaredField("fangjianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "kefangleixing"}.yes {
                mGenghuanfangjianItemBean.kefangleixing = mCrossObj.javaClass.getDeclaredField("kefangleixing").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "jiage"}.yes {
                mGenghuanfangjianItemBean.jiage = mCrossObj.javaClass.getDeclaredField("jiage").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuzhanghao"}.yes {
                mGenghuanfangjianItemBean.yonghuzhanghao = mCrossObj.javaClass.getDeclaredField("yonghuzhanghao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuxingming"}.yes {
                mGenghuanfangjianItemBean.yonghuxingming = mCrossObj.javaClass.getDeclaredField("yonghuxingming").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "shoujihaoma"}.yes {
                mGenghuanfangjianItemBean.shoujihaoma = mCrossObj.javaClass.getDeclaredField("shoujihaoma").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "louceng"}.yes {
                mGenghuanfangjianItemBean.louceng = mCrossObj.javaClass.getDeclaredField("louceng").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "genghuanyuanyin"}.yes {
                mGenghuanfangjianItemBean.genghuanyuanyin = mCrossObj.javaClass.getDeclaredField("genghuanyuanyin").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "xinfanghao"}.yes {
                mGenghuanfangjianItemBean.xinfanghao = mCrossObj.javaClass.getDeclaredField("xinfanghao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
        }
        mGenghuanfangjianItemBean.sfsh = "待审核"
        binding.setData()
    }

    /**验证*/
    private fun GenghuanfangjianaddorupdateLayoutBinding.submit() {
        mGenghuanfangjianItemBean.yudingbianhao = yudingbianhaoEt.text.toString()
        mGenghuanfangjianItemBean.fangjianhao = fangjianhaoEt.text.toString()
        mGenghuanfangjianItemBean.kefangleixing = kefangleixingEt.text.toString()
        mGenghuanfangjianItemBean.jiage = jiageEt.text.toString()
        mGenghuanfangjianItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mGenghuanfangjianItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        mGenghuanfangjianItemBean.shoujihaoma = shoujihaomaEt.text.toString()
        mGenghuanfangjianItemBean.louceng = loucengEt.text.toString()
        mGenghuanfangjianItemBean.genghuanyuanyin = genghuanyuanyinEt.text.toString()
        mGenghuanfangjianItemBean.xinfanghao = xinfanghaoEt.text.toString()
        var crossuserid:Long = 0
        var crossrefid:Long = 0
        var crossoptnum:Int = 0
        if (mStatusColumnName.isNotNullOrEmpty()){
            if (!mStatusColumnName.startsWith("[")){
                mCrossObj.javaClass.declaredFields.any{it.name == mStatusColumnName}.yes {
                    mCrossObj.javaClass.getDeclaredField(mStatusColumnName).also { it.isAccessible=true }.set(mCrossObj,mStatusColumnValue)
                    UserRepository.update(mCrossTable,mCrossObj).observeForever {  }
                }
            }else{
                crossuserid = Utils.getUserId()
                mCrossObj.javaClass.declaredFields.any{it.name == "id"}.yes {
                    crossrefid =mCrossObj.javaClass.getDeclaredField("id").also { it.isAccessible=true }.get(mCrossObj).toString().toLong()
                }
                crossoptnum = mStatusColumnName.replace("[","").replace("]","").toIntOrNull()?:0
            }
        }

        if (crossuserid>0 && crossrefid>0){
            mGenghuanfangjianItemBean.javaClass.declaredFields.any{it.name == "crossuserid"}.yes {
                mGenghuanfangjianItemBean.javaClass.getDeclaredField("crossuserid").also { it.isAccessible=true }.set(mGenghuanfangjianItemBean,crossuserid)
            }
            mGenghuanfangjianItemBean.javaClass.declaredFields.any{it.name == "crossrefid"}.yes {
                mGenghuanfangjianItemBean.javaClass.getDeclaredField("crossrefid").also { it.isAccessible=true }.set(mGenghuanfangjianItemBean,crossrefid)
            }
            HomeRepository.list<GenghuanfangjianItemBean>("genghuanfangjian", mapOf("page" to "1","limit" to "10","crossuserid" to crossuserid.toString(),"crossrefid" to crossrefid.toString())).observeKt{
                it.getOrNull()?.let {
                    if (it.data.list.size>=crossoptnum){
                        mTips.showToast()
                    }else{
                        crossCal()
                    }
                }
            }
        }else{
            crossCal()
        }

}
    private fun crossCal(){/*更新跨表数据*/
        addOrUpdate()
    }
    private fun addOrUpdate(){/*更新或添加*/
        if (mGenghuanfangjianItemBean.id>0){
            UserRepository.update("genghuanfangjian",mGenghuanfangjianItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<GenghuanfangjianItemBean>("genghuanfangjian",mGenghuanfangjianItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun GenghuanfangjianaddorupdateLayoutBinding.setData(){
        if (mGenghuanfangjianItemBean.yudingbianhao.isNotNullOrEmpty()){
            yudingbianhaoEt.setText(mGenghuanfangjianItemBean.yudingbianhao.toString())
        }
        if (mGenghuanfangjianItemBean.fangjianhao.isNotNullOrEmpty()){
            fangjianhaoEt.setText(mGenghuanfangjianItemBean.fangjianhao.toString())
        }
        if (mGenghuanfangjianItemBean.kefangleixing.isNotNullOrEmpty()){
            kefangleixingEt.setText(mGenghuanfangjianItemBean.kefangleixing.toString())
        }
        if (mGenghuanfangjianItemBean.jiage.isNotNullOrEmpty()){
            jiageEt.setText(mGenghuanfangjianItemBean.jiage.toString())
        }
        if (mGenghuanfangjianItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mGenghuanfangjianItemBean.yonghuzhanghao.toString())
        }
        if (mGenghuanfangjianItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mGenghuanfangjianItemBean.yonghuxingming.toString())
        }
        if (mGenghuanfangjianItemBean.shoujihaoma.isNotNullOrEmpty()){
            shoujihaomaEt.setText(mGenghuanfangjianItemBean.shoujihaoma.toString())
        }
        if (mGenghuanfangjianItemBean.louceng.isNotNullOrEmpty()){
            loucengEt.setText(mGenghuanfangjianItemBean.louceng.toString())
        }
        if (mGenghuanfangjianItemBean.xinfanghao.isNotNullOrEmpty()){
            xinfanghaoEt.setText(mGenghuanfangjianItemBean.xinfanghao.toString())
        }
        if (mGenghuanfangjianItemBean.genghuanyuanyin.isNotNullOrEmpty()){
            genghuanyuanyinEt.setText(mGenghuanfangjianItemBean.genghuanyuanyin.toString())
        }
    }
}