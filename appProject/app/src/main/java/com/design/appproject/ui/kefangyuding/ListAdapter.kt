package com.design.appproject.ui.kefangyuding
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.KefangyudingItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 客房预订适配器列表
 */
class ListAdapter : LoadMoreAdapter<KefangyudingItemBean>(R.layout.kefangyuding_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: KefangyudingItemBean) {
        holder.setText(R.id.yudingbianhao_tv,"预订编号:"+ item.yudingbianhao.toString())
        holder.setText(R.id.fangjianhao_tv, item.fangjianhao.toString())
        holder.setText(R.id.jiage_tv,"价格:"+ item.jiage.toString())
        holder.setText(R.id.yuyuezhuangtai_tv,"预约状态:"+ item.yuyuezhuangtai.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("kefangyuding","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("kefangyuding","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("kefangyuding","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("kefangyuding","删除"))
        }
    }
}