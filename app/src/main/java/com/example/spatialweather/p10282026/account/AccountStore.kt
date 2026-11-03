package com.example.spatialweather.p10282026.account
import android.content.Context
import androidx.compose.runtime.*
import com.example.spatialweather.p10282026.R
object AccountStore {
 var session by mutableStateOf<AccountSession?>(null);private set
 var guestName by mutableStateOf("空间旅人");private set
 var guestAvatar by mutableStateOf("pilot");private set
 fun service(context:Context):AccountService {
  val base=context.getString(R.string.mindsoul_account_api).trim()
  return if(base.isEmpty())UnconfiguredAccountService() else try{HttpAccountService(base)}catch(_:Exception){UnconfiguredAccountService()}
 }
 fun loadGuest(context:Context){val p=context.getSharedPreferences("guest_profile",Context.MODE_PRIVATE);guestName=p.getString("name","空间旅人")?:"空间旅人";guestAvatar=p.getString("avatar","pilot")?:"pilot"}
 fun saveGuest(context:Context,name:String,avatar:String){require(AccountRules.validProfile(name,avatar));guestName=name.trim();guestAvatar=avatar;context.getSharedPreferences("guest_profile",Context.MODE_PRIVATE).edit().putString("name",guestName).putString("avatar",avatar).apply()}
 fun signIn(value:AccountSession){session=value}
 fun update(value:AccountProfile){session=session?.copy(profile=value)}
 fun signOut(){session=null}
}
