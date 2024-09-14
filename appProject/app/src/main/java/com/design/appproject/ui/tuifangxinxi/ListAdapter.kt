package com.design.appproject.ui.tuifangxinxi
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.TuifangxinxiItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 退房信息适配器列表
 */
class ListAdapter : LoadMoreAdapter<TuifangxinxiItemBean>(R.layout.tuifangxinxi_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: TuifangxinxiItemBean) {
        holder.setText(R.id.fangjianhao_tv, item.fangjianhao.toString())
        holder.setText(R.id.tuifangshijian_tv,"退房时间:"+ item.tuifangshijian.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("tuifangxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("tuifangxinxi","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("tuifangxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("tuifangxinxi","删除"))
        }
    }
}