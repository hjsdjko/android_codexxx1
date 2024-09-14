package com.design.appproject.ui.genghuanfangjian
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.GenghuanfangjianItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 更换房间适配器列表
 */
class ListAdapter : LoadMoreAdapter<GenghuanfangjianItemBean>(R.layout.genghuanfangjian_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: GenghuanfangjianItemBean) {
        holder.setText(R.id.fangjianhao_tv, item.fangjianhao.toString())
        holder.setText(R.id.xinfanghao_tv,"新房号:"+ item.xinfanghao.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("genghuanfangjian","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("genghuanfangjian","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("genghuanfangjian","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("genghuanfangjian","删除"))
        }
    }
}