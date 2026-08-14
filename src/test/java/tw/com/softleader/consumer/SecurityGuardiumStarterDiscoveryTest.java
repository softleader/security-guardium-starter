package tw.com.softleader.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import tw.com.softleader.data.jakarta.security.guardium.GuardiumApi;
import tw.com.softleader.data.jakarta.security.guardium.NativeQueryGuardiumApi;
import tw.com.softleader.data.jakarta.security.guardium.SafeguardAspect;

/** 特徵化 #17：走真實的 AutoConfiguration.imports 發現機制，確認使用端真的裝得到這支 starter。 */
class SecurityGuardiumStarterDiscoveryTest {

  @DisplayName("#17 使用端啟動 @SpringBootApplication → GuardiumApi 與 SafeguardAspect 真的被自動裝配")
  @Test
  void starterIsDiscoveredThroughAutoConfigurationImports() {
    try (ConfigurableApplicationContext context =
        new SpringApplicationBuilder(ConsumerApplication.class)
            .web(WebApplicationType.NONE)
            .properties("spring.sql.init.mode=never")
            .run()) {
      // 這條的鑑別力來自 getBean 本身：imports 檔一旦失效，兩次 getBean 都會丟
      // NoSuchBeanDefinitionException。型別斷言只是順帶確認拿到的是本 starter 的實作，
      // 因此用父型別即可——精確型別由 #9 釘住。
      assertThat(context.getBean(GuardiumApi.class)).isInstanceOf(NativeQueryGuardiumApi.class);
      assertThat(context.getBean(SafeguardAspect.class)).isNotNull();
    }
  }
}
