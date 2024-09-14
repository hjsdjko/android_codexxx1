package com.design.appproject.ui.kefangyuding

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
import com.design.appproject.bean.KefangyudingItemBean
import com.design.appproject.ext.afterTextChanged
import com.design.appproject.bean.KefangxinxiItemBean
import com.design.appproject.databinding.KefangyudingaddorupdateLayoutBinding
import com.design.appproject.ext.load
import android.text.InputType

/**
 * 客房预订新增或修改类
 */
@Route(path = CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_KEFANGYUDING)
class AddOrUpdateActivity:BaseBindingActivity<KefangyudingaddorupdateLayoutBinding>() {

    @JvmField
    @Autowired
    var mId: Long = 0L /*id*/

    @JvmField
    @Autowired
    var mCrossTable: String = "" /*跨表表名*/

    @JvmField
    @Autowired
    var mCrossObj: KefangxinxiItemBean = KefangxinxiItemBean() /*跨表表内容*/

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
    var mKefangyudingItemBean = KefangyudingItemBean()

    override fun initEvent() {
        setBarTitle("客房预订")
        setBarColor("#C6000F","white")
        if (mRefid>0){/*如果上一级页面传递了refid，获取改refid数据信息*/
            if (mKefangyudingItemBean.javaClass.declaredFields.any{it.name == "refid"}){
                mKefangyudingItemBean.javaClass.getDeclaredField("refid").also { it.isAccessible=true }.let {
                    it.set(mKefangyudingItemBean,mRefid)
                }
            }
            if (mKefangyudingItemBean.javaClass.declaredFields.any{it.name == "nickname"}){
                mKefangyudingItemBean.javaClass.getDeclaredField("nickname").also { it.isAccessible=true }.let {
                    it.set(mKefangyudingItemBean,StorageUtil.decodeString(CommonBean.USERNAME_KEY)?:"")
                }
            }
        }
        if (Utils.isLogin() && mKefangyudingItemBean.javaClass.declaredFields.any{it.name == "userid"}){/*如果有登陆，获取登陆后保存的userid*/
            mKefangyudingItemBean.javaClass.getDeclaredField("userid").also { it.isAccessible=true }.let {
                it.set(mKefangyudingItemBean,Utils.getUserId())
            }
        }
        binding.initView()

    }

    fun KefangyudingaddorupdateLayoutBinding.initView(){
            jiageEt.afterTextChanged {
                mKefangyudingItemBean.jiage =it.toDoubleOrNull()?:0.0
                val reulst = mKefangyudingItemBean.jiage*mKefangyudingItemBean.ruzhutianshu
                zongjiageEt.setText(reulst.toString())
                mKefangyudingItemBean.zongjiage = reulst
            }
            ruzhutianshuEt.afterTextChanged {
                mKefangyudingItemBean.ruzhutianshu =it.toIntOrNull()?:0
                val reulst = mKefangyudingItemBean.jiage*mKefangyudingItemBean.ruzhutianshu
                zongjiageEt.setText(reulst.toString())
                mKefangyudingItemBean.zongjiage = reulst
            }
            yuyuezhuangtaiBs.setOptions("已入住,未入住".split(","),"请选择预约状态",
            listener = object : BottomSpinner.OnItemSelectedListener {
                override fun onItemSelected(position: Int, content: String) {
                    super.onItemSelected(position, content)
                    yuyuezhuangtaiBs.text = content
                    mKefangyudingItemBean.yuyuezhuangtai =content
                }
            })
            val mruzhuriqiPicker = DatePicker(this@AddOrUpdateActivity).apply {
                wheelLayout.setDateFormatter(BirthdayFormatter())
                wheelLayout.setRange(DateEntity.target(1923, 1, 1),DateEntity.target(2050, 12, 31), DateEntity.today())
                setOnDatePickedListener { year, month, day ->
                    ruzhuriqiTv.text = "$year-$month-$day"
                    mKefangyudingItemBean.ruzhuriqi="$year-$month-$day"
                }
        }
            ruzhuriqiTv.setOnClickListener {
            mruzhuriqiPicker.show()
        }
            mKefangyudingItemBean.yuyueshijian = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            yuyueshijianTv.text = TimeUtils.getNowString(SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
            val myuyueshijianPicker = DatimePicker(this@AddOrUpdateActivity).apply {
            wheelLayout.setDateFormatter(BirthdayFormatter())
            wheelLayout.setTimeFormatter(UnitTimeFormatter())
            wheelLayout.setRange(DatimeEntity.yearOnFuture(-100), DatimeEntity.yearOnFuture(50), DatimeEntity.now())
            setOnDatimePickedListener { year, month, day, hour, minute, second ->
                yuyueshijianTv.text = "$year-$month-$day $hour:$minute:$second"
                mKefangyudingItemBean.yuyueshijian="$year-$month-$day $hour:$minute:$second"
            }
        }
            yuyueshijianTv.setOnClickListener {
            myuyueshijianPicker.show()
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
                    if (mKefangyudingItemBean.yonghuzhanghao.isNullOrEmpty()){
                        mKefangyudingItemBean.yonghuzhanghao = it["yonghuzhanghao"].toString()
                    }
                    binding.yonghuzhanghaoEt.keyListener = null
                    if (mKefangyudingItemBean.yonghuxingming.isNullOrEmpty()){
                        mKefangyudingItemBean.yonghuxingming = it["yonghuxingming"].toString()
                    }
                    binding.yonghuxingmingEt.keyListener = null
                    if (mKefangyudingItemBean.shoujihaoma.isNullOrEmpty()){
                        mKefangyudingItemBean.shoujihaoma = it["shoujihaoma"].toString()
                    }
                    binding.shoujihaomaEt.keyListener = null
                    binding.setData()
                }
            }
        }

        (mId>0).yes {/*更新操作*/
            HomeRepository.info<KefangyudingItemBean>("kefangyuding",mId).observeKt {
                it.getOrNull()?.let {
                    mKefangyudingItemBean = it.data
                    mKefangyudingItemBean.id = mId
                    binding.setData()
                }
            }
        }
        if (mCrossTable.isNotNullOrEmpty()){/*跨表*/
            mCrossObj.javaClass.declaredFields.any{it.name == "yudingbianhao"}.yes {
                mKefangyudingItemBean.yudingbianhao = mCrossObj.javaClass.getDeclaredField("yudingbianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "fangjianhao"}.yes {
                mKefangyudingItemBean.fangjianhao = mCrossObj.javaClass.getDeclaredField("fangjianhao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "kefangleixing"}.yes {
                mKefangyudingItemBean.kefangleixing = mCrossObj.javaClass.getDeclaredField("kefangleixing").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "jiage"}.yes {
                mKefangyudingItemBean.jiage = mCrossObj.javaClass.getDeclaredField("jiage").also { it.isAccessible=true }.get(mCrossObj) as Double
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhutianshu"}.yes {
                mKefangyudingItemBean.ruzhutianshu = mCrossObj.javaClass.getDeclaredField("ruzhutianshu").also { it.isAccessible=true }.get(mCrossObj) as Int
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "zongjiage"}.yes {
                mKefangyudingItemBean.zongjiage = mCrossObj.javaClass.getDeclaredField("zongjiage").also { it.isAccessible=true }.get(mCrossObj) as Double
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yuyuezhuangtai"}.yes {
                mKefangyudingItemBean.yuyuezhuangtai = mCrossObj.javaClass.getDeclaredField("yuyuezhuangtai").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "ruzhuriqi"}.yes {
                mKefangyudingItemBean.ruzhuriqi = mCrossObj.javaClass.getDeclaredField("ruzhuriqi").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yuyueshijian"}.yes {
                mKefangyudingItemBean.yuyueshijian = mCrossObj.javaClass.getDeclaredField("yuyueshijian").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuzhanghao"}.yes {
                mKefangyudingItemBean.yonghuzhanghao = mCrossObj.javaClass.getDeclaredField("yonghuzhanghao").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "yonghuxingming"}.yes {
                mKefangyudingItemBean.yonghuxingming = mCrossObj.javaClass.getDeclaredField("yonghuxingming").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "shoujihaoma"}.yes {
                mKefangyudingItemBean.shoujihaoma = mCrossObj.javaClass.getDeclaredField("shoujihaoma").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
            mCrossObj.javaClass.declaredFields.any{it.name == "louceng"}.yes {
                mKefangyudingItemBean.louceng = mCrossObj.javaClass.getDeclaredField("louceng").also { it.isAccessible=true }.get(mCrossObj) as  String
            }
        }
        mKefangyudingItemBean.yuyuezhuangtai = "未入住"
        mKefangyudingItemBean.ispay = "未支付"
        mKefangyudingItemBean.sfsh = "待审核"
        binding.setData()
    }

    /**验证*/
    private fun KefangyudingaddorupdateLayoutBinding.submit() {
        mKefangyudingItemBean.yudingbianhao = yudingbianhaoEt.text.toString()
        mKefangyudingItemBean.fangjianhao = fangjianhaoEt.text.toString()
        mKefangyudingItemBean.kefangleixing = kefangleixingEt.text.toString()
        jiageEt.inputType = InputType.TYPE_CLASS_NUMBER
        mKefangyudingItemBean.jiage = jiageEt.text.toString().toDoubleOrNull()?:0.0
        ruzhutianshuEt.inputType = InputType.TYPE_CLASS_NUMBER
        mKefangyudingItemBean.ruzhutianshu = ruzhutianshuEt.text.toString().toInt()
        zongjiageEt.inputType = InputType.TYPE_CLASS_NUMBER
        mKefangyudingItemBean.zongjiage = zongjiageEt.text.toString().toDoubleOrNull()?:0.0
        mKefangyudingItemBean.yonghuzhanghao = yonghuzhanghaoEt.text.toString()
        mKefangyudingItemBean.yonghuxingming = yonghuxingmingEt.text.toString()
        mKefangyudingItemBean.shoujihaoma = shoujihaomaEt.text.toString()
        mKefangyudingItemBean.louceng = loucengEt.text.toString()
        if(mKefangyudingItemBean.ruzhutianshu<=0){
            "入住天数不能为空".showToast()
            return
        }
        if(mKefangyudingItemBean.ruzhuriqi.toString().isNullOrEmpty()){
            "入住日期不能为空".showToast()
            return
        }
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
            mKefangyudingItemBean.javaClass.declaredFields.any{it.name == "crossuserid"}.yes {
                mKefangyudingItemBean.javaClass.getDeclaredField("crossuserid").also { it.isAccessible=true }.set(mKefangyudingItemBean,crossuserid)
            }
            mKefangyudingItemBean.javaClass.declaredFields.any{it.name == "crossrefid"}.yes {
                mKefangyudingItemBean.javaClass.getDeclaredField("crossrefid").also { it.isAccessible=true }.set(mKefangyudingItemBean,crossrefid)
            }
            HomeRepository.list<KefangyudingItemBean>("kefangyuding", mapOf("page" to "1","limit" to "10","crossuserid" to crossuserid.toString(),"crossrefid" to crossrefid.toString())).observeKt{
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
        if (mKefangyudingItemBean.id>0){
            UserRepository.update("kefangyuding",mKefangyudingItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }else{
            HomeRepository.add<KefangyudingItemBean>("kefangyuding",mKefangyudingItemBean).observeKt{
            it.getOrNull()?.let {
                "提交成功".showToast()
                finish()
            }
        }
        }
    }


    private fun KefangyudingaddorupdateLayoutBinding.setData(){
        if (mKefangyudingItemBean.yudingbianhao.isNotNullOrEmpty()){
            yudingbianhaoEt.setText(mKefangyudingItemBean.yudingbianhao.toString())
        }
        yudingbianhaoEt.setText(Utils.genTradeNo())
        if (mKefangyudingItemBean.fangjianhao.isNotNullOrEmpty()){
            fangjianhaoEt.setText(mKefangyudingItemBean.fangjianhao.toString())
        }
        if (mKefangyudingItemBean.kefangleixing.isNotNullOrEmpty()){
            kefangleixingEt.setText(mKefangyudingItemBean.kefangleixing.toString())
        }
        if (mKefangyudingItemBean.jiage>=0){
            jiageEt.setText(mKefangyudingItemBean.jiage.toString())
        }
        if (mKefangyudingItemBean.ruzhutianshu>=0){
            ruzhutianshuEt.setText(mKefangyudingItemBean.ruzhutianshu.toString())
        }
        if (mKefangyudingItemBean.zongjiage>=0){
            zongjiageEt.setText(mKefangyudingItemBean.zongjiage.toString())
        }
        if (mKefangyudingItemBean.yuyuezhuangtai.isNotNullOrEmpty()){
            yuyuezhuangtaiBs.text =mKefangyudingItemBean.yuyuezhuangtai
        }
        ruzhuriqiTv.text = mKefangyudingItemBean.ruzhuriqi
        if (mKefangyudingItemBean.yonghuzhanghao.isNotNullOrEmpty()){
            yonghuzhanghaoEt.setText(mKefangyudingItemBean.yonghuzhanghao.toString())
        }
        if (mKefangyudingItemBean.yonghuxingming.isNotNullOrEmpty()){
            yonghuxingmingEt.setText(mKefangyudingItemBean.yonghuxingming.toString())
        }
        if (mKefangyudingItemBean.shoujihaoma.isNotNullOrEmpty()){
            shoujihaomaEt.setText(mKefangyudingItemBean.shoujihaoma.toString())
        }
        if (mKefangyudingItemBean.louceng.isNotNullOrEmpty()){
            loucengEt.setText(mKefangyudingItemBean.louceng.toString())
        }
    }
}