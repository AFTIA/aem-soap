package me.guillaumecle.aem.soap.core.endpoints;

import javax.jws.WebService;

@WebService
public interface GreeterService {
    public String sayHi(String message);
}
