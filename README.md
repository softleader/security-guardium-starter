# security-guardium-starter

Secure service w/ IBM Security Guardium

## Properteis

- `softleader.security.guardium.enabled` - 是否啟用 (default: `true`)

以下設定適用於純 JDBC (ex: [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)), 若使用 JPA (ex: [Spring Data JPA](https://spring.io/projects/spring-data-jpa)) 則以下參數將忽略

- `softleader.security.guardium.query-timeout-second` - 設定 sql statement timeout 秒數, -1 代表使用 driver 預設值, 但 driver default 有可能會 block main thread 太久, 因此提供參數可以控制, 建議要設定 (default: `10`)
- `softleader.security.guardium.data-source-ref` - 設定 dataSource bean 名稱 (default `dataSource`)

## How to setup

首先我們須在要 `pom.xml` 加入 starter:

```xml
<dependency>
    <groupId>tw.com.softleader.data</groupId>
    <artifactId>security-guardium-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

> 如果你的專案使用 [softleader/softleader-boot-starter-platform](https://github.com/softleader/softleader-boot-starter-platform), 請使用 [legacy 版本](https://github.com/softleader/security-guardium-starter/tree/legacy)

此 Starter 預設啟動的 IBM Security Guardium 實作為版本 10, 實作為 [`IBMSecurityGuardium10Api`](./src/main/java/tw/com/softleader/data/security/guardium/IBMSecurityGuardium10Api.java), 請注意你的環境是否符合[規格](./docs)

在使用上專案必須提供 [`GuardAppEventSupplier`](./src/main/java/tw/com/softleader/data/security/guardium/GuardAppEventSupplier.java) 實作, 並將之註冊成一個 Spring Bean, `IBMSecurityGuardium10Api` 會在每一次要寫入 IBM Security Guardium 時所要取得的 runtime 資料, 範例如下:

```java
@Configuration
class MyConfig {

  @Bean
  GuardAppEventSupplier guardAppEventSupplier() {
    return (method, args) -> GuardAppEvent.builder()
      .userName("...") // 對應 Guardium API 的 GuardAppEventUserName, ex: 登入系統之使用者 帳號(ID)
      .type("...") // 對應 Guardium API 的 GuardAppEventType, ex: 使用者所使用的 應用系統名稱_模組功能名稱
      .strValue("...") // 對應 Guardium API 的 GuardAppEventStrValue, ex: 使用者 IP 位址
      .build();
  }
}
```

- `method` - 被 AOP 攔截的 method
- `args` - 被 AOP 攔截 method 的 input 參數

## How to use

在你希望被加入安全監控的 Spring service class 或 method 上加上 `@Safeguard` 即可:

```java
@Safeguard
public class MyService {
    
    // will be safeguard
    public void hello() {
      // ...
    }
}
``` 

## Logging

set `logging.level.tw.com.softleader.data.security.guardium=trace` to show every sql that `GuardAppEventApi` executed
