package tw.com.softleader.data.security.guardium;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcOperations;

@RequiredArgsConstructor
public class JdbcNativeQuery implements NativeQuery {

  final JdbcOperations operations;

  @Override
  public void execute(String sql) {
    operations.execute(sql);
  }
}
