package com.design.appproject.ui.kefangyuding

import android.app.Activity.RESULT_OK
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.design.appproject.base.BaseBindingFragment
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.logic.repository.UserRepository
import com.lxj.xpopup.XPopup
import com.design.appproject.databinding.FragmentPayconfirmLayoutBinding
import com.union.union_basic.ext.showToast

@Route(path = CommonArouteApi.PATH_FRAGMENT_PAY_CONFIRM_KEFANGYUDING)
class PayConfirmFragment : BaseBindingFragment<FragmentPayconfirmLayoutBinding>() {

    @Autowired
    @JvmField
    var paytable: String = ""/*表名*/

    @Autowired
    @JvmField
    var payObject: Any = Any()/*支付内容*/

    override fun initEvent() {
        setBarTitle("确认支付")
        binding.apply {
            ensureBtn.setOnClickListener {
                XPopup.Builder(requireActivity()).asConfirm("提示","是否确认支付") {
                    val ispay = payObject.javaClass.getDeclaredField("ispay")
                    if (ispay!=null){
                        ispay.isAccessible =true
                        ispay.set(payObject,"已支付")
                    }
                    UserRepository.update(paytable,payObject).observeKt {
                        it.getOrNull()?.let {
                            "支付成功".showToast()
                            requireActivity().setResult(RESULT_OK)
                            requireActivity().finish()
                        }
                    }
                }.show()
            }
        }
    }
}