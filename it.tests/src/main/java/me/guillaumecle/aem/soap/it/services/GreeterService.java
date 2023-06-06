package me.guillaumecle.aem.soap.it.services;

import javax.jws.WebService;

@WebService
public interface GreeterService {
    public String sayHi(String message);
}
