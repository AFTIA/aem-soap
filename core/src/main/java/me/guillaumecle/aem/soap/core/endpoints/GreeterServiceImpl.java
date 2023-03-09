package me.guillaumecle.aem.soap.core.endpoints;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.guillaumecle.aem.soap.core.services.CXFService;

@WebService
public class GreeterServiceImpl implements CXFService, GreeterService {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    static final String NAME = "GreeterService";

    @Override
    public String sayHi(String message) {
        log.info(message);
        return "Hello";
    }
}
