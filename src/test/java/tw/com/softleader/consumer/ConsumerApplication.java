package tw.com.softleader.consumer;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * 模擬「使用端」的 Spring Boot 應用程式。
 *
 * <p>刻意放在 starter 自己的套件（{@code tw.com.softleader.data.jakarta.security.guardium}）之外。
 * 若放在裡面，component scan 會涵蓋 {@code ...guardium.autoconfigure}，而負責把 {@code
 * SecurityGuardiumAutoConfiguration} 排除在掃描之外的 {@code AutoConfigurationExcludeFilter} 讀的正是同一份
 * imports 檔——imports 一旦失效，該類別反而會被 component scan 撿進來，bean 照樣存在、 斷言照樣綠，這條測試就等於零鑑別力。
 */
@SpringBootApplication
class ConsumerApplication {

  /**
   * 使用端自備的 {@code DataSource}。
   *
   * <p>自己宣告而不靠 {@code DataSourceAutoConfiguration}，是為了讓這條測試只在「starter 有沒有被自動 裝配」這件事上翻紅，不受 Boot
   * 版本間內嵌 DataSource 偵測行為變動的影響。AUTO 方言偵測會對它呼叫 {@code getConnection()}，所以必須是真的連得上的資料庫，不能用 mock。
   */
  @Bean
  DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .generateUniqueName(true)
        .build();
  }
}
