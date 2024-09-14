package com.design.appproject.ui.ruzhuxinxi

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
import com.design.appproject.bean.RuzhuxinxiItemBean
import com.design.appproject.ext.afterTextChanged
import com.design.appproject.bean.KefangyudingItemBean
import com.design.appproject.databinding.RuzhuxinxiaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 入住信息新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_RUZHUXINXI)
class AddOrUpdateActivity:BaseBindingActivity<RuzhuxinxiaddorupdateLayoutBinding>() {

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
    var mRuzhuxinxiItemBean = RuzhuxinxiItemBean()

    override fun initEvent() {
        setBarTitle("入住信息")
        setBarColor("#C6000F","white")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mRuzhuxinxiItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mRuzhuxinxiItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mRuzhuxinxiItemBean,mRefid)
                }
            }
            if (mRuzhuxinxiItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mRuzhuxinxiItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mRuzhuxinxiItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mRuzhuxinxiItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mRuzhuxinxiItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mRuzhuxinxiItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun RuzhuxinxiaddorupdateLayoutBinding.initView(){
            mRuzhuxinxiItemBean.ruzhuriqi = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
            ruzhuriqiTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd"))
            val mruzhuriqiPicker = DatePicker(this@AddOrUpdateActivity).apply {
                wheelLayout.setDateFormatter(BirthdayFormatter())
                wheelLayout.setRange(DateEntity.target(1923, 1, 1),DateEntity.target(2050, 12, 31), DateEntity.today())
                setOnDatePickedListener { year, month, day ->
                    ruzhuriqiTv.text = "$year-$month-$day"
                    mRuzhuxinxiItemBean.ruzhuriqi="$year-$month-$day"
                }
        }
            ruzhuriqiTv.setOnClickListener {
            mruzhuriqiPicker.show()
        }
            mRuzhuxinxiItemBean.ruzhushijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            ruzhushijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            val mruzhushijianPicker = DatimePicker(this@AddOrUpdateActivity).apply {
            wheelLayout.setDateFormatter(BirthdayFormatter())
            wheelLayout.setTimeFormatter(UnitTimeFormatter())
            wheelLayout.setRange(DatimeEntity.yearOnFuture(-100), DatimeEntity.yearOnFuture(50), DatimeEntity.now())
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                ruzhushijianTv.text = "$year-$month-$day $hour:$minute:$second"
                mRuzhuxinxiItemBean.ruzhushijian="$year-$month-$day $hour:$minute:$second"
            }
        }
            ruzhushijianTv.setOnClickListener {
            mruzhushijianPicker.show()
        }
            tuifangzhuangtaiBs.setOptions("已退房,未退房".split(","),"请选择退房状态",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    tuifangzhuangtaiBs.text = content
                    mRuzhuxinxiItemBean.tuifangzhuangtai =content
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
            HomeRepository.info<RuzhuxinxiItemBean>("ruzhuxinxi",mId).observeKt {
                it.getOrNull()?.let {
                    mRuzhuxinxiItemBean = it.data
                    mRuzhuxinxiItemBean.id = mId
                    binding.setData()
                }
            }
        }
        if (mCrossTable.isNotNullOrEmpty()){/*跨表*/
            mCrossObj.javaClass.declaredFields.any{it.name == "yudingbianhao"}.yes {
                mRuzhuxinxiItemBean.yudingbianhao = mCrossObj.javaClass.getDeclaredField("yudingbianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "fangjianhao"}.yes {
                mRuzhuxinxiItemBean.fangjianhao = mCrossObj.javaClass.getDeclaredField("fangjianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "kefangleixing"}.yes {
                mRuzhuxinxiItemBean.kefangleixing = mCrossObj.javaClass.getDeclaredField("kefangleixing").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhuriqi"}.yes {
                mRuzhuxinxiItemBean.ruzhuriqi = mCrossObj.javaClass.getDeclaredField("ruzhuriqi").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhutianshu"}.yes {
                mRuzhuxinxiItemBean.ruzhutianshu = mCrossObj.javaClass.getDeclaredField("ruzhutianshu").also { it.isAccessible=true }.get(mCrossObj) as Int
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuzhanghao"}.yes {
                mRuzhuxinxiItemBean.yonghuzhanghao = mCrossObj.javaClass.getDeclaredField("yonghuzhanghao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuxingming"}.yes {
                mRuzhuxinxiItemBean.yonghuxingming = mCrossObj.javaClass.getDeclaredField("yonghuxingming").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "shoujihaoma"}.yes {
                mRuzhuxinxiItemBean.shoujihaoma = mCrossObj.javaClass.getDeclaredField("shoujihaoma").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhushijian"}.yes {
                mRuzhuxinxiItemBean.ruzhushijian = mCrossObj.javaClass.getDeclaredField("ruzhushijian").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "tuifangzhuangtai"}.yes {
                mRuzhuxinxiItemBean.tuifangzhuangtai = mCrossObj.javaClass.getDeclaredField("tuifangzhuangtai").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
        }
        mRuzhuxinxiItemBean.tuifangzhuangtai = "未退房"
        binding.setData()
    }

    /**验证*/
    private fun RuzhuxinxiaddorupdateLayoutBinding.submit() {
        mRuzhuxinxiItemBean.yudingbianhao = yudingbianhaoEt.text.toString()
        mRuzhuxinxiItemBean.fangjianhao = fangjianhaoEt.text.toString()
        mRuzhuxinxiItemBean.kefangleixing = kefangleixingEt.text.toString()
        ruzhutianshuEt.inputType = InputType.TYPE_CLASS_NUMBER
        mRuzhuxinxiItemBean.ruzhutianshu = ruzhutianshuEt.text.toString().toInt()
        mRuzhuxinxiItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mRuzhuxinxiItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        mRuzhuxinxiItemBean.shoujihaoma = shoujihaomaEt.text.toString()
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
            mRuzhuxinxiItemBean.javaClass.declaredFields.any{it.name == "crossuserid"}.yes {
                mRuzhuxinxiItemBean.javaClass.getDeclaredField("crossuserid").also { it.isAccessible=true }.set(mRuzhuxinxiItemBean,crossuserid)
            }
            mRuzhuxinxiItemBean.javaClass.declaredFields.any{it.name == "crossrefid"}.yes {
                mRuzhuxinxiItemBean.javaClass.getDeclaredField("crossrefid").also { it.isAccessible=true }.set(mRuzhuxinxiItemBean,crossrefid)
            }
            HomeRepository.list<RuzhuxinxiItemBean>("ruzhuxinxi", mapOf("page" to "1","limit" to "10","crossuserid" to crossuserid.toString(),"crossrefid" to crossrefid.toString())).observeKt{
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
        if (mRuzhuxinxiItemBean.id>0){
            UserRepository.update("ruzhuxinxi",mRuzhuxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<RuzhuxinxiItemBean>("ruzhuxinxi",mRuzhuxinxiItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun RuzhuxinxiaddorupdateLayoutBinding.setData(){
        if (mRuzhuxinxiItemBean.yudingbianhao.isNotNullOrEmpty()){
            yudingbianhaoEt.setText(mRuzhuxinxiItemBean.yudingbianhao.toString())
        }
        if (mRuzhuxinxiItemBean.fangjianhao.isNotNullOrEmpty()){
            fangjianhaoEt.setText(mRuzhuxinxiItemBean.fangjianhao.toString())
        }
        if (mRuzhuxinxiItemBean.kefangleixing.isNotNullOrEmpty()){
            kefangleixingEt.setText(mRuzhuxinxiItemBean.kefangleixing.toString())
        }
        if (mRuzhuxinxiItemBean.ruzhutianshu>=0){
            ruzhutianshuEt.setText(mRuzhuxinxiItemBean.ruzhutianshu.toString())
        }
        if (mRuzhuxinxiItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mRuzhuxinxiItemBean.yonghuzhanghao.toString())
        }
        if (mRuzhuxinxiItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mRuzhuxinxiItemBean.yonghuxingming.toString())
        }
        if (mRuzhuxinxiItemBean.shoujihaoma.isNotNullOrEmpty()){
            shoujihaomaEt.setText(mRuzhuxinxiItemBean.shoujihaoma.toString())
        }
        if (mRuzhuxinxiItemBean.tuifangzhuangtai.isNotNullOrEmpty()){
            tuifangzhuangtaiBs.text =mRuzhuxinxiItemBean.tuifangzhuangtai
        }
    }
}