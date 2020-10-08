
package com.my.mq.common.spi;

import java.lang.annotation.*;

/**
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpiMeta {
    /**
     *
     * @return spi名称
     */
    String name() default "";
}
