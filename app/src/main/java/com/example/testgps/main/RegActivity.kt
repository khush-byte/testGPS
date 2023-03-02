package com.example.testgps.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testgps.connection.*
import com.example.testgps.databinding.ActivityRegBinding
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest


class RegActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegBinding.inflate(layoutInflater)
    }
    var gender: String = "male"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val items = arrayOf("male", "female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        binding.userGender.adapter = adapter

        binding.userGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = binding.userGender.selectedItem.toString()
            }
        }

        binding.btnReg.setOnClickListener {
            if (binding.userQrField.text.length == 5 && binding.userNameField.text.isNotEmpty()
                && binding.userRegionField.text.isNotEmpty() && binding.userDistrictField.text.isNotEmpty()
                && binding.userJamoatField.text.isNotEmpty() && binding.userVillageField.text.isNotEmpty() && binding.userPhoneField.text.length == 9) {

                if(CheckConnection.isOnline(this)) {
                    val response = ServiceBuilder.buildService(ApiInterfaceReg::class.java)
                    val requestModel = RequestModelReg(
                        md5("${binding.userPhoneField.text}${binding.userQrField.text}bCctS9eqoYaZl21a"),
                        "${binding.userQrField.text}",
                        "${binding.userNameField.text}",
                        gender,
                        "${binding.userRegionField.text}",
                        "${binding.userDistrictField.text}",
                        "${binding.userJamoatField.text}",
                        "${binding.userVillageField.text}",
                        "${binding.userPhoneField.text}")
                    Log.d("myTag", requestModel.toString())

                    response.sendReq(requestModel).enqueue(
                        object : Callback<ResponseModel> {
                            override fun onResponse(
                                call: Call<ResponseModel>,
                                response: Response<ResponseModel>
                            ) {
                                if(response.body()?.result.equals("0")) {
                                    Log.d("myTag", parseMsg("${response.body()?.msg}"))
                                    if(parseMsg("${response.body()?.msg}") == "ok"){
                                        saveUserInfo(binding.userQrField.text.toString(), binding.userPhoneField.text.toString())
                                        Toast.makeText(applicationContext, "Registered successfully!",Toast.LENGTH_LONG).show()
                                    }else if (parseMsg("${response.body()?.msg}") == "exist"){
                                        Toast.makeText(applicationContext, "QR id already registered!",Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(applicationContext, "System Error!",Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                                Toast.makeText(applicationContext,t.toString(),Toast.LENGTH_LONG).show()
                                Log.d("myTag", "$t")
                            }
                        }
                    )
                }else{
                    Toast.makeText(applicationContext,"No Internet Connection!",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(applicationContext,"Please fill in all fields correctly!",Toast.LENGTH_LONG).show()
            }

//        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                moveTaskToBack(true);
//                exitProcess(-1)
//            }
//        })
        }
    }

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun parseMsg(msg: String): String {
        val text = msg.substring(1, msg.length-1).split(",")
        val obj = JSONObject("{${text[0]}}")
        return obj.getString("response")
    }

    private fun saveUserInfo(qr: String, phone: String){
        val sharedPreference = getSharedPreferences("LocalMemory", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putInt("regState", 1)
        editor.putString("phone", phone)
        editor.putString("qr", qr)
        editor.apply()
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}