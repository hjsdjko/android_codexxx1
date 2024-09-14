package com.design.appproject.bean

import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken

var roleMenusList =
    GsonUtils.fromJson<List<RoleMenusItem>>("[{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"轮播图\",\"menuJump\":\"列表\",\"tableName\":\"config\"},{\"appFrontIcon\":\"cuIcon-send\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"资讯信息\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common22\",\"menu\":\"管理员管理\",\"unicode\":\"&#xee04;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vip\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"客房类型\",\"menuJump\":\"列表\",\"tableName\":\"kefangleixing\"},{\"appFrontIcon\":\"cuIcon-clothes\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"查看评论\"],\"menu\":\"客房信息\",\"menuJump\":\"列表\",\"tableName\":\"kefangxinxi\"}],\"fontClass\":\"icon-common19\",\"menu\":\"客房信息管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-pay\",\"buttons\":[\"查看\",\"删除\",\"入住\",\"审核\"],\"menu\":\"客房预订\",\"menuJump\":\"列表\",\"tableName\":\"kefangyuding\"}],\"fontClass\":\"icon-common19\",\"menu\":\"客房预定管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vip\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"入住信息\",\"menuJump\":\"列表\",\"tableName\":\"ruzhuxinxi\"}],\"fontClass\":\"icon-common19\",\"menu\":\"入住信息管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"审核\",\"删除\"],\"menu\":\"更换房间\",\"menuJump\":\"列表\",\"tableName\":\"genghuanfangjian\"}],\"fontClass\":\"icon-common19\",\"menu\":\"更换房间管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"退房信息\",\"menuJump\":\"列表\",\"tableName\":\"tuifangxinxi\"}],\"fontClass\":\"icon-common19\",\"menu\":\"退房信息管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-newshot\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"用户\",\"menuJump\":\"列表\",\"tableName\":\"yonghu\"}],\"fontClass\":\"icon-user3\",\"menu\":\"用户信息\",\"unicode\":\"&#xef99;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-paint\",\"buttons\":[\"预定\"],\"menu\":\"客房信息\",\"menuJump\":\"列表\",\"tableName\":\"kefangxinxi\"}],\"menu\":\"客房信息管理\"}],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-pay\",\"buttons\":[\"查看\",\"删除\",\"支付\",\"更换房间\"],\"menu\":\"客房预订\",\"menuJump\":\"列表\",\"tableName\":\"kefangyuding\"}],\"fontClass\":\"icon-common19\",\"menu\":\"客房预定管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vip\",\"buttons\":[\"查看\",\"删除\",\"退房\"],\"menu\":\"入住信息\",\"menuJump\":\"列表\",\"tableName\":\"ruzhuxinxi\"}],\"fontClass\":\"icon-common19\",\"menu\":\"入住信息管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"更换房间\",\"menuJump\":\"列表\",\"tableName\":\"genghuanfangjian\"}],\"fontClass\":\"icon-common19\",\"menu\":\"更换房间管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-explore\",\"buttons\":[\"查看\",\"删除\"],\"menu\":\"退房信息\",\"menuJump\":\"列表\",\"tableName\":\"tuifangxinxi\"}],\"fontClass\":\"icon-common19\",\"menu\":\"退房信息管理\",\"unicode\":\"&#xee00;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-pay\",\"buttons\":[\"查看\"],\"menu\":\"我的收藏\",\"menuJump\":\"1\",\"tableName\":\"storeup\"}],\"fontClass\":\"icon-common25\",\"menu\":\"我的收藏管理\",\"unicode\":\"&#xee09;\"}],\"frontMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-paint\",\"buttons\":[\"预定\"],\"menu\":\"客房信息\",\"menuJump\":\"列表\",\"tableName\":\"kefangxinxi\"}],\"menu\":\"客房信息管理\"}],\"hasBackLogin\":\"否\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"是\",\"hasFrontRegister\":\"是\",\"roleName\":\"用户\",\"tableName\":\"yonghu\"}]", object : TypeToken<List<RoleMenusItem>>() {}.type)

data class RoleMenusItem(
    val backMenu: List<MenuBean>,
    val frontMenu: List<MenuBean>,
    val hasBackLogin: String,
    val hasBackRegister: String,
    val hasFrontLogin: String,
    val hasFrontRegister: String,
    val roleName: String,
    val tableName: String
)

data class MenuBean(
    val child: List<Child>,
    val menu: String,
    val fontClass: String,
    val unicode: String=""
)

data class Child(
    val appFrontIcon: String,
    val buttons: List<String>,
    val menu: String,
    val menuJump: String,
    val tableName: String
)

