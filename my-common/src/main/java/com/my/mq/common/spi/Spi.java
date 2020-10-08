
package com.my.mq.common.spi;

import java.lang.annotation.*;

/**
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Spi {

    /**
     * 作用范围
     * @return 作用范围
     */
    Scope scope() default Scope.PROTOTYPE;

}
