package com.design.appproject.ui.kefangxinxi

import com.design.appproject.base.NetRetrofitClient
import com.design.appproject.utils.ArouterUtils
import androidx.fragment.app.viewModels
import com.design.appproject.base.BaseBindingFragment
import com.alibaba.android.arouter.launcher.ARouter
import android.widget.*
import android.view.View
import androidx.constraintlayout.utils.widget.ImageFilterView
import com.design.appproject.ext.load
import com.qmuiteam.qmui.layout.QMUILinearLayout
import com.blankj.utilcode.util.ColorUtils
import androidx.core.view.children
import androidx.core.view.isVisible
import com.design.appproject.R
import com.design.appproject.ui.kefangxinxi.ListFilterDialog
import android.view.ViewGroup
import android.view.Gravity
import androidx.core.view.setPadding
import androidx.core.view.setMargins
import com.design.appproject.widget.SpacesItemDecoration
import com.union.union_basic.ext.*
import net.lucode.hackware.magicindicator.MagicIndicator
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.KefangxinxicommonListLayoutBinding
import com.design.appproject.logic.repository.HomeRepository
import com.design.appproject.logic.viewmodel.kefangxinxi.ListModel
import com.design.appproject.ui.kefangxinxi.ListAdapter
import com.design.appproject.utils.Utils
import com.google.android.flexbox.*
import com.design.appproject.logic.repository.UserRepository
import com.design.appproject.bean.KefangleixingItemBean
import android.view.LayoutInflater
import com.design.appproject.widget.MagicIndexCommonNavigator
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.enums.PopupPosition
import com.blankj.utilcode.util.KeyboardUtils
import com.design.appproject.bean.KefangxinxiItemBean
import com.design.appproject.widget.MyFlexboxLayoutManager
/**
 * 客房信息列表页
 */
@Route(path = CommonArouteApi.PATH_FRAGMENT_LIST_KEFANGXINXI)
class ListFragment : BaseBindingFragment<KefangxinxicommonListLayoutBinding>() {

    @JvmField
    @Autowired
    var mSearch: String = "" /*搜索*/

    @JvmField
    @Autowired
    var mIsBack: Boolean = false /*是否用户后台进入*/

    @JvmField
    @Autowired
    var mHasBack: Boolean = true /*是否有返回键*/

    private val mListAdapter by lazy {
        ListAdapter().apply {
            mIsBack = this@ListFragment.mIsBack
        }
    }

    private val mListModel by viewModels<ListModel>()

    private val params by lazy { /*请求参数*/
        mutableMapOf(
            "page" to "1",
            "limit" to "20",
        )
    }
    val queryIndex = Pair<String,String>("房间号","fangjianhao")/*查询索引*/

    private val mListFilterdialog by lazy {/*筛选弹窗*/
        ListFilterDialog(requireActivity()).apply {
            callBackListener = {isEnsure,searchParams ->
                isEnsure.yes { /*确定筛选条件*/
                    loadData(1,searchParams)
                }.otherwise {
                    loadData(1,searchParams)
                }
            }
        }
    }
    override fun initEvent() {
        setBarTitle("客房信息",mHasBack)
        setBarColor("#C6000F","white")
        binding.apply {
            addBtn.isVisible = (mIsBack && Utils.isAuthBack("kefangxinxi","新增"))|| Utils.isAuthFront("kefangxinxi","新增")
            addBtn.setOnClickListener {
                ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_KEFANGXINXI).navigation()
            }
            initSearch()
            initCategory()
            srv.setOnRefreshListener {
                loadData(1)
            }
            filterTv.setOnClickListener {
                XPopup.Builder(requireActivity())
                    .popupPosition(PopupPosition.Right) /*右边*/
                    .hasStatusBarShadow(false) /*启用状态栏阴影*/
                    .autoFocusEditText(false)
                    .asCustom(mListFilterdialog)
                    .show()
            }
            srv.mRecyclerView.layoutManager = MyFlexboxLayoutManager(requireActivity()).apply {
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.SPACE_BETWEEN
            }
            mListAdapter.addChildClickViewIds(R.id.edit_tv,R.id.delete_tv)
            mListAdapter.setOnItemChildClickListener { adapter, view, position ->
                if (view.id==R.id.edit_tv){
                  ARouter.getInstance().build(CommonArouteApi.PATH_ACTIVITY_ADDORUPDATE_KEFANGXINXI)
                    .withLong("mId",mListAdapter.data[position].id)
                    .navigation()
                }else if (view.id==R.id.delete_tv){
                  XPopup.Builder(requireActivity()).asConfirm("提示","是否确认删除") {
                      HomeRepository.delete<Any>("kefangxinxi",listOf(mListAdapter.data[position].id)).observeKt {
                        it.getOrNull()?.let {
                            "删除成功".showToast()
                            loadData(1)
                        }
                      }
                  }.show()
                }
            }
            srv.setAdapter(mListAdapter.apply {
                pageLoadMoreListener {
                    loadData(it)
                }
                setOnItemClickListener { adapter, view, position ->
                    ArouterUtils.startFragment(CommonArouteApi.PATH_FRAGMENT_DETAILS_KEFANGXINXI,map=mapOf("mId" to data[position].id,"mIsBack" to mIsBack))
                }
            })
        }
    }
    private fun KefangxinxicommonListLayoutBinding.initSearch() {
        searchValueEt.hint = queryIndex.first
        searchValueEt.setText(mSearch)
        searchBtn.setOnClickListener {
            loadData(1)
        }
        searchValueEt.postDelayed({ KeyboardUtils.hideSoftInput(searchValueEt) }, 200)
    }


    val mCategoryList = mutableListOf("全部")
    var mSelectedIndex = 0

    private fun KefangxinxicommonListLayoutBinding.initCategory() {
        mIsBack.yes {
            HomeRepository.page<KefangleixingItemBean>("kefangleixing", mapOf("page" to "1", "limit" to "100")).observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let {
                setNavigator(tab,it.data.list)
            }
        } }.otherwise {
            HomeRepository.list<KefangleixingItemBean>("kefangleixing", mapOf("page" to "1", "limit" to "100")).observeKt(errorBlock = {binding.srv.isRefreshing =false}) {
            it.getOrNull()?.let {
                setNavigator(tab,it.data.list)
            }
        }
        }
    }

    private fun setNavigator(tab: LinearLayout,list:List<KefangleixingItemBean>){
        mCategoryList.addAll(list.map{it.kefangleixing})
        mCategoryList.forEachIndexed { index, s ->
            val itemView = creatTabView(index,tab,index==0,s)
            tab.addView(itemView)
        }
    }

    private fun creatTabView(index:Int,tab: LinearLayout, isSelect:Boolean,title:String):View{
        val itemView = LayoutInflater.from(context).inflate(if (isSelect) R.layout.item_tab_layout_active else R.layout.item_tab_layout_default, tab, false)
        itemView.isSelected = isSelect
        itemView.toConversion<TextView>()?.text = title
        itemView.setOnClickListener {
            if (itemView.isSelected){
                return@setOnClickListener
            }else{
                mSelectedIndex = index
                loadData(1)
                val oldIndex= tab.children.indexOfFirst { it.isSelected }
                if (oldIndex!=-1){
                    tab.removeViewAt(oldIndex)
                    tab.addView(creatTabView(oldIndex,tab,false,mCategoryList[oldIndex]),oldIndex)
                }
                tab.removeViewAt(index)
                tab.addView(creatTabView(index,tab,true,title),index)
            }
        }
        return itemView
    }

    override fun initData() {
        super.initData()
        loadData(1)
        mListModel.pageLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }

        mListModel.listLiveData.observeKt {
            it.getOrNull()?.let {
                binding.srv.setData(it.data.list,it.data.total)
            }
        }

    }

    private fun loadData(page:Int,searchParams:MutableMap<String,String>?=null){
        if (page==1){
            binding.srv.reload()
        }
        params.put("page",page.toString())
        binding.searchValueEt.text.toString().isNotNullOrEmpty().yes {
            params.put("fangjianhao","%" + binding.searchValueEt.text.toString() + "%" )
        }.otherwise {
            params.remove("fangjianhao")
        }
        if(mCategoryList[mSelectedIndex]!="全部"){
            params.put("kefangleixing","%" + mCategoryList[mSelectedIndex] + "%" )
        }else{
            params.remove("kefangleixing")
        }
        val requestParams = mutableMapOf<String,String>()
        requestParams.putAll(params)
        searchParams?.let { requestParams.putAll(it) }
        mIsBack.yes {
                mListModel.page("kefangxinxi", requestParams)
        }.otherwise {
                mListModel.list("kefangxinxi", requestParams)
        }
    }
}