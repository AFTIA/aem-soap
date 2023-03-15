package me.guillaumecle.aem.soap.core.services;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * Annotation used to define the interface describing a class 
 * to be exposed as a SOAP endpoint via Apache CXF.
 * <p>
 * <b>Example:</b> {@code @CXFServiceInterface(wsdlInterface = GreeterService.class)}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CXFServiceInterface {
    public Class<?> wsdlInterface();
}
