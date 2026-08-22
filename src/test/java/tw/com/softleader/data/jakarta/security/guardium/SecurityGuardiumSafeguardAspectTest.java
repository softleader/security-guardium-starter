package tw.com.softleader.data.jakarta.security.guardium;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tw.com.softleader.data.jakarta.security.guardium.annotation.Safeguard;
import tw.com.softleader.data.jakarta.security.guardium.autoconfigure.SecurityGuardiumAutoConfiguration;

/** 特徵化 #16：SafeguardAspect 已註冊，且真的攔截 @Safeguard 方法。 */
class SecurityGuardiumSafeguardAspectTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SecurityGuardiumAutoConfiguration.class));

  @DisplayName("#16 SafeguardAspect 已註冊，且真的攔截 @Safeguard 方法")
  @Test
  void aspectInterceptsSafeguardedMethod() throws SQLException {
    DataSource dataSource = CharacterizationFixtures.dataSourceReporting("H2");
    runner
        .withBean("dataSource", DataSource.class, () -> dataSource)
        .withBean(RecordingGuardiumApi.class, RecordingGuardiumApi::new)
        .withBean(SafeguardedBean.class, SafeguardedBean::new)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              assertThat(context).hasSingleBean(SafeguardAspect.class);
              RecordingGuardiumApi recorder = context.getBean(RecordingGuardiumApi.class);
              assertThat(recorder.events).isEmpty();

              context.getBean(SafeguardedBean.class).guarded();

              assertThat(recorder.events).containsExactly("start:guarded", "released");
            });
  }

  /** 自備 GuardiumApi 會讓 autoconfigure 的 @ConditionalOnMissingBean(GuardiumApi.class) 退讓。 */
  static class RecordingGuardiumApi implements GuardiumApi {

    final List<String> events = new ArrayList<>();

    @Override
    public void start(Method method, Object[] args) {
      events.add("start:" + method.getName());
    }

    @Override
    public void released() {
      events.add("released");
    }
  }

  /**
   * 掛 {@link Safeguard} 的受測 bean。
   *
   * <p>類別與方法都必須是 public、非 final：它沒有實作任何介面，Spring AOP 會用 CGLIB 做子類別 proxy， 只有可覆寫的方法攔得到。
   */
  @Safeguard
  public static class SafeguardedBean {

    public void guarded() {}
  }
}
