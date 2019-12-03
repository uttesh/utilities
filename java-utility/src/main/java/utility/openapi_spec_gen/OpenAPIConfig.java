package utility.openapi_spec_gen;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@Service
public class OpenAPIConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(OpenAPIConfig.class);

	private Map<HandlerMethod, String> springdocTags = new HashMap<>();

	
	@Autowired
	private ApplicationContext context;

	@PostConstruct
	public void config() {
		logger.info("\n\n\n\n open api config {} ",context);
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(OpenAPIDefinition.class));
		if (AutoConfigurationPackages.has(context)) {
			List<String> packagesToScan = AutoConfigurationPackages.get(context);
			packagesToScan.forEach(item ->{
				logger.info(":::package items:: {} ",item);
			});
			OpenAPIDefinition apiDef = getApiDefClass(scanner, packagesToScan);
			logger.info("\n\n\n apiDef {} ",apiDef+"\n\n ");
			if(apiDef!=null) {
				logger.info("\n\n\n apiDef {} ",apiDef.info());
			}
			logger.info("\n\n restcontroler map {} ",getRestControllersMap()+"\n\n");
			logger.info("\n\n getRequestMapping map {} ",getRequestMappingMap()+"\n\n");
			
		}
	}
	
	private OpenAPIDefinition getApiDefClass(ClassPathScanningCandidateComponentProvider scanner,
			List<String> packagesToScan) {
		for (String pack : packagesToScan) {
			for (BeanDefinition bd : scanner.findCandidateComponents(pack)) {
				// first one found is ok
				try {
					return AnnotationUtils.findAnnotation(Class.forName(bd.getBeanClassName()),
							OpenAPIDefinition.class);
				} catch (ClassNotFoundException e) {
					logger.error("Class Not Found in classpath : {}", e.getMessage());
				}
			}
		}
		return null;
	}
	
	public Map<String, Object> getRestControllersMap() {
		return context.getBeansWithAnnotation((Class<? extends Annotation>) RestController.class);
	}
	
	public Map<String, Object> getRequestMappingMap() {
		return context.getBeansWithAnnotation((Class<? extends Annotation>) RequestMapping.class);
	}
	
	public void addTag(Set<HandlerMethod> handlerMethods, String tagName) {
		handlerMethods.forEach(handlerMethod -> springdocTags.put(handlerMethod, tagName));
	}
}
