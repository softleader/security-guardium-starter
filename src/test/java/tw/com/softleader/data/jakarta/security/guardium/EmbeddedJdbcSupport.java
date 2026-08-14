package tw.com.softleader.data.jakarta.security.guardium;

import java.util.List;
import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

/**
 * 三支既有測試改寫為「版本中立」後共用的組裝零件：把 Spring Boot 的 JDBC test slice 原本代勞的事情 自己補齊。
 *
 * <p>類名刻意不符合 surefire 預設 includes 樣式（{@code Test*} / {@code *Test} / {@code *Tests} / {@code
 * *TestCase}），本身也沒有任何 {@code @Test}，因此不會被當成測試類別執行。
 *
 * <p>本檔的註解刻意不寫出那個 test slice 註解的字面名稱：Stage 0a 的驗收條件是它在整個 {@code src/} 底下 grep 零命中，寫進註解會讓驗收指令誤報。
 */
final class EmbeddedJdbcSupport {

  private EmbeddedJdbcSupport() {}

  /**
   * 建一個已備妥 H2、{@link JdbcTemplate} 與交易管理的 {@link ApplicationContextRunner}。
   *
   * <p>每呼叫一次就建一個獨立的 H2（{@code generateUniqueName(true)}），取代原本靠 test slice 自帶的
   * {@code @Transactional} 在方法結束時 rollback 得到的隔離。資料庫刻意不做 shutdown：它是 in-memory、每個測試方法一個、JVM 結束即消失，而
   * {@code withBean} 的 customizer 只拿得到 {@code BeanDefinition} 介面，要設 destroy method 得往下轉型成 {@code
   * AbstractBeanDefinition}， 不值得為三個 in-memory DB 增加這個耦合。
   *
   * <p><strong>維護規則（給未來改動這裡的人）：</strong>隔離完全仰賴「每個測試方法各自呼叫本方法一次」 這件事本身，本方法內部沒有任何鎖或連線池能補救。呼叫端必須在每個
   * {@code @Test} 方法裡親自呼叫 {@code runner()}，不可以把回傳值提升成欄位跨方法共用，也不可以在同一個測試類別裡新增第二個 {@code @Test}
   * 卻沿用同一次呼叫結果。一旦違反，多支測試會共用同一個 H2 資料庫，隔離會不動聲色地 消失，測試之間開始看到彼此寫入的資料列，而且不會有任何明顯的錯誤訊息提示原因。
   */
  static ApplicationContextRunner runner() {
    EmbeddedDatabase database =
        new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .addScript("schema.sql")
            .build();
    return new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SecurityGuardiumAutoConfiguration.class))
        .withUserConfiguration(TransactionManagementConfiguration.class)
        .withBean("dataSource", DataSource.class, () -> database)
        .withBean(JdbcTemplate.class, () -> new JdbcTemplate(database))
        .withBean(JdbcTransactionManager.class, () -> new JdbcTransactionManager(database));
  }

  /** 只為了帶入 {@link EnableTransactionManagement}——註解沒辦法用 {@code withBean} 掛上去。 */
  @Configuration(proxyBeanMethods = false)
  @EnableTransactionManagement
  static class TransactionManagementConfiguration {}

  /**
   * 把指定型別的 bean 在初始化完成後換成 {@link Mockito#spy(Object)}。
   *
   * <p>必須是 {@link BeanPostProcessor}，不能是事後的 {@code spy(context.getBean(...))}：aspect 與
   * autoconfigure 持有的是容器裡那一顆，事後包出來的 spy 沒有任何人會呼叫到，驗證永遠會是 0 次。 Spring 自己的 spy bean override
   * 機制內部也是走同一條路。
   */
  static class SpyBeanPostProcessor implements BeanPostProcessor {

    private final List<Class<?>> types;

    SpyBeanPostProcessor(Class<?>... types) {
      this.types = List.of(types);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
        throws BeansException {
      return this.types.stream().anyMatch(type -> type.isInstance(bean)) ? Mockito.spy(bean) : bean;
    }
  }
}
