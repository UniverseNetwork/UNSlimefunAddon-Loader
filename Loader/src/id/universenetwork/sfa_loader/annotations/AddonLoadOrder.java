package id.universenetwork.sfa_loader.annotations;

import id.universenetwork.sfa_loader.enums.LoadPriority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddonLoadOrder {
    LoadPriority value() default LoadPriority.NORMAL;
}
