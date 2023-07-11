package id.universenetwork.sfa_loader.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(AddonLibraries.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddonLibrary {
    String groupId();

    String artifactId();

    String version();

    String packageRelocation() default "";

    String packageRelocationName() default "";
}