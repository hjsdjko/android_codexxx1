package com.design.appproject.bean

/**
 * 入住信息实体类
 */
data class RuzhuxinxiItemBean(
    var id:Long=0L,
    var yudingbianhao:String="",
    var fangjianhao:String="",
    var kefangleixing:String="",
    var ruzhuriqi:String="",
    var ruzhutianshu:Int=0,
    var yonghuzhanghao:String="",
    var yonghuxingming:String="",
    var shoujihaoma:String="",
    var ruzhushijian:String="",
    var tuifangzhuangtai:String="",
    var addtime:String?=null,
)