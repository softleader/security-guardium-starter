# security-guardium-starter

Secure service w/ IBM Security Guardium

## Properteis

- `softleader.security.guardium.version` - 設定客戶使用的 IBM Security Guardium 版本, IBM Security Guardium 版本目前有 8, 9 跟 10 這幾個大版本, 不確定差異在哪, 但目前第一家客戶 (CKI) 使用的是 10, 因此我們先提供 10 的實作, 當然, 我們現在也就只有 10 的實作 (defualt: `10`)
- `softleader.security.guardium.query-timeout-second` - 設定 sql statement timeout 秒數, -1 代表使用 driver 預設值, 但 driver default 有可能會 block main thread 太久, 因此提供參數可以控制, 建議要設定 (default: `-1`)
- `softleader.security.guardium.data-source-ref` - 設定 dataSource bean 名稱 (default `dataSource`)

## How to setup

首先我們須在要 `pom.xml` 加入 starter:

```xml
<dependency>
    <groupId>tw.com.softleader</groupId>
    <artifactId>security-guardium-starter</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>
```

此 Starter 預設啟動的 IBM Security Guardium 實作為版本 10, 實作為 `tw.com.softleader.boot.security.guardium.IBMSecurityGuardium10GuardAppEvent`

在 `IBMSecurityGuardium10GuardAppEvent` 中, 會透過 `tw.com.softleader.boot.security.guardium.EventDataSupplier` 來取得每一次要寫入 IBM Security Guardium 的 runtime 資料, 因此使用的專案必須要提供 `EventDataSupplier` 的實作:

```java
@Configuration
public class SomeConfiguration {

	@Bean
	public EventDataSupplier eventDataSupplier(
		return (method, args) -> {
			Map<String, String> data = new LinkedHashMap<>(); // key=欄位名稱, value=值
    			data.put(...); // 放入要寫的 data
                   		       // data 應該是要每次 runtime 才取, 例如呼叫另一個 class 取得資料
                    		       // 而非直接 hard code 在這邊
			return data;
	    }
	}
}
```

- `method` - 被 AOP 攔截的 method
- `args` - 被 AOP 攔截 method 的 input 參數

如果你的專案有依賴 [softleader-product/softleader-jasmine-config](https://github.com/softleader-product/softleader-jasmine-config) 那就更簡單了, 直接使用 [UsernameIpEventDataSupplier](https://github.com/softleader-product/softleader-jasmine-config/blob/master/src/main/java/tw/com/softleader/jasmine/security/guardium/UsernameIpEventDataSupplier.java) 即可:

```java
@Bean
public EventDataSupplier eventDataSupplier(@Autowired JasmineUsernameSupplier supplier) {
	return EventDataSupplier.proxy(new UsernameIpEventDataSupplier(supplier));
}
```

> 但實際上, `UsernameIpEventDataSupplier ` 其實是依照兆豐的需求而定的 :/

## How to use

在你希望被加入安全監控的 Spring service method 上加上 `@Safeguard` 即可:

```java
@Safeguard
public void hello() {
  // ...
}
```

在 `1.0.1` 版本之後, `@Safeguard` 也可以掛在 class 上了! 即該 class 下所有 public method 都會被加入安全監控:

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

set `logging.level.tw.com.softleader.boot.security.guardium=trace` to show every sql that `tw.com.softleader.boot.security.guardium.GuardAppEvent` executed
