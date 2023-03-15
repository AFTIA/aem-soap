package me.guillaumecle.aem.soap.core.endpoints;

import javax.jws.WebService;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.guillaumecle.aem.soap.core.annotations.CXFServiceInterface;
import me.guillaumecle.aem.soap.core.services.CXFService;

@WebService
@Component(service = CXFService.class)
@CXFServiceInterface(wsdlInterface = GreeterService.class)
public class GreeterServiceImpl implements CXFService, GreeterService {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    static final String NAME = GreeterService.class.getName();

    @Override
    public String sayHi(String message) {
        log.info(message);
        return "Hello";
    }
}
