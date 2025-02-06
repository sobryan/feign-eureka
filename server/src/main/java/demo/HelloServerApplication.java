package demo;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * @author Spencer Gibb
 */
@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableWs
public class HelloServerApplication {

	@Autowired
	DiscoveryClient client;

	@RequestMapping("/")
	public String hello() {
		List<ServiceInstance> instances = client.getInstances("HelloServer");
		ServiceInstance selectedInstance = instances
				.get(new Random().nextInt(instances.size()));
		return "Hello World: " + selectedInstance.getServiceId() + ":" + selectedInstance
				.getHost() + ":" + selectedInstance.getPort();
	}

	public static void main(String[] args) {
		SpringApplication.run(HelloServerApplication.class, args);
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan("demo.wsdl");
		return marshaller;
	}

	@Bean(name = "hello")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema helloSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("HelloPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://demo/hello");
		wsdl11Definition.setSchema(helloSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema helloSchema() {
		return new SimpleXsdSchema(new ClassPathResource("hello.xsd"));
	}
}
