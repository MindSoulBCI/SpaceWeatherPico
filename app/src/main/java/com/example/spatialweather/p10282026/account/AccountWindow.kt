package com.example.spatialweather.p10282026.account
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialweather.p10282026.R
import kotlinx.coroutines.*

private enum class Page { LOGIN, REGISTER }

@Composable fun AccountWindow(){
 val context=LocalContext.current
 val service=remember{AccountStore.service(context)}
 val nav=LocalSpatialNavigator.current
 val scope=rememberCoroutineScope()
 var page by remember{mutableStateOf(Page.LOGIN)}
 var email by remember{mutableStateOf("")}
 var password by remember{mutableStateOf("")}
 var confirm by remember{mutableStateOf("")}
 var code by remember{mutableStateOf("")}
 var busy by remember{mutableStateOf(false)}
 var message by remember{mutableStateOf("")}
 var cooldown by remember{mutableIntStateOf(0)}
 LaunchedEffect(cooldown){if(cooldown>0){delay(1000);cooldown--}}
 fun run(block:suspend ()->Unit){
  if(busy)return
  busy=true;message=""
  scope.launch{try{block()}catch(e:CancellationException){throw e}catch(e:AccountFailure){message=e.message?:"操作失败"}catch(_:Exception){message="账号服务暂不可用，请重试。"}finally{busy=false}}
 }
 Column(
  Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(32.dp),
  horizontalAlignment=Alignment.CenterHorizontally,
  verticalArrangement=Arrangement.spacedBy(16.dp)
 ){
  Image(painterResource(R.drawable.mindsoul_weather_icon),"空间天气",Modifier.size(96.dp))
  Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
   ToggleableChip(label={Text("登录")},isToggleOn=page==Page.LOGIN,onClick={if(!busy){page=Page.LOGIN;password="";confirm="";code="";message=""}})
   ToggleableChip(label={Text("注册")},isToggleOn=page==Page.REGISTER,onClick={if(!busy){page=Page.REGISTER;password="";confirm="";code="";message=""}})
  }
  TextField(value=email,onValueChange={if(it.length<=254)email=it},placeholder={Text("邮箱")},singleLine=true,enabled=!busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),modifier=Modifier.fillMaxWidth())
  TextField(value=password,onValueChange={if(it.length<=20)password=it},placeholder={Text(if(page==Page.REGISTER)"密码，6–20 位字母或数字" else "密码")},singleLine=true,enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
  if(page==Page.REGISTER){
   TextField(value=confirm,onValueChange={if(it.length<=20)confirm=it},placeholder={Text("再次输入密码")},singleLine=true,enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
   Row(horizontalArrangement=Arrangement.spacedBy(12.dp), verticalAlignment=Alignment.CenterVertically){
    TextField(value=code,onValueChange={if(it.length<=6&&it.all(Char::isDigit))code=it},placeholder={Text("邮箱验证码")},singleLine=true,enabled=!busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),modifier=Modifier.weight(1f))
    Button(enabled=!busy&&cooldown==0,onClick={run{
     val mail=AccountRules.email(email)
     if(!AccountRules.validEmail(mail)) throw AccountFailure("请输入有效的邮箱地址。")
     cooldown=service.sendCode(mail, CodePurpose.REGISTER)
     message="验证码已发送。"
    }}){Text(if(cooldown>0)"${cooldown}s" else "获取验证码")}
   }
  }
  if(message.isNotEmpty()) Text(message)
  Button(enabled=!busy,onClick={run{
   val mail=AccountRules.email(email)
   if(!AccountRules.validEmail(mail)) throw AccountFailure("请输入有效的邮箱地址。")
   if(page==Page.REGISTER){
    if(!AccountRules.validCode(code)) throw AccountFailure("请输入 6 位邮箱验证码。")
    if(!AccountRules.validPassword(password)) throw AccountFailure("密码需为 6–20 位字母或数字。")
    if(password!=confirm) throw AccountFailure("两次输入的密码不一致。")
    AccountStore.signIn(service.register(mail,password,code))
   }else{
    if(password.isEmpty()) throw AccountFailure("请输入密码。")
    AccountStore.signIn(service.passwordLogin(mail,password))
   }
   nav.closeWindowContainer()
  }}){Text(if(busy)"处理中…" else if(page==Page.REGISTER)"注册" else "登录")}
 }
}
