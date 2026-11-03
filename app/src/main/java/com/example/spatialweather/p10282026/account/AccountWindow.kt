package com.example.spatialweather.p10282026.account
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.*
import com.pico.spatial.ui.platform.containers.LocalSpatialNavigator
import com.example.spatialweather.p10282026.ui.weather.components.*
import kotlinx.coroutines.*

private enum class Mode(val title:String){PASSWORD("密码登录"),CODE("验证码登录"),REGISTER("注册")}
@Composable fun AccountWindow(){
 val context=LocalContext.current
 val service=remember{AccountStore.service(context)}
 val nav=LocalSpatialNavigator.current
 val scope=rememberCoroutineScope()
 var mode by remember{mutableStateOf(Mode.PASSWORD)}
 var email by remember{mutableStateOf("")}
 var password by remember{mutableStateOf("")}
 var confirm by remember{mutableStateOf("")}
 var code by remember{mutableStateOf("")}
 var busy by remember{mutableStateOf(false)}
 var message by remember{mutableStateOf("")}
 var cooldown by remember{mutableIntStateOf(0)}
 LaunchedEffect(cooldown){if(cooldown>0){delay(1000);cooldown--}}
 fun run(action:suspend()->Unit){if(busy)return;busy=true;message="";scope.launch{try{action()}catch(e:CancellationException){throw e}catch(e:AccountFailure){message=e.message?:"操作失败"}catch(_:Exception){message="账号服务暂不可用，请重试。"}finally{busy=false}}}
 fun address():String=AccountRules.email(email).also{if(!AccountRules.validEmail(it))throw AccountFailure("请输入有效的邮箱地址。")}
 Panel(Modifier){
  Heading("MindSoul · 登录 / 注册")
  Copy("空间天气 · 脑机接口体验")
  val session=AccountStore.session
  if(session!=null){
   Heading("已登录")
   Copy(session.profile.email)
   Button(onClick={nav.closeWindowContainer()}){Text("返回空间天气")}
   Button(enabled=!busy,onClick={run{try{service.logout(session.accessToken)}finally{AccountStore.signOut()}}}){Text("退出登录")}
  }else{
   Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
    Mode.entries.forEach{item->ToggleableChip(label={Text(item.title)},isToggleOn=mode==item,onClick={if(!busy){mode=item;password="";confirm="";code="";message=""}})}
   }
   Copy("邮箱账户")
   TextField(value=email,onValueChange={if(it.length<=254)email=it},placeholder={Text("name@example.com")},singleLine=true,enabled=!busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),modifier=Modifier.fillMaxWidth())
   if(mode!=Mode.CODE){
    Copy(if(mode==Mode.REGISTER)"设置密码 · 8–128 个字符" else "密码")
    TextField(value=password,onValueChange={if(it.length<=128)password=it},singleLine=true,enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
    if(mode==Mode.REGISTER)TextField(value=confirm,onValueChange={if(it.length<=128)confirm=it},placeholder={Text("再次输入密码")},singleLine=true,enabled=!busy,visualTransformation=PasswordVisualTransformation(),keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),modifier=Modifier.fillMaxWidth())
   }
   if(mode!=Mode.PASSWORD){
    Copy("邮箱验证码")
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)){
     TextField(value=code,onValueChange={if(it.length<=6&&it.all{c->c in '0'..'9'})code=it},placeholder={Text("6 位数字")},singleLine=true,enabled=!busy,keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),modifier=Modifier.weight(1f))
     Button(enabled=!busy&&cooldown==0,onClick={run{cooldown=service.sendCode(address(),if(mode==Mode.REGISTER)CodePurpose.REGISTER else CodePurpose.LOGIN);message="请求已受理，请检查邮箱。"}}){Text(if(cooldown>0)"${cooldown}s 后重发" else "获取验证码")}
    }
   }
   if(!service.configured)Copy("账号服务尚未配置，暂不能发送真实验证码或完成登录注册。你仍可体验空间天气。")
   Button(enabled=!busy,onClick={run{
    val mail=address()
    if(mode!=Mode.PASSWORD&&!AccountRules.validCode(code))throw AccountFailure("请输入 6 位邮箱验证码。")
    if(mode==Mode.REGISTER&&(!AccountRules.validPassword(password)||password!=confirm))throw AccountFailure("密码需为 8–128 个字符，且两次输入一致。")
    if(mode==Mode.PASSWORD&&password.isEmpty())throw AccountFailure("请输入密码。")
    val result=when(mode){Mode.PASSWORD->service.passwordLogin(mail,password);Mode.CODE->service.codeLogin(mail,code);Mode.REGISTER->service.register(mail,password,code)}
    AccountStore.signIn(result);password="";confirm="";code="";message="登录成功。"
   }}){Text(if(busy)"处理中…" else if(mode==Mode.REGISTER)"验证邮箱并注册" else "登录")}
  }
  if(message.isNotEmpty())Copy(message)
  Button(enabled=!busy,onClick={password="";confirm="";code="";nav.closeWindowContainer()}){Text("关闭")}
  Copy("成都市脑云矩阵科技有限公司 · MindSoul 提供")
 }
}
