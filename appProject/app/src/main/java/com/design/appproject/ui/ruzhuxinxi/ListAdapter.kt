package com.design.appproject.ui.ruzhuxinxi
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.RuzhuxinxiItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 入住信息适配器列表
 */
class ListAdapter : LoadMoreAdapter<RuzhuxinxiItemBean>(R.layout.ruzhuxinxi_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: RuzhuxinxiItemBean) {
        holder.setText(R.id.fangjianhao_tv, item.fangjianhao.toString())
        holder.setText(R.id.ruzhuriqi_tv,"入住日期:"+ item.ruzhuriqi.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("ruzhuxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("ruzhuxinxi","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("ruzhuxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("ruzhuxinxi","删除"))
        }
    }
}