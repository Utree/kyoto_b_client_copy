package com.kyotob.client.chatList

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.kyotob.client.Client
import com.kyotob.client.R
import com.kyotob.client.baseUrl
import com.kyotob.client.entities.AddUserRequest
import com.kyotob.client.entities.AddUserResponse
import com.kyotob.client.entities.SearchUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class PairFragment: Fragment() {

    lateinit var dialogEditText: EditText
    lateinit var addUserButton: Button
    lateinit var notFoundView: TextView
    lateinit var foundView: ConstraintLayout
    lateinit var foundText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.dialog_pair, null)
        with(root) {
            // ユーザー検索欄
            dialogEditText = findViewById(R.id.dialog_edit_text)
            // ユーザー追加ボタン
            addUserButton = findViewById(R.id.addUser)
            notFoundView = findViewById(R.id.dialog_not_found_text_view)
            foundView = findViewById(R.id.dialog_found_user)
            foundText = findViewById(R.id.dialog_user_name_text_view)
        }

        /* JSON のスネークケースで表現されるフィールド名を、
           Java オブジェクトでキャメルケースに対応させるための設定 */
        val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
        // クライアントの実装の生成
        val client = retrofit.create(Client::class.java)


        // エンターキー押下時の挙動
        dialogEditText.setOnKeyListener { _, keyCode, event ->
            (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN).apply {

                // 通信
                client.searchUser(dialogEditText.text.toString(), "foo").enqueue(object : Callback<SearchUserResponse> {
                    // Request成功時に呼ばれる
                    override fun onResponse(call: Call<SearchUserResponse>, response: Response<SearchUserResponse>) {

                        // 通信成功時
                        if(response.isSuccessful) {
                            // TEST
                            // ユーザー表示名の変更
                            foundText.text = response.body()!!.screenName

                            foundView.visibility = View.VISIBLE
                            notFoundView.visibility = View.INVISIBLE
                        }
                        // Bad request
                        else {
                            foundView.visibility = View.INVISIBLE
                            notFoundView.visibility = View.VISIBLE
                        }
                    }

                    // Request失敗時に呼ばれる
                    override fun onFailure(call: Call<SearchUserResponse>?, t: Throwable?) {
                        // Fail to connect Internet access
                        Toast.makeText(context, "Fail to Connect Internet Access", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        addUserButton.setOnClickListener {
            // roomの追加
            client.makeRoom(AddUserRequest("foo", dialogEditText.text.toString()), "aaa").enqueue(object : Callback<AddUserResponse> {
                // Request成功時に呼ばれる
                override fun onResponse(call: Call<AddUserResponse>, response: Response<AddUserResponse>) {
                    // 通信成功時
                    if(response.isSuccessful) {
//                        Toast.makeText(context, "Successful", Toast.LENGTH_LONG).show()
                    }
                    // Bad request
                    else {
//                        Toast.makeText(context, "Bad request", Toast.LENGTH_LONG).show()
                    }
                }

                // Request失敗時に呼ばれる
                override fun onFailure(call: Call<AddUserResponse>?, t: Throwable?) {
                    // Fail to connect Internet access
//                    Toast.makeText(context, "Fail to Connect Internet Access", Toast.LENGTH_LONG).show()
                }
            })
            // ダイアログを閉じる
            //dialog.dismiss()
        }
        return root
    }
}
