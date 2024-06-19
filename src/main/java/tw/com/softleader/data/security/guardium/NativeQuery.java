package tw.com.softleader.data.security.guardium;

/**
 * 封裝 JPA or JDBC 的實作
 *
 * @author Matt Ho
 */
public interface NativeQuery {

  /** Executing SQL statement */
  void execute(String sql);
}
