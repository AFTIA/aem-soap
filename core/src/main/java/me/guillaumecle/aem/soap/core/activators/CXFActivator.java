package me.guillaumecle.aem.soap.core.activators;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.guillaumecle.aem.soap.core.endpoints.GreeterService;
import me.guillaumecle.aem.soap.core.endpoints.GreeterServiceImpl;
import me.guillaumecle.aem.soap.core.services.CXFService;
import me.guillaumecle.aem.soap.core.services.CXFServiceInterface;

/**
 * @see https://cxf.apache.org/distributed-osgi-greeter-demo-walkthrough.html
 */
@Component(service = BundleActivator.class,
           immediate = true,
           name = "Apache CXF Activator"
)
@Designate(ocd = CXFActivator.ActivatorConfig.class)
public class CXFActivator implements BundleActivator {
    
    @SuppressWarnings("rawtypes")
    private static ServiceRegistration registration;
    private static BundleContext bundleContext;
    private static Dictionary<String, String> props;

    @Reference(	policy = ReferencePolicy.DYNAMIC, 
				cardinality = ReferenceCardinality.MULTIPLE,
				bind="bindModelList",
				unbind="unbindModelList")
	private volatile List<CXFService> cxfServices;

    private Logger log = LoggerFactory.getLogger(getClass());

    @ObjectClassDefinition(name = "Apache CXF Activator Configuration", description = "Configures the Apache CXF SOAP Endpoints and Connector details. See https://cxf.apache.org/distributed-osgi-reference.html")
    public @interface ActivatorConfig {

        @AttributeDefinition(name = "Denotes the interfaces to be exposed remotely. This is a comma-separated list of fully qualified Java interfaces that should be made available remotely. ")
        String exportedInterfaces() default "*";

        @AttributeDefinition(name = "Specifies the mechanism for configuring the service exposure.")
        String exportedConfigs() default "org.apache.cxf.ws";

        @AttributeDefinition(name = "The address at which the service will be made available remotely.")
        String address() default "http://0.0.0.0:4504/soap/endpoint";

        @AttributeDefinition(name = "The CXF frontend which will be used to create endpoints.")
        String frontend() default "simple";

        @AttributeDefinition(name = "The CXF databindings which will be used to marshall objects")
        String databinding() default "aegis";
    }

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting CXF Bundle");
        bundleContext = context;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        log.info("Stopping CXF Bundle");

        if(registration != null){
            registration.unregister();
        }
    }

    @Activate
    @Modified
    protected void activate(final ActivatorConfig config) throws Exception {
        log.debug("Activating CXF Bundle with address [{}]", config.address());
        props = new Hashtable<String, String>();

        props.put("service.exported.interfaces", config.exportedInterfaces());
        props.put("service.exported.configs", config.exportedConfigs());
        props.put("org.apache.cxf.ws.address", config.address());
        props.put("org.apache.cxf.ws.frontend", config.frontend()); 
        props.put("org.apache.cxf.ws.databinding", config.databinding());  

        // for (ServiceReference<?> reference : references) {
        //     log.debug("Registering service [{}]", reference.getClass().getName());
        //     registration = context.registerService(CXFService.class.getName(), reference, props);
        // }
        //Potential to register interceptors via interface registration
        // https://bitbucket.org/aftia/aftia-fbp/src/master/fbp/core/src/main/java/com/aftia/fbp/core/PojoWorkflowImpl.java

        // ServiceReference<?>[] references = bundleContext.getAllServiceReferences(CXFService.class.getName(), null);

        // log.info("Svcs:" + references.length);
    }

    protected synchronized void bindModelList(CXFService cxfService) {
		if (null == cxfServices) {
			cxfServices = new ArrayList<CXFService>();
		}
		
		cxfServices.add(cxfService);

        registration = bundleContext.registerService(cxfService.getClass().getAnnotation(CXFServiceInterface.class).wsdlInterface().getName(), cxfService, props);
		log.info("Bound service implementation: [" + cxfService.getClass().getName() + "].");
	}
	
	protected synchronized void unbindModelList(CXFService cxfService) {
		cxfServices.remove(cxfService);
		log.info("Unbound service implementation: [" + cxfService.getClass().getName() + "].");
	}
}
