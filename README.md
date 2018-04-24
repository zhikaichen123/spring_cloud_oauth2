## 1.架构图
 - Euraka注册中心集群
 - Zuul网关集群
 - 各模块微服务集群
 - Nginx实现负载均衡
 - Spring Cloud Config 统一配置中心
 - Monitor微服务监控
 
注意：本demo需要一定的spring cloud基础

 

项目构建工具：gradle-4.6

配置目录 -> D:/gradle

构建项目
双击 eclipse.bat

打包项目
双击 build.bat

一：首先hosts文件需要添加以下域名
127.0.0.1       register
127.0.0.1       register1
127.0.0.1       register2


二：启动注册服务中心
register -> node-1.bat +node-2.bat


服务url：
http://register2:9011   or   http://register1:9011


三：启动网关服务器
gateway  -> node-1.bat

四：启动权限认证
auth-center -> node-1.bat

五.启动resource服务
resource  ->  ResourceApplication

六.启动分布式链路跟踪服务(zipkin,只在getway收集信息即可)
monitor  ->   MonitorApplication

访问地址：
http://localhost:9050


1.获取token
client模式(账号信息来自 表: oauth_client_details ->  client_id  + client_secret)：
http://localhost:9030/uaa/oauth/token?grant_type=client_credentials&scope=select&client_id=client_1&client_secret=123456
获得
{"access_token":"aa76f57b-77a5-4a5f-89e2-137f026d2712","token_type":"bearer","expires_in":43148,"scope":"select"}



password模式： (账号信息来自数据库表: oauth_client_details + rc_user, 内部使用了BCryptPasswordEncoder加密，原始密码是123456)：
http://localhost:9030/uaa/oauth/token?username=admin&password=123456&grant_type=password&scope=select&client_id=client_2&client_secret=123456
 响应如下： 
{"access_token":"2b81db72-f5c9-4676-b97a-7aec45f02b34","token_type":"bearer","refresh_token":"57e6d057-7b0c-46c6-ab79-b6521e369e25","expires_in":43169,"scope":"select"}


2.获得用户信息
http://localhost:9030/resource/getUser?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34
注意：授权权限认证来自Micro-Service-Skeleton-Auth的UserController


3.注销
自定义：
http://localhost:9030/authCenter/cancel?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34
控制台输入 UserDetailsService 里边的账号密码   egg: admin 123456

官方：
http://localhost:9030/uaa/oauth/remove?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34


注意：这里需要添加白名单
security:
  ignored: /cancel/**,/oauth/remove


4.其它
index页面（没有过滤，所以需要权限）
            ->  http://localhost:9060/?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34
            ->  http://localhost:9030/authCenter?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34

hello页面（已经过滤，所以不需要权限）         
            ->  http://localhost:9060/hello
            ->  http://localhost:9030/authCenter/hello

login页面（没有过滤，所以需要权限）  
            ->  http://localhost:9060/login?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34
            ->  http://localhost:9030/authCenter/login?access_token=2b81db72-f5c9-4676-b97a-7aec45f02b34
            
注意：authCenter是服务名


授权{

注意：账号信息来自数据库表: oauth_client_details + rc_user, 内部使用了BCryptPasswordEncoder加密，原始密码是123456

注意：需要保证client_id 拥有 authorization_code+client_credentials，如果没有请在oauth_client_details表里加

1.
授权地址：
http://localhost:9030/uaa/oauth/authorize?client_id=client_2&response_type=code&redirect_uri=http://www.baidu.com&state=123

2.
控制台输入 UserDetailsService 里边的账号密码(rc_user,egg: test1 123456),选择Approval，点击确认

得到code
https://www.baidu.com/?code=m8mWSj&state=123


3.
curl -X POST -H "Cant-Type: application/x-www-form-urlencoded" -d "grant_type=authorization_code&code=m8mWSj&redirect_uri=http://www.baidu.com" "http://client_2:123456@localhost:9030/uaa/oauth/token"
返回如下：
{
		"access_token": "857598ee-f82a-498c-959e-6315bbf27cd9",
		"token_type": "bearer",
		"re
		fresh_token": "09a2d921-cdf0-4ef7-a719-495184c9a221",
		"expires_in": 39387,
		"scope": "
		select"
}


注意：http://账号:密码@ip:端口/oauth/token  client_id + client_secret
对应的是数据库表： oauth_client_details + rc_user	
	
}



不同角色权限控制{

数据库中的字段是authorities,表是oauth_client_details

clients.inMemory()
                .withClient("default")
                .secret("kx")
                .scopes("AUTH", "TRUST")
                .autoApprove(true)
                .authorities("ROLE_GUEST", "ROLE_USER", "ROLE_ADMIN")
                .authorizedGrantTypes("authorization_code", "implicit", "refresh_token");
				
				
    /**
	 * 不需要权限，有token即可
	 * @param user 正常登陆获得token即可
	 * @return 权限
	 */
	@RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
	
	/**
	 * 需要用户权限
	 * @param principal 普通用户
	 * @return 权限
	 */
	@PreAuthorize("hasRole('admin') or hasRole('user')")
    @RequestMapping("/user2")
    public Map<String,String> user2(Principal principal) {
       Map<String,String> map = new LinkedHashMap<>();
       map.put("name", principal.getName());
       return map;
    }
	
	/**
	 * 需要管理员权限
	 * @param principal 管理员用户
	 * @return 权限
	 */
    @PreAuthorize("hasRole('ROLE_admin')")  
    //@PreAuthorize("#oauth2.hasScope('openid') and hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "admin", method = RequestMethod.GET)
    public Principal helloSecret(Principal principal) {
    	return principal;
    }

测试：


有正确权限的
http://localhost:9030/uaa/oauth/token?username=admin&password=123456&grant_type=password&scope=select&client_id=client_3&client_secret=123456	
数据库authorized_grant_types 是           ->   password,refresh_token,client_credentials
数据库 表: rc_role value 是               ->   ROLE_admin （代码中加了前缀 ROLE_ ，后缀可以在数据库表 rc_role 中设置）
返回：
{"access_token":"b8e8902e-6205-409d-9cc5-bca28e7e34ea","token_type":"bearer","refresh_token":"1fc2c78b-0600-4918-914e-49643cddf59e","expires_in":43173,"scope":"select"}


具体测试：
http://localhost:9060/admin?access_token=b8e8902e-6205-409d-9cc5-bca28e7e34ea

得到正确返回：
{"authorities":[{"authority":"ROLE_admin"},{"authority":"apidoc"},{"authority":"database/log"},{"authority":"system"},{"authority":"user/add"},
{"authority":"user/delete"},{"authority":"user/edit"},{"authority":"user/view"},{"authority":"userList"}],"details":{"remoteAddress":"0:0:0:0:0:0:0:1","sessionId":null,
"tokenValue":"4dc653dc-747f-490a-a84f-11cfd77ae165","tokenType":"Bearer","decodedDetails":null},"authenticated":true,"userAuthentication":{"authorities":[{"authority":"ROLE_admin"},
{"authority":"apidoc"},{"authority":"database/log"},{"authority":"system"},{"authority":"user/add"},{"authority":"user/delete"},{"authority":"user/edit"},{"authority":"user/view"},
{"authority":"userList"}],"details":{"grant_type":"password","scope":"select","client_secret":"123456","client_id":"client_3","username":"admin"},"authenticated":true,
"principal":{"password":null,"username":"admin","authorities":[{"authority":"ROLE_admin"},{"authority":"apidoc"},{"authority":"database/log"},{"authority":"system"},
{"authority":"user/add"},{"authority":"user/delete"},{"authority":"user/edit"},{"authority":"user/view"},{"authority":"userList"}],"accountNonExpired":true,
"accountNonLocked":true,"credentialsNonExpired":true,"enabled":true},"credentials":null,"name":"admin"},"credentials":"","oauth2Request":{"clientId":"client_3",
"scope":["select"],"requestParameters":{"grant_type":"password","scope":"select","client_id":"client_3","username":"admin"},"resourceIds":[],"authorities":[],"approved":true,
"refresh":false,"redirectUri":null,"responseTypes":[],"extensions":{},"grantType":"password","refreshTokenRequest":null},"clientOnly":false,"principal":{"password":null,
"username":"admin","authorities":[{"authority":"ROLE_admin"},{"authority":"apidoc"},{"authority":"database/log"},{"authority":"system"},{"authority":"user/add"},
{"authority":"user/delete"},{"authority":"user/edit"},{"authority":"user/view"},{"authority":"userList"}],"accountNonExpired":true,"accountNonLocked":true,
"credentialsNonExpired":true,"enabled":true},"name":"admin"}




没有有正确权限：
http://localhost:9030/uaa/oauth/token?username=test2&password=123456&grant_type=password&scope=select&client_id=client_2&client_secret=123456
数据库authorized_grant_types 是           ->   password,refresh_token,authorization_code,client_credentials
数据库 表: rc_role value 是               ->   ROLE_user （代码中加了前缀 ROLE_ ，后缀可以在数据库表 rc_role 中设置）
返回
{"access_token":"ddc25040-33f1-4bbc-8a69-a37609c21433","token_type":"bearer","refresh_token":"b024254a-63b1-49c6-9cab-68d2e3b7869b","expires_in":43199,"scope":"select"}
具体测试：
http://localhost:9060/admin?access_token=ddc25040-33f1-4bbc-8a69-a37609c21433
得到错误提示：
<oauth>
<error_description>不允许访问</error_description>
<error>access_denied</error>
</oauth>


注意：
需要有登陆用户即可           http://localhost:9060/user?access_token=4dc653dc-747f-490a-a84f-11cfd77ae165
                             http://localhost:9030/uaa/user?access_token=4dc653dc-747f-490a-a84f-11cfd77ae165
需要有user用户权限以上       http://localhost:9060/user2?access_token=ddc25040-33f1-4bbc-8a69-a37609c21433
                             http://localhost:9030/uaa/user2?access_token=ddc25040-33f1-4bbc-8a69-a37609c21433
需要有admin用户权限          http://localhost:9060/admin?access_token=4dc653dc-747f-490a-a84f-11cfd77ae165
                             http://localhost:9030/uaa/admin?access_token=4dc653dc-747f-490a-a84f-11cfd77ae165


创建用户角色流程：  表: rc_user(基本用户)  ->  表: rc_user_role(跟rc_role表用户权限绑定)  ->  表: rc_role(具体的角色，即authorities)   ->   表: rc_privilege（菜单权限）   ->   表: rc_menu（页面权限）
	

	
其它项目使用权限认证

resource子服务  ->  application.yml

###actuator监控点 start####
endpoints:
  health:
    sensitive: false
    enabled: true
##默认情况下很多端点是不允许访问的，会返回401:Unauthorized
management:
  security:
    enabled: false
###actuator监控点 end####
security:
  oauth2:
    resource:
      id: resource
      #默认配置，有token就可以的
#       user-info-uri: http://localhost:9030/uaa/user
      #额外配置，需要管理员角色权限
      user-info-uri: http://localhost:9030/uaa/admin
      prefer-token-info: false

	  
@RestController
public class UserController {

    @GetMapping(value = "getUser")
    public String getUser(){
        return "order";
    }

}	  

使用admin角色access_token
http://localhost:9030/resource/getUser?access_token=b8e8902e-6205-409d-9cc5-bca28e7e34ea	
返回：order

使用user角色access_token
http://localhost:9030/resource/getUser?access_token=ddc25040-33f1-4bbc-8a69-a37609c21433	
返回:
<oauth>
<error_description>420a5132-98ba-4bd8-8251-03a18be3af59</error_description>
<error>invalid_token</error>
</oauth>
	
	
拦截无效请参考：
https://blog.csdn.net/sinat_28454173/article/details/52312828

总参考：
https://stackoverflow.com/questions/35088918/spring-oauth2-hasrole-access-denied
}


其它异常{

Possible CSRF detected - state parameter was required but no state could be found

这个错误只在使用AuthorizationCode方式时出现， 就是客户端反复刷新带有code和state的参数的url导致的,因为code已经被使用过一次了,
相当于过期了,你反复使用,自然会被认为是非法操作,所以"可能是CSRF攻击",没有说的那个是可能是你懵逼了

}



win7 配置curl
下载地址:https://winampplugins.co.uk/curl/
配置环境变量
当然，可以给Windows增加curl命令的环境变量，增加CURL_HOME环境变量，给PATH环境变量加上%CURL_HOME%; 


参考：
https://blog.csdn.net/w1054993544/article/details/78932614
https://www.jianshu.com/p/c6ce913a3d52
https://www.cnblogs.com/dream-to-pku/p/7452059.html
