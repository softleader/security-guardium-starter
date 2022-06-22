package tw.com.softleader.data.security.guardium;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class JpaNativeQuery implements NativeQuery {

  @PersistenceContext
  EntityManager manager;

  @Override
  public void execute(String sql) {
    manager.createNativeQuery(sql).getResultList();
  }
}
