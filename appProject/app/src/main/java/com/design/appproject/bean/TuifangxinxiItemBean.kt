package com.design.appproject.bean

/**
 * 退房信息实体类
 */
data class TuifangxinxiItemBean(
    var id:Long=0L,
    var yudingbianhao:String="",
    var fangjianhao:String="",
    var kefangleixing:String="",
    var ruzhuriqi:String="",
    var ruzhutianshu:Int=0,
    var yonghuzhanghao:String="",
    var yonghuxingming:String="",
    var shoujihaoma:String="",
    var tuifangshijian:String="",
    var addtime:String?=null,
)