package tw.com.softleader.data.jakarta.security.guardium;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.persistence.EntityManagerFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;

/**
 * 特徵化測試共用的假物件工廠。
 *
 * <p>類名刻意不符合 surefire 預設 includes 樣式（Test* / *Test / *Tests / *TestCase），本身也不含任何 {@code @Test}，
 * 因此不會被當成測試類別執行。
 */
final class CharacterizationFixtures {

  private CharacterizationFixtures() {}

  /** 回傳一個 {@link DataSource}，其 metadata 回報指定的 databaseProductName。 */
  static DataSource dataSourceReporting(String databaseProductName) throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    Connection connection = mock(Connection.class);
    DatabaseMetaData metaData = mock(DatabaseMetaData.class);
    given(dataSource.getConnection()).willReturn(connection);
    given(connection.getMetaData()).willReturn(metaData);
    given(metaData.getDatabaseProductName()).willReturn(databaseProductName);
    return dataSource;
  }

  /** 回傳一個 {@link DataSource}，呼叫 getConnection() 一律丟 {@link SQLException}。 */
  static DataSource dataSourceThrowingOnGetConnection() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    given(dataSource.getConnection())
        .willThrow(new SQLException("characterization: connection refused"));
    return dataSource;
  }

  /**
   * 回傳一個 {@link EntityManagerFactoryInfo}。
   *
   * <p>本 repo 今天沒有任何測試會提供這種 bean，這正是 autoconfigure 的 JPA 分支零覆蓋的原因。
   */
  static EntityManagerFactoryInfo entityManagerFactoryInfo(String persistenceUnitName) {
    EntityManagerFactoryInfo info = mock(EntityManagerFactoryInfo.class);
    EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
    given(info.getPersistenceUnitName()).willReturn(persistenceUnitName);
    given(info.getNativeEntityManagerFactory()).willReturn(entityManagerFactory);
    return info;
  }
}
