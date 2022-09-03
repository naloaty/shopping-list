package me.naloaty.shoppinglist.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import me.naloaty.shoppinglist.R
import me.naloaty.shoppinglist.domain.ShopItem
import java.lang.RuntimeException

class ShopItemActivity : AppCompatActivity() {

    private lateinit var viewModel: ShopItemViewModel

    private lateinit var tilTitle: TextInputLayout
    private lateinit var tilCount: TextInputLayout
    private lateinit var etTitle: EditText
    private lateinit var etCount: EditText
    private lateinit var btnSave: Button

    private var screenMode = UNDEFINED_SCREEN_MODE
    private var shopItemId = ShopItem.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_item)
        parseIntent()
        viewModel = ViewModelProvider(this)[ShopItemViewModel::class.java]
        initViews()
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
        viewModel.shopItem.observe(this) {
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
        viewModel.errorInputTitle.observe(this) {
            if (it)
                tilTitle.error = resources.getString(R.string.invalid_title)
            else
                tilTitle.error = null
        }
        viewModel.errorInputCount.observe(this) {
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

        viewModel.closeScreen.observe(this) {
            finish()
        }
    }

    private fun parseIntent() {
        if (!intent.hasExtra(EXTRA_SCREEN_MODE))
            throw RuntimeException("Param 'screen mode' is absent")

        val mode = intent.getStringExtra(EXTRA_SCREEN_MODE)

        when(mode) {
            MODE_EDIT -> {
                if (!intent.hasExtra(EXTRA_SHOP_ITEM_ID))
                    throw RuntimeException("Param 'shop item id' is absent")
                shopItemId = intent.getIntExtra(EXTRA_SHOP_ITEM_ID, ShopItem.UNDEFINED_ID)
            }

            MODE_ADD -> {}

            else -> throw RuntimeException("Unknown screen mode $mode")
        }

        screenMode = mode
    }

    private fun initViews() {
        tilTitle = findViewById(R.id.til_title)
        tilCount = findViewById(R.id.til_count)
        etTitle = findViewById(R.id.et_title)
        etCount = findViewById(R.id.et_count)
        btnSave = findViewById(R.id.btn_save)
    }

    companion object {

        private const val UNDEFINED_SCREEN_MODE = ""
        private const val EXTRA_SCREEN_MODE = "extra_mode"
        private const val EXTRA_SHOP_ITEM_ID = "extra_shop_item_id"
        private const val MODE_EDIT = "mode_edit"
        private const val MODE_ADD = "mode_add"

        fun newIntentAddItem(context: Context): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_ADD)
            return intent
        }

        fun newIntentEditItem(context: Context, shopItemId: Int): Intent {
            val intent = Intent(context, ShopItemActivity::class.java)
            intent.putExtra(EXTRA_SCREEN_MODE, MODE_EDIT)
            intent.putExtra(EXTRA_SHOP_ITEM_ID, shopItemId)
            return intent
        }
    }
}