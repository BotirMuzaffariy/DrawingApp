package uz.lazycoder.drawingapp

import kotlin.math.abs
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import uz.lazycoder.drawingapp.databinding.DialogSizeBinding
import uz.lazycoder.drawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var maxWidth = 0
    private var maxHeight = 0
    private var minSize = 150

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            sbSpeed.max = 600
            sbSpeed.progress = sbSpeed.max

            ivMenu.setOnClickListener {
                val popupMenu = PopupMenu(this@MainActivity, it)

                popupMenu.inflate(R.menu.popup_menu)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) popupMenu.setForceShowIcon(true)

                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.m_clear -> {
                            iv1.clearDraw()
                            iv2.clearDraw()
                        }
                    }
                    return@setOnMenuItemClickListener true
                }

                popupMenu.show()
            }

            btnSize.setOnClickListener {
                minSize = iv1.height / 4
                maxWidth = iv1.width * 2
                maxHeight = iv1.height * 2

                val builder = AlertDialog.Builder(this@MainActivity)
                val dialogBinding = DialogSizeBinding.inflate(layoutInflater)

                builder.setView(dialogBinding.root)
                val dialog = builder.create()
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                dialogBinding.apply {
                    etSize1.setText(iv1.b?.width.toString())
                    etSize2.setText(iv1.b?.height.toString())

                    tvCancel.setOnClickListener { dialog.dismiss() }

                    tvOk.setOnClickListener {
                        val width = etSize1.text.toString()
                        val height = etSize2.text.toString()

                        if (width.isEmpty() || height.isEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Required fields are empty",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (width.toInt() <= minSize || height.toInt() <= minSize) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Size must be higher than $minSize",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (width.toInt() > maxWidth || height.toInt() > maxHeight) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "The size exceeded the set limit",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                iv1.changeSize(width.toInt(), height.toInt())
                                iv2.changeSize(width.toInt(), height.toInt())
                                dialog.dismiss()
                            }
                        }
                    }
                }

                dialog.show()
            }

            btnGenerate.setOnClickListener {
                iv1.startDraw(spn1.selectedItemPosition, abs(sbSpeed.progress - sbSpeed.max))
                iv2.startDraw(spn2.selectedItemPosition, abs(sbSpeed.progress - sbSpeed.max))
            }
        }
    }

}