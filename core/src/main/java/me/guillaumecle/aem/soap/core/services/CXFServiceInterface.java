package me.guillaumecle.aem.soap.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CXFServiceInterface {
    public Class<?> wsdlInterface();
}
