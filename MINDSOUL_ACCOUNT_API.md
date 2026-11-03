# MindSoul 账号接入层

## 当前状态

客户端界面及 HTTPS 适配层已实现，服务地址默认留空。没有创建后台、SMTP 服务、真实邮箱账户或默认测试密码。空配置使用 UnconfiguredAccountService，所有认证操作明确失败；不生成本地验证码、不假装发送成功。游客头像/昵称可保存于本机；游客不代表注册或登录。

支持邮箱密码登录、邮箱验证码登录、邮箱验证码注册、通过邮箱验证码重置密码、预设头像与昵称更新、退出登录。头像当前为四款内置矢量头像（pilot/orbit/spark/explorer），不含相册上传。登录 token 只保留在进程内存，不持久化；重启后重新登录。账号服务尚未连接，不能声称端到端认证已验证。

## 配置

在 app/src/main/res/values/strings.xml 中设置 mindsoul_account_api 为自有 HTTPS 服务根地址，如 https://accounts.example.com。禁止 HTTP 或 URL 内嵌账号密码。不要在 APK 里配置 SMTP 凭据、服务端密钥、Supabase service-role 等秘密。若已有服务接口不一致，可替换 AccountService 的实现，无需重写 UI。网络权限已声明。

所有请求 POST，Content-Type: application/json。profile/logout 通过 Authorization: Bearer <access_token> 传令牌。客户端不跟随重定向，不记录凭据或响应正文，连接/读取超时为 10/15 秒，响应上限 64 KiB。失败响应只展示本地通用信息。

## API 约定

| 路径 | 请求 JSON | 成功响应 |
|---|---|---|
| /v1/auth/email-code | email, purpose | {"retry_after_seconds":60}；purpose 为 register/login/reset_password |
| /v1/auth/register | email, password, code | Session；完成邮箱验证并创建账号 |
| /v1/auth/login/password | email, password | Session |
| /v1/auth/login/code | email, code | Session |
| /v1/auth/password/reset | email, code, new_password | 200 {} 或 204；客户端回登录页 |
| /v1/account/profile | display_name, avatar_id | {"user": User}；需要 Bearer token |
| /v1/auth/logout | {} | 200 {} 或 204；需要 Bearer token，撤销服务器会话 |

Session 示例（字段必须完整）：

```json
{
  "access_token": "server-issued-token",
  "user": {
    "id": "server-user-id",
    "email": "pilot@example.com",
    "display_name": "空间旅人",
    "avatar_id": "pilot"
  }
}
```

错误状态：400/422 信息或验证码无效，401/403 凭据无效或会话过期，409 无法注册，429 限流，其余显示服务暂不可用。发送验证码成功后的提示使用“请求已受理，若邮箱符合条件将收到验证码”，不声称邮件已经送达。

## 服务端必须实现

密码安全哈希与验证；验证码绑定邮箱与 purpose、短时有效、单次消费、错误次数限制；邮件发送与服务端重发限流；注册时验证邮箱；重置密码后撤销既有会话；profile 依据令牌身份更新，禁止客户端指定其他用户；token 过期与注销撤销。邮箱发送/找回接口使用一致响应以减少账号枚举。客户端格式验证和倒计时只是交互辅助，不能替代这些服务端控制。

客户端注册/重置要求 8–128 字符密码、两次一致；验证码为 6 位 ASCII 数字；昵称 1–24 字；avatar_id 限定四款。后端需执行相同或更严格规则。

## 窗口与验证

启动先显示 MindSoulAccount 原生 Planar 窗口，游客试玩或登录后“进入飞车”打开控制台。控制台“账户与头像”可重开。MindSoulAccount 在 PicoTheme 下使用系统默认 Material.Regular，无自绘根背景；TextField/Option/Button 使用原生颜色、状态与 hover；头像 Icon 使用 labelPrimary。没有原生色板覆盖或自定义 hover。

新增 5 项单测覆盖邮箱、密码、验证码、头像资料约束、未配置服务拒绝所有认证动作和不安全 URL 拒绝。共 20 项功能测试通过（另有 1 项模板测试）。构建和 SpatialUI verifier 通过，模拟器确认登录页显示。尚未验证空间键盘输入与各页完整点击流程、实际 SMTP 收信、远程注册/登录/重置/退出。截图 MindSoul-Account.png 是真实模拟器画面。
