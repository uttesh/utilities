package utility.annotation_dynamic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Greeter {

    public String greet() default "";
}
