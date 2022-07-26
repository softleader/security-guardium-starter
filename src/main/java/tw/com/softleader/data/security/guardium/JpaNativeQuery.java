package tw.com.softleader.data.security.guardium;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

@RequiredArgsConstructor
public class JpaNativeQuery implements NativeQuery {

  final EntityManagerFactory factory;

  @Override
  public void execute(String sql) {
    var em = createEntityManager(factory);
    try {
      em.createNativeQuery(sql).getResultList();
    } finally {
      closeEntityManager(em);
    }
  }

  protected EntityManager createEntityManager(EntityManagerFactory factory) {
    return factory.createEntityManager();
  }

  protected void closeEntityManager(EntityManager em) {
    EntityManagerFactoryUtils.closeEntityManager(em);
  }
}
