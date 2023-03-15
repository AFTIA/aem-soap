package me.guillaumecle.aem.soap.core.activators;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
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

import me.guillaumecle.aem.soap.core.annotations.CXFServiceInterface;
import me.guillaumecle.aem.soap.core.services.CXFService;

/**
 * Apache CXF Activator that allows SOAP-based services to be exposed via OSGi.
 * 
 * @author Guillaume Clement
 * @see https://cxf.apache.org/distributed-osgi-greeter-demo-walkthrough.html
 */
@Component(service = BundleActivator.class,
           immediate = true,
           name = "Apache CXF Activator"
)
@Designate(ocd = CXFActivator.ActivatorConfig.class)
public class CXFActivator implements BundleActivator {

    private static BundleContext bundleContext;
    private static Dictionary<String, String> props;

    @Reference(	policy = ReferencePolicy.DYNAMIC, 
				cardinality = ReferenceCardinality.MULTIPLE,
				bind="bindService",
				unbind="unbindService")
	private volatile List<CXFService> cxfServices = new ArrayList<CXFService>();
	private volatile List<RegistrationHolder> registrationHolders = new ArrayList<RegistrationHolder>();

    private Logger log = LoggerFactory.getLogger(getClass());

    @ObjectClassDefinition(name = "Apache CXF Activator Configuration", description = "Configures the Apache CXF SOAP Endpoints and Connector details. See https://cxf.apache.org/distributed-osgi-reference.html")
    public @interface ActivatorConfig {

        @AttributeDefinition(name = "Interfaces to be exposed remotely", description = "This is a comma-separated list of fully qualified Java interfaces that should be made available remotely. Use * to list all registered interfaces.")
        String exportedInterfaces() default "*";

        @AttributeDefinition(name = "Exported configurations", description = "Specifies the mechanism for configuring the service exposure.")
        String exportedConfigs() default "org.apache.cxf.ws";

        @AttributeDefinition(name = "CXF address", description = "The address at which the service will be made available remotely.")
        String address() default "http://0.0.0.0:4504/soap/endpoint";

        @AttributeDefinition(name = "CXF frontend", description = "The CXF frontend which will be used to create endpoints.")
        String frontend() default "simple";

        @AttributeDefinition(name = "CXF databindings", description = "The CXF databindings which will be used to marshall objects")
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
    }

    @Activate
    @Modified
    protected void activate(final ActivatorConfig config) throws Exception {
        log.info("Activating CXF Bundle with address [{}]", config.address());
        props = new Hashtable<String, String>();

        props.put("service.exported.interfaces", config.exportedInterfaces());
        props.put("service.exported.configs", config.exportedConfigs());
        props.put("org.apache.cxf.ws.address", config.address());
        props.put("org.apache.cxf.ws.frontend", config.frontend()); 
        props.put("org.apache.cxf.ws.databinding", config.databinding());
    }

    protected synchronized void bindService(CXFService cxfService) throws Exception {
        CXFServiceInterface serviceInterface;
        RegistrationHolder holder = new RegistrationHolder();
        holder.setCxfService(cxfService);

        try {
            serviceInterface = cxfService.getClass().getAnnotation(CXFServiceInterface.class);

            if(null == serviceInterface){
                throw new NullPointerException("Service annotation is null.");
            }

            ServiceRegistration<?> registration = bundleContext.registerService(serviceInterface.wsdlInterface().getName(), cxfService, props);
            holder.setRegistration(registration);

            registrationHolders.add(holder);

            log.info("Bound service implementation: [" + cxfService.getClass().getName() + "].");
        } catch (NullPointerException e) {
            throw new Exception("Service registration is missing a valid @CXFServiceInterface annotation.", e);
        }
	}
	
	protected synchronized void unbindService(CXFService cxfService) {
        registrationHolders.stream()
            .filter(r -> r.getCxfService().equals(cxfService)).findFirst()
            .ifPresent(r -> {
                r.getRegistration().unregister();
                registrationHolders.remove(r);
            });

		log.info("Unbound service implementation: [" + cxfService.getClass().getName() + "].");
	}

    class RegistrationHolder {
        private CXFService cxfService;
        private ServiceRegistration<?> registration;

        public CXFService getCxfService() {
            return cxfService;
        }
        public void setCxfService(CXFService cxfService) {
            this.cxfService = cxfService;
        }
        public ServiceRegistration<?> getRegistration() {
            return registration;
        }
        public void setRegistration(ServiceRegistration<?> registration) {
            this.registration = registration;
        }
    }
}
