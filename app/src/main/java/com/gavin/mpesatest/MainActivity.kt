package com.gavin.mpesatest

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidstudy.daraja.Daraja
import com.androidstudy.daraja.DarajaListener
import com.androidstudy.daraja.model.AccessToken
import com.androidstudy.daraja.model.LNMExpress
import com.androidstudy.daraja.model.LNMResult
import com.androidstudy.daraja.util.Env
import com.androidstudy.daraja.util.TransactionType
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MpesaListener {

    lateinit var daraja: Daraja
    companion object{
        lateinit var mpesaListener: MpesaListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        daraja = Daraja.with(
            "vMULGsDZpcqbJpKgD7gtxmBqOgautXpM",
            "waiAHMnzGAm22unz",
            Env.SANDBOX,
            object : DarajaListener<AccessToken> {
                override fun onResult(accessToken: AccessToken) {
                    showToast("MPESA TOKEN :${accessToken.access_token}")
                }

                override fun onError(error: String?) {
                    showToast("error: $error")
                }
            }
        )



        pay_button.setOnClickListener {
            val phoneNumber = phone.text.trim().toString().trim()
            val lnmExpress = LNMExpress(
                "174379",
                "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",
                TransactionType.CustomerPayBillOnline,
                "1",
                phoneNumber,
                "174379",
                phoneNumber,
                "https://us-central1-mpesa-app.cloudfunctions.net/api/myCallbackUrl",
                "001ABC",
                "Goods Payment"
            )
            daraja.requestMPESAExpress(lnmExpress, object : DarajaListener<LNMResult> {
                override fun onResult(lnmResult: LNMResult) {

                    FirebaseMessaging.getInstance()
                        .subscribeToTopic(lnmResult.CheckoutRequestID.toString())

                    showToast("Response here ${lnmResult.ResponseDescription}")
                }

                override fun onError(error: String?) {
                    showToast("Error here: $error")
                }
            })
        }
        mpesaListener = this
    }
    private fun showToast(message:String){
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
    }

    override fun sendSuccesfull(amount: String, phone: String, date: String, receipt: String) {
        runOnUiThread {
            Toast.makeText(
                this, "Payment Succesfull\n" +
                        "Receipt: $receipt\n" +
                        "Date: $date\n" +
                        "Phone: $phone\n" +
                        "Amount: $amount", Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun sendFailed(reason: String) {
        runOnUiThread {
            Toast.makeText(
                this, "Payment Failed\n" +
                        "Reason: $reason"
                , Toast.LENGTH_LONG
            ).show()
        }
    }

}