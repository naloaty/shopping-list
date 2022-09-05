package me.naloaty.shoppinglist.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import me.naloaty.shoppinglist.R
import me.naloaty.shoppinglist.domain.ShopItem
import java.lang.RuntimeException

class ShopItemFragment: Fragment() {

    private lateinit var viewModel: ShopItemViewModel

    private lateinit var tilTitle: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etTitle: EditText
    private lateinit var etCount: EditText
    private lateinit var btnSave: Button

    private var screenMode: String = UNDEFINED_SCREEN_MODE
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews(view)
        launchRightMode()
        observeViewModel()
    }

    private fun launchRightMode() {
        when(screenMode) {
            MODE_EDIT -> launchEditMode()
            MODE_ADD -> launchAddMode()
        }
    }

    private fun launchEditMode() {
        viewModel.getShopItem(shopItemId)
        viewModel.shopItem.observe(viewLifecycleOwner) {
            etTitle.setText(it.title)
            etCount.setText(it.count.toString())
        }

        btnSave.setOnClickListener {
            val inputTitle = etTitle.text?.toString()
            val inputCount = etCount.text?.toString()
            viewModel.editShopItem(inputTitle, inputCount)
        }
    }

    private fun launchAddMode() {
        btnSave.setOnClickListener {
            val inputTitle = etTitle.text?.toString()
            val inputCount = etCount.text?.toString()
            viewModel.addShopItem(inputTitle, inputCount)
        }
    }

    private fun observeViewModel() {
        viewModel.errorInputTitle.observe(viewLifecycleOwner) {
            if (it)
                tilTitle.error = resources.getString(R.string.invalid_title)
            else
                tilTitle.error = null
        }
        viewModel.errorInputCount.observe(viewLifecycleOwner) {
            if (it)
                tilCount.error = resources.getString(R.string.invalid_count)
            else
                tilCount.error = null
        }

        etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTitle()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            activity?.onBackPressed()
        }
    }

    private fun parseParams() {
        val args = requireArguments()

        if (!args.containsKey(ARG_SCREEN_MODE))
            throw RuntimeException("Param 'screen mode' is absent")

        val mode = args.getString(ARG_SCREEN_MODE)

        if (mode != MODE_EDIT && mode != MODE_ADD) {
            throw RuntimeException("Unknown screen mode $mode")
        }

        screenMode = mode

        if (mode == MODE_EDIT) {
            if (!args.containsKey(ARG_SHOP_ITEM_ID))
                throw RuntimeException("Param 'shop item id' is absent")

            shopItemId = args.getInt(ARG_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
        }
    }

    private fun initViews(view: View) {
        tilTitle = view.findViewById(R.id.til_title)
        tilCount = view.findViewById(R.id.til_count)
        etTitle = view.findViewById(R.id.et_title)
        etCount = view.findViewById(R.id.et_count)
        btnSave = view.findViewById(R.id.btn_save)
    }

    companion object {

        private const val UNDEFINED_SCREEN_MODE = ""
        private const val ARG_SCREEN_MODE = "extra_mode"
        private const val ARG_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"

        fun newInstanceAddItem(): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCREEN_MODE, MODE_ADD)
                }
            }
        }

        fun newInstanceEditItem(shopItemId: Int): ShopItemFragment {
            return ShopItemFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SCREEN_MODE, MODE_EDIT)
                    putInt(ARG_SHOP_ITEM_ID, shopItemId)
                }
            }
        }
    }
}