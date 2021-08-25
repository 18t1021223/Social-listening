# Spring Boot with Security, OAuth2
**An tutorial application using Spring Boot as OAuth2 Authorization back-end.**

More details about the codes, please read the online **[Spring Boot](https://projects.spring.io/spring-boot).**

### Relevant information:

1. `module oauth` - the Spring based Authorization Server
2. user registered in the Authorization Server:
    1. 0ZS4RIXTWcf9a0S9a059Ux42VppRpmLG / rk81o1jobMBiATh153CUY92st6PmB0AW
3. `module api` - the Resource Server 
4. `module entity` - contains entity
Requirements
------
Running in
+ [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.8 or newer
+ [Spring Boot](https://github.com/spring-projects/spring-boot) 2.x.x RELEASE
+ Maven

Optional
------
+ YAML

Dependencies
------
+ [org.springframework.boot:spring-boot-starter-data-jpa](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-jpa)
+ [org.springframework.security.oauth:spring-security-oauth2:2.3.5.RELEASE](https://mvnrepository.com/artifact/org.springframework.security.oauth/spring-security-oauth2/2.3.5.RELEASE)
+ [org.springframework.security:spring-security-jwt:1.0.10.RELEASE](https://mvnrepository.com/artifact/org.springframework.security/spring-security-jwt)
+ [org.mariadb.jdbc:mariadb-java-client:2.7.2](https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client/2.7.2)
+ [org.springframework.boot:spring-boot-starter-data-mongodb:2.4.5](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-data-mongodb/2.4.5)
+ ...
Latest Update

Test accounts
------
+ Reference file [SpringBootOAuth2AuthorizationServerTutorialApplication.java](https://github.com/warumono-for-develop/spring-boot-oauth2-authorization-server-tutorial/blob/master/src/main/java/com/warumono/SpringBootOAuth2AuthorizationServerTutorialApplication.java)

#### User
```sql
username: admin
password: 1234567890
-- Has authorities: USER
```

#### Client
```sql
client id    : 0ZS4RIXTWcf9a0S9a059Ux42VppRpmLG
client secret: rk81o1jobMBiATh153CUY92st6PmB0AW
-- Has scopes: read, write
-- Has grant types: authorization_code, refresh_token, implicit, password, client_credentials
```

------
#### Request command

##### Template command

- clientid		: id in AppClient entity.
- clientsecret	: secret in AppClient entity.
- username		: username in AppUser entity.
- password		: password in AppUser entity.

```cli
$ curl -XPOST "@localhost:8001/guest/login" -d "username=<username>&password=<password>"
```

##### via

#### Response

##### Response template

- access_token	: token in order to access resource in ResourceServer
- refresh_token	: token in order to get a new access_token in AuthorizationServer

```json
{"access_token":"<access_token>","token_type":"bearer","refresh_token":"<refresh_token>","expires_in":43199,"scope":"read write","jti":"ed68363e-2ced-4466-8c07-894a04cd3250"}
```

#### via
