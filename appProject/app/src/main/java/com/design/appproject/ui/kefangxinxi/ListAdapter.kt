package com.design.appproject.ui.kefangxinxi
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.yes
import android.widget.ImageView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.design.appproject.R
import com.design.appproject.bean.KefangxinxiItemBean
import com.design.appproject.widget.LoadMoreAdapter
import com.design.appproject.ext.load
import com.design.appproject.utils.Utils

/**
 * 客房信息适配器列表
 */
class ListAdapter : LoadMoreAdapter<KefangxinxiItemBean>(R.layout.kefangxinxi_list_item_layout) {

    var mIsBack = false/*是否后台进入*/
    override fun convert(holder: BaseViewHolder, item: KefangxinxiItemBean) {
        holder.setText(R.id.fangjianhao_tv,"房间号:"+ item.fangjianhao.toString())
        val img = item.fangjiantupian.split(",")[0]
        holder.getView<ImageView>(R.id.picture_iv).load(context,img, needPrefix = !img.startsWith("http"))
        holder.setText(R.id.jiage_tv,"每晚/元:"+ item.jiage.toString())
        holder.setText(R.id.fangjianzhuangtai_tv,"房间状态:"+ item.fangjianzhuangtai.toString())
        mIsBack.yes {
            holder.setGone(R.id.edit_fl,!Utils.isAuthBack("kefangxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthBack("kefangxinxi","删除"))
        }.otherwise {
            holder.setGone(R.id.edit_fl,!Utils.isAuthFront("kefangxinxi","修改"))
            holder.setGone(R.id.delete_fl,!Utils.isAuthFront("kefangxinxi","删除"))
        }
    }
}