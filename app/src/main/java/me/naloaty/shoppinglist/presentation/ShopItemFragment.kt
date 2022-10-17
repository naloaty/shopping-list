package me.naloaty.shoppinglist.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.naloaty.shoppinglist.databinding.FragmentShopItemBinding
import me.naloaty.shoppinglist.domain.ShopItem

class ShopItemFragment: Fragment() {

    private var _binding: FragmentShopItemBinding? = null
    private val binding: FragmentShopItemBinding
        get() = _binding ?: throw RuntimeException("FragmentShopItemBinding is null")

    private val viewModel by lazy {
        ViewModelProvider(this)[ShopItemViewModel::class.java]
    }

    private lateinit var onEditingFinishedListener: EditingFinishedListener

    private var screenMode: String = UNDEFINED_SCREEN_MODE
    private var shopItemId: Int = ShopItem.UNDEFINED_ID

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EditingFinishedListener) {
            onEditingFinishedListener = context
        } else {
            throw RuntimeException("Activity must implement EditingFinishedListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseParams()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
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

        binding.btnSave.setOnClickListener {
            val inputTitle = binding.etTitle.text?.toString()
            val inputCount = binding.etCount.text?.toString()
            viewModel.editShopItem(inputTitle, inputCount)
        }
    }

    private fun launchAddMode() {
        binding.btnSave.setOnClickListener {
            val inputTitle = binding.etTitle.text?.toString()
            val inputCount = binding.etCount.text?.toString()
            viewModel.addShopItem(inputTitle, inputCount)
        }
    }

    private fun observeViewModel() {
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTitle()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.etCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputCount()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        viewModel.closeScreen.observe(viewLifecycleOwner) {
            onEditingFinishedListener.onEditingFinished()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface EditingFinishedListener {
        fun onEditingFinished()
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