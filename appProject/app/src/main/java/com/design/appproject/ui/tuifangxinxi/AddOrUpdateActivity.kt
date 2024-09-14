package com.design.appproject.ui.tuifangxinxi

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
import com.design.appproject.bean.TuifangxinxiItemBean
import com.design.appproject.ext.afterTextChanged
import com.design.appproject.bean.RuzhuxinxiItemBean
import com.design.appproject.databinding.TuifangxinxiaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 退房信息新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_TUIFANGXINXI)
class AddOrUpdateActivity:BaseBindingActivity<TuifangxinxiaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mCrossTable: String = "" /*跨表表名*/

    @JvmField
    @Autowired
    var mCrossObj: RuzhuxinxiItemBean = RuzhuxinxiItemBean() /*跨表表内容*/

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
    var mTuifangxinxiItemBean = TuifangxinxiItemBean()

    override fun initEvent() {
        setBarTitle("退房信息")
        setBarColor("#C6000F","white")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mTuifangxinxiItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mTuifangxinxiItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mTuifangxinxiItemBean,mRefid)
                }
            }
            if (mTuifangxinxiItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mTuifangxinxiItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mTuifangxinxiItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mTuifangxinxiItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mTuifangxinxiItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mTuifangxinxiItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun TuifangxinxiaddorupdateLayoutBinding.initView(){
            mTuifangxinxiItemBean.tuifangshijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            tuifangshijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            val mtuifangshijianPicker = DatimePicker(this@AddOrUpdateActivity).apply {
            wheelLayout.setDateFormatter(BirthdayFormatter())
            wheelLayout.setTimeFormatter(UnitTimeFormatter())
            wheelLayout.setRange(DatimeEntity.yearOnFuture(-100), DatimeEntity.yearOnFuture(50), DatimeEntity.now())
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                tuifangshijianTv.text = "$year-$month-$day $hour:$minute:$second"
                mTuifangxinxiItemBean.tuifangshijian="$year-$month-$day $hour:$minute:$second"
            }
        }
            tuifangshijianTv.setOnClickListener {
            mtuifangshijianPicker.show()
        }
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
            HomeRepository.info<TuifangxinxiItemBean>("tuifangxinxi",mId).observeKt {
                it.getOrNull()?.let {
                    mTuifangxinxiItemBean = it.data
                    mTuifangxinxiItemBean.id = mId
                    binding.setData()
                }
            }
        }
        if (mCrossTable.isNotNullOrEmpty()){/*跨表*/
            mCrossObj.javaClass.declaredFields.any{it.name == "yudingbianhao"}.yes {
                mTuifangxinxiItemBean.yudingbianhao = mCrossObj.javaClass.getDeclaredField("yudingbianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "fangjianhao"}.yes {
                mTuifangxinxiItemBean.fangjianhao = mCrossObj.javaClass.getDeclaredField("fangjianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "kefangleixing"}.yes {
                mTuifangxinxiItemBean.kefangleixing = mCrossObj.javaClass.getDeclaredField("kefangleixing").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhuriqi"}.yes {
                mTuifangxinxiItemBean.ruzhuriqi = mCrossObj.javaClass.getDeclaredField("ruzhuriqi").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhutianshu"}.yes {
                mTuifangxinxiItemBean.ruzhutianshu = mCrossObj.javaClass.getDeclaredField("ruzhutianshu").also { it.isAccessible=true }.get(mCrossObj) as Int
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuzhanghao"}.yes {
                mTuifangxinxiItemBean.yonghuzhanghao = mCrossObj.javaClass.getDeclaredField("yonghuzhanghao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuxingming"}.yes {
                mTuifangxinxiItemBean.yonghuxingming = mCrossObj.javaClass.getDeclaredField("yonghuxingming").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "shoujihaoma"}.yes {
                mTuifangxinxiItemBean.shoujihaoma = mCrossObj.javaClass.getDeclaredField("shoujihaoma").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "tuifangshijian"}.yes {
                mTuifangxinxiItemBean.tuifangshijian = mCrossObj.javaClass.getDeclaredField("tuifangshijian").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
        }
        binding.setData()
    }

    /**验证*/
    private fun TuifangxinxiaddorupdateLayoutBinding.submit() {
        mTuifangxinxiItemBean.yudingbianhao = yudingbianhaoEt.text.toString()
        mTuifangxinxiItemBean.fangjianhao = fangjianhaoEt.text.toString()
        mTuifangxinxiItemBean.kefangleixing = kefangleixingEt.text.toString()
        mTuifangxinxiItemBean.ruzhuriqi = ruzhuriqiEt.text.toString()
        ruzhutianshuEt.inputType = InputType.TYPE_CLASS_NUMBER
        mTuifangxinxiItemBean.ruzhutianshu = ruzhutianshuEt.text.toString().toInt()
        mTuifangxinxiItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mTuifangxinxiItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        mTuifangxinxiItemBean.shoujihaoma = shoujihaomaEt.text.toString()
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
            mTuifangxinxiItemBean.javaClass.declaredFields.any{it.name == "crossuserid"}.yes {
                mTuifangxinxiItemBean.javaClass.getDeclaredField("crossuserid").also { it.isAccessible=true }.set(mTuifangxinxiItemBean,crossuserid)
            }
            mTuifangxinxiItemBean.javaClass.declaredFields.any{it.name == "crossrefid"}.yes {
                mTuifangxinxiItemBean.javaClass.getDeclaredField("crossrefid").also { it.isAccessible=true }.set(mTuifangxinxiItemBean,crossrefid)
            }
            HomeRepository.list<TuifangxinxiItemBean>("tuifangxinxi", mapOf("page" to "1","limit" to "10","crossuserid" to crossuserid.toString(),"crossrefid" to crossrefid.toString())).observeKt{
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
        if (mTuifangxinxiItemBean.id>0){
            UserRepository.update("tuifangxinxi",mTuifangxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<TuifangxinxiItemBean>("tuifangxinxi",mTuifangxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun TuifangxinxiaddorupdateLayoutBinding.setData(){
        if (mTuifangxinxiItemBean.yudingbianhao.isNotNullOrEmpty()){
            yudingbianhaoEt.setText(mTuifangxinxiItemBean.yudingbianhao.toString())
        }
        if (mTuifangxinxiItemBean.fangjianhao.isNotNullOrEmpty()){
            fangjianhaoEt.setText(mTuifangxinxiItemBean.fangjianhao.toString())
        }
        if (mTuifangxinxiItemBean.kefangleixing.isNotNullOrEmpty()){
            kefangleixingEt.setText(mTuifangxinxiItemBean.kefangleixing.toString())
        }
        if (mTuifangxinxiItemBean.ruzhuriqi.isNotNullOrEmpty()){
            ruzhuriqiEt.setText(mTuifangxinxiItemBean.ruzhuriqi.toString())
        }
        if (mTuifangxinxiItemBean.ruzhutianshu>=0){
            ruzhutianshuEt.setText(mTuifangxinxiItemBean.ruzhutianshu.toString())
        }
        if (mTuifangxinxiItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mTuifangxinxiItemBean.yonghuzhanghao.toString())
        }
        if (mTuifangxinxiItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mTuifangxinxiItemBean.yonghuxingming.toString())
        }
        if (mTuifangxinxiItemBean.shoujihaoma.isNotNullOrEmpty()){
            shoujihaomaEt.setText(mTuifangxinxiItemBean.shoujihaoma.toString())
        }
    }
}