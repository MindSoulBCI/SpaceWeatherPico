package com.example.spatialweather.p10282026.account
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
class AccountRulesTest {
 @Test fun emailValidationRejectsMalformedAndWhitespace(){
  assertTrue(AccountRules.validEmail(AccountRules.email(" pilot+test@example.com ")))
  listOf("", "pilot", "pilot@", "@example.com", "a b@example.com", "a@example.com\nBcc:x@y.com").forEach{assertFalse(AccountRules.validEmail(it))}
 }
 @Test fun registrationPasswordAndVerificationCodeBoundaries(){
  assertFalse(AccountRules.validPassword("1234567"));assertTrue(AccountRules.validPassword("long password"));assertFalse(AccountRules.validPassword("x".repeat(129)))
  assertTrue(AccountRules.validCode("012345"));assertFalse(AccountRules.validCode("12345"));assertFalse(AccountRules.validCode("12 345"));assertFalse(AccountRules.validCode("abcdef"))
 }
 @Test fun profileRestrictsAvatarAndDisplayName(){
  assertTrue(AccountRules.validProfile("飞行员","pilot"));assertFalse(AccountRules.validProfile(" ","pilot"));assertFalse(AccountRules.validProfile("飞行员","https://remote/avatar"));assertFalse(AccountRules.validProfile("x".repeat(25),"orbit"))
 }
 @Test fun unconfiguredBackendNeverReturnsFakeSuccess()=runBlocking {
  val api=UnconfiguredAccountService();assertFalse(api.configured)
  val operations:List<suspend ()->Any?> = listOf(
   {api.sendCode("a@example.com",CodePurpose.LOGIN)},
   {api.register("a@example.com","password123","123456")},
   {api.passwordLogin("a@example.com","password123")},
   {api.codeLogin("a@example.com","123456")},
   {api.resetPassword("a@example.com","123456","newpassword")},
   {api.updateProfile("token","name","pilot")})
  operations.forEach{operation->try{operation();fail("Unconfigured backend must fail closed")}catch(e:AccountFailure){assertTrue(e.message!!.contains("尚未配置"))}}
 }
 @Test fun httpAdapterRejectsInsecureEndpoint(){
  listOf("http://example.com","https://user:password@example.com").forEach{url->try{HttpAccountService(url);fail("Insecure endpoint accepted")}catch(_:IllegalArgumentException){}}
 }
}
