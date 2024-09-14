package com.design.appproject.bean

/**
 * 客房预订实体类
 */
data class KefangyudingItemBean(
    var id:Long=0L,
    var yudingbianhao:String="",
    var fangjianhao:String="",
    var kefangleixing:String="",
    var jiage:Double=0.0,
    var ruzhutianshu:Int=0,
    var zongjiage:Double=0.0,
    var yuyuezhuangtai:String="",
    var ruzhuriqi:String="",
    var yuyueshijian:String="",
    var yonghuzhanghao:String="",
    var yonghuxingming:String="",
    var shoujihaoma:String="",
    var ispay:String="",
    var louceng:String="",
    var sfsh:String="",
    var shhf:String="",
    var addtime:String?=null,
)