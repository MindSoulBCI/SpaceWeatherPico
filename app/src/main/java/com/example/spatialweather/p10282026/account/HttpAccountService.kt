package com.example.spatialweather.p10282026.account

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/** No credentials, OTPs or responses are logged or persisted by this adapter. */
class HttpAccountService(baseUrl:String):AccountService {
 private val base=baseUrl.trimEnd('/')
 init {require(URL(base).protocol=="https" && URL(base).userInfo==null){"Account API must use HTTPS without embedded credentials"}}
 override val configured=true
 private suspend fun post(path:String,body:JSONObject,token:String?=null):JSONObject=withContext(Dispatchers.IO){
  val connection=URL(base+path).openConnection() as HttpsURLConnection
  try {
   connection.requestMethod="POST";connection.connectTimeout=10000;connection.readTimeout=15000
   connection.instanceFollowRedirects=false;connection.doOutput=true
   connection.setRequestProperty("Content-Type","application/json; charset=utf-8")
   if(token!=null)connection.setRequestProperty("Authorization","Bearer $token")
   connection.outputStream.use{it.write(body.toString().toByteArray(Charsets.UTF_8))}
   val status=connection.responseCode
   if(status !in 200..299)throw AccountFailure(when(status){401,403->"登录凭据无效或会话已过期，请重新登录。";409->"该邮箱无法注册，请尝试登录或找回密码。";429->"操作过于频繁，请稍后再试。";400,422->"信息或验证码无效，请检查后重试。";else->"账号服务暂时不可用，请稍后重试。"})
   val bytes=connection.inputStream.use{it.readNBytes(65537)}
   if(bytes.size>65536)throw AccountFailure("账号服务响应异常。")
   if(bytes.isEmpty())JSONObject() else JSONObject(String(bytes,Charsets.UTF_8))
  } catch(e:CancellationException){throw e} catch(e:AccountFailure){throw e}
   catch(e:Exception){throw AccountFailure("无法连接账号服务或响应无效，请检查网络与服务配置。")}
  finally {connection.disconnect()}
 }
 private fun body(vararg fields:Pair<String,String>)=JSONObject().apply{fields.forEach{put(it.first,it.second)}}
 private fun profile(json:JSONObject):AccountProfile=AccountProfile(json.getString("id"),json.getString("email"),json.getString("display_name"),json.getString("avatar_id"))
 private fun session(json:JSONObject):AccountSession {
  val token=json.getString("access_token")
  if(token.isBlank()||token.any{it=='\r'||it=='\n'})throw AccountFailure("账号服务返回了无效会话。")
  return AccountSession(token,profile(json.getJSONObject("user")))
 }
 override suspend fun sendCode(email:String,purpose:CodePurpose):Int=post("/v1/auth/email-code",body("email" to email,"purpose" to purpose.wire)).optInt("retry_after_seconds",60).coerceIn(30,3600)
 override suspend fun register(email:String,password:String,code:String)=session(post("/v1/auth/register",body("email" to email,"password" to password,"code" to code)))
 override suspend fun passwordLogin(email:String,password:String)=session(post("/v1/auth/login/password",body("email" to email,"password" to password)))
 override suspend fun codeLogin(email:String,code:String)=session(post("/v1/auth/login/code",body("email" to email,"code" to code)))
 override suspend fun resetPassword(email:String,code:String,newPassword:String){post("/v1/auth/password/reset",body("email" to email,"code" to code,"new_password" to newPassword))}
 override suspend fun updateProfile(token:String,name:String,avatar:String)=profile(post("/v1/account/profile",body("display_name" to name,"avatar_id" to avatar),token).getJSONObject("user"))
 override suspend fun logout(token:String){post("/v1/auth/logout",JSONObject(),token)}
}
