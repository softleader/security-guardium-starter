package tw.com.softleader.data.jakarta.security.guardium.annotation;

import java.lang.annotation.*;

/**
 * Safeguard 表示此 method 必須被安全控管, 整合資訊到 IBM Security Guardium 中
 *
 * <p>如果掛在 class 上, 即該 class 下的所有 public method 都會被安全控管!
 *
 * @see <a href=
 *     "https://www.ibm.com/support/knowledgecenter/en/SSMPHH_10.6.0/com.ibm.guardium.doc/overview/product_overview.html">https://www.ibm.com/support/knowledgecenter/en/SSMPHH_10.6.0/com.ibm.guardium.doc/overview/product_overview.html</a>
 * @author matt
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Safeguard {}
