package tw.com.softleader.boot.security.guardium;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * {@link tw.com.softleader.boot.security.guardium.annotation.Safeguard} 的 aop Aspect
 *
 * @author matt
 */
@Aspect
@RequiredArgsConstructor
public class SafeguardAspect {

  @NonNull private GuardAppEvent event;

  @Around("@annotation(tw.com.softleader.boot.security.guardium.annotation.Safeguard)")
  public Object around(final ProceedingJoinPoint pjp) throws Throwable {
    MethodSignature signature = (MethodSignature) pjp.getSignature();
    event.start(signature.getMethod(), pjp.getArgs());
    try {
      return pjp.proceed();
    } finally {
      event.released();
    }
  }
}
