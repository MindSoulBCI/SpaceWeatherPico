package com.example.spatialweather.p10282026.account

/** Backend-neutral contract. Verification and password hashing belong to the server. */
enum class CodePurpose(val wire:String){REGISTER("register"),LOGIN("login"),RESET("reset_password")}
data class AccountProfile(val id:String,val email:String,val displayName:String,val avatar:String)
data class AccountSession(val accessToken:String,val profile:AccountProfile)
class AccountFailure(message:String):Exception(message)
interface AccountService {
 val configured:Boolean
 suspend fun sendCode(email:String,purpose:CodePurpose):Int
 suspend fun register(email:String,password:String,code:String):AccountSession
 suspend fun passwordLogin(email:String,password:String):AccountSession
 suspend fun codeLogin(email:String,code:String):AccountSession
 suspend fun resetPassword(email:String,code:String,newPassword:String)
 suspend fun updateProfile(token:String,name:String,avatar:String):AccountProfile
 suspend fun logout(token:String)
}
object AccountRules {
 val avatars=listOf("pilot","orbit","spark","explorer")
 fun email(value:String):String=value.trim()
 fun validEmail(value:String)=value.length<=254&&Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(value)
 /** 与 space-forum-server 一致：6–20 位字母或数字 */
 fun validPassword(value:String)=value.length in 6..20 && value.all{it.isLetterOrDigit()}
 fun validCode(value:String)=Regex("^[0-9]{6}$").matches(value)
 fun validProfile(name:String,avatar:String)=name.trim().length in 1..24&&avatar in avatars
}
class UnconfiguredAccountService:AccountService {
 override val configured=false
 private fun unavailable():Nothing=throw AccountFailure("账号服务尚未配置。当前不能注册、登录、发送邮件或重置密码，可先以游客体验。")
 override suspend fun sendCode(email:String,purpose:CodePurpose):Int=unavailable()
 override suspend fun register(email:String,password:String,code:String):AccountSession=unavailable()
 override suspend fun passwordLogin(email:String,password:String):AccountSession=unavailable()
 override suspend fun codeLogin(email:String,code:String):AccountSession=unavailable()
 override suspend fun resetPassword(email:String,code:String,newPassword:String):Unit=unavailable()
 override suspend fun updateProfile(token:String,name:String,avatar:String):AccountProfile=unavailable()
 override suspend fun logout(token:String):Unit=unavailable()
}
