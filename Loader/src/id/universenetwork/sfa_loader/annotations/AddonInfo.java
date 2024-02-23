package id.universenetwork.sfa_loader.annotations;

import id.universenetwork.sfa_loader.enums.PaperRequirementLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddonInfo {
    String name() default "";

    PaperRequirementLevel requirePaper() default PaperRequirementLevel.ESSENTIAL;
}