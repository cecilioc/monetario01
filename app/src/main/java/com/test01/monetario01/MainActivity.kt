package com.test01.monetario01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {

    private lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        result  = findViewById<TextView>(R.id.result_number)

        val buttonConverter = findViewById<Button>(R.id.button_converter)
        buttonConverter.setOnClickListener {
            //result.text = "novo text"
            //result.visibility = View.VISIBLE
            converter()
        }

    }
    private fun converter() {
        val selectedCurrency = findViewById<RadioGroup>(R.id.radio_group)
        val checked = selectedCurrency.checkedRadioButtonId

        val currency = when(checked) {
            R.id.radio_USD -> "USD"
            R.id.radio_EUR -> "EUR"
            else -> "CLP"
        }

        val editField = findViewById<EditText>(R.id.edit_field)

        val value = editField.text.toString()

        if (value.isEmpty())
            return

        //result.text = value
        //result.visibility = View.VISIBLE

        //processo para chamadas que demoram
        Thread{

            //aqui acontece em paralelo
            //essa API responde todas as moedas refentes a 1 dolar

            val url = URL("https://api.freecurrencyapi.com/v1/latest?apikey=BnzmWDl3Iv32W1l6V5zzLj6KMTukCXRGoZiiRriH")

            val conn = url.openConnection() as HttpsURLConnection
            try {
                val info = conn.inputStream.bufferedReader().readText()

                //tranformar em um obj Jason
                val jObj = JSONObject(info)

                runOnUiThread {

                    // API tras valores referentes a 1 dolar.
                    // funcao input reais para Dolar
                    // entra 100 reais -> resultado(USD) = (1 USD x inputBRL)/rateBRL
                    //
                    // dolar para EUR
                    // tenho resultado(EUR) = rateEUR x input
                    //

                    val res = jObj.getJSONObject("data")
                    val BRLrate = res.getDouble("BRL")
                    val EURrate = res.getDouble("EUR")

                    if (currency == "USD"){
                        val r = value.toDouble() / BRLrate
                        result.text = r.toString()
                        result.visibility = View.VISIBLE
                    }
                    if (currency == "EUR"){
                        val rUSD = value.toDouble() / BRLrate
                        val r = rUSD * EURrate
                        result.text = r.toString()
                        result.visibility = View.VISIBLE
                    }


                }


            } finally {
                conn.disconnect()
            }

        }.start()




    }
}