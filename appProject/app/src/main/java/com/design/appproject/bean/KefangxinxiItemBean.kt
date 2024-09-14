package com.design.appproject.bean

/**
 * 客房信息实体类
 */
data class KefangxinxiItemBean(
    var id:Long=0L,
    var fangjianhao:String="",
    var fangjiantupian:String="",
    var kefangleixing:String="",
    var jiage:Double=0.0,
    var fangjiansheshi:String="",
    var fangjianxiangqing:String="",
    var fangjianzhuangtai:String="",
    var storeupnum:Int=0,
    var clicktime:String="",
    var clicknum:Int=0,
    var louceng:String="",
    var addtime:String?=null,
)