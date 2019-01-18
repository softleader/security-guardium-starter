package tw.com.softleader.boot.security.guardium.annotation;

import java.lang.annotation.*;

/**
 * Safeguard 表示此 method 必須被控管, 整合資訊到 IBM Security Guardium 中
 *
 * @see <a
 *     href="https://www.ibm.com/support/knowledgecenter/en/SSMPHH_10.6.0/com.ibm.guardium.doc/overview/product_overview.html">https://www.ibm.com/support/knowledgecenter/en/SSMPHH_10.6.0/com.ibm.guardium.doc/overview/product_overview.html</a>
 * @author matt
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Safeguard {}
