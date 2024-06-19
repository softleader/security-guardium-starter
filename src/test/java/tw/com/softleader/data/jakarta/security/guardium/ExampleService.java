package tw.com.softleader.data.jakarta.security.guardium;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import tw.com.softleader.data.jakarta.security.guardium.annotation.Safeguard;

@Safeguard
@RequiredArgsConstructor
public class ExampleService {

  @Autowired private JdbcTemplate template;

  @Transactional
  public void save(String name) {
    template.update("insert into test(name) values(?);", name);
  }
}
