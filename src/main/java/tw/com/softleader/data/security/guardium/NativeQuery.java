package tw.com.softleader.data.security.guardium;

/**
 * 封裝 JPA or JDBC 的實作
 *
 * @author Matt Ho
 */
public interface NativeQuery {

  void execute(String sql);
}
