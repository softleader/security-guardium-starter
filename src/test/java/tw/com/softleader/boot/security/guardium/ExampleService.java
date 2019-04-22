package tw.com.softleader.boot.security.guardium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.com.softleader.boot.security.guardium.annotation.Safeguard;

@Safeguard
@Service
public class ExampleService {

  @Autowired private JdbcTemplate template;
  @Autowired private Example example;

  @Transactional
  public void hello() {
    template.update("INSERT INTO EXAMPLE (CALLS) VALUES (?)", example.getCalls());
  }
}
