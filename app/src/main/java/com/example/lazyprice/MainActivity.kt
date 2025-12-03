package com.example.lazyprice

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    // Функция скрытия клавиатуры
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация элементов UI
        val weight1 = findViewById<EditText>(R.id.weight1)
        val price1 = findViewById<EditText>(R.id.price1)
        val weight2 = findViewById<EditText>(R.id.weight2)
        val price2 = findViewById<EditText>(R.id.price2)
        val compareButton = findViewById<Button>(R.id.compareButton)
        val resultText = findViewById<TextView>(R.id.resultText)
        val resetButton = findViewById<Button>(R.id.resetButton) // Кнопка «Сброс»

        // Обработчик нажатия кнопки «Сравнить»
        compareButton.setOnClickListener {
            hideKeyboard()
            val w1 = weight1.text.toString().replace(',', '.').toDoubleOrNull()
            val p1 = price1.text.toString().replace(',', '.').toDoubleOrNull()
            val w2 = weight2.text.toString().replace(',', '.').toDoubleOrNull()
            val p2 = price2.text.toString().replace(',', '.').toDoubleOrNull()

            if (w1 == null || p1 == null || w2 == null || p2 == null) {
                resultText.text = "Пожалуйста, введите корректные числа во все поля."
                return@setOnClickListener
            }

            if (w1 <= 0.0 || w2 <= 0.0) {
                resultText.text = "Вес должен быть положительным."
                return@setOnClickListener
            }

            val item1 = Item(w1, p1)
            val item2 = Item(w2, p2)

            val output = comparePrices(item1, item2)
            resultText.text = output
        }

        // Обработчик нажатия кнопки «Сброс»
        resetButton.setOnClickListener {
            resetFields()
        }
    }

    // Функция сброса полей ввода и очистки результата
    private fun resetFields() {
        findViewById<EditText>(R.id.weight1).text.clear()
        findViewById<EditText>(R.id.price1).text.clear()
        findViewById<EditText>(R.id.weight2).text.clear()
        findViewById<EditText>(R.id.price2).text.clear()
        findViewById<TextView>(R.id.resultText).text = ""
        hideKeyboard()
    }

    // Класс для хранения данных о товаре
    data class Item(val weight: Double, val price: Double)

    // Расчёт цены за единицу веса
    private fun calculateUnitPrice(weight: Double, price: Double): Double {
        return price / weight
    }

    // Сравнение цен и формирование результата
    private fun comparePrices(item1: Item, item2: Item): String {
        val unitPrice1 = calculateUnitPrice(item1.weight, item1.price)
        val unitPrice2 = calculateUnitPrice(item2.weight, item2.price)

        val sb = StringBuilder()
        sb.append(String.format("Цена грамма Товара1: %.2f_₽  \n", unitPrice1))
        sb.append(String.format("Цена грамма Товара2: %.2f_₽ \n\n", unitPrice2))

        return when {
            unitPrice1 < unitPrice2 -> {
                val item2CostAt1 = unitPrice1 * item2.weight
                val item2CostAt2 = unitPrice2 * item2.weight
                val overpay = item2CostAt1 - item2CostAt2
                sb.append("Товар1 выгоднее.\n\n")
                sb.append(String.format("Товар2 по цене Товара1 \nстоил бы: %.2f_₽,\n_        а не: %.2f_₽.\n\n",
                    item2CostAt1, item2CostAt2))
                sb.append(String.format("Переплата: %.2f_₽", abs(overpay)))
                sb.toString()
            }
            unitPrice1 > unitPrice2 -> {
                val item1CostAt2 = unitPrice2 * item1.weight
                val item1CostAt1 = unitPrice1 * item1.weight
                val overpay = item1CostAt2 - item1CostAt1
                sb.append("Товар2 выгоднее.\n\n")
                sb.append(String.format(
                    "Товар1 по цене Товара2 \nстоил бы: %.2f_₽,\n_        а не: %.2f_₽.\n\n",
                    item1CostAt2, item1CostAt1
                ))
                sb.append(String.format("Переплата: %.2f_₽", abs(overpay)))
                sb.toString()
            }
            else -> {
                sb.append("Цены равны.")
                sb.toString()
            }
        }
    }
}
