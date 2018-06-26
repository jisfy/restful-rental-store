package com.chompchompfig.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
@ComponentScan({"com.chompchompfig.store.infrastructure.rest", "com.chompchompfig.store.domain",
		"com.chompchompfig.store.application"})
public class StoreApplication {

	public static final String CONTACT_URL = "https://www.thetractorbeamhasyou.com";
	public static final String CONTACT_NAME = "jisfy";
	public static final String CONTACT_EMAIL_ADDRESS = "thetractorbeamhasyou@gmail.com";
	public static final String API_DESCRIPTION = "a RESTful API for a simple Video Rental business";
	public static final String API_TITLE = "Video Rental Store";
	public static final String API_VERSION = "0.0.1";
	public static final String API_LICENSE_NAME = "LGPL";
	public static final String API_LICENSE_URL = "https://es.wikipedia.org/wiki/GNU_Lesser_General_Public_License";
	public static final String TERMS_OF_SERVICE_URL = "";

	public static void main(String[] args) {
		SpringApplication.run(StoreApplication.class, args);
	}

	@Bean
	public Docket api() {
		Contact jisfContact = new Contact(CONTACT_NAME, CONTACT_URL, CONTACT_EMAIL_ADDRESS);
		ApiInfo apiInfo = new ApiInfo(API_TITLE, API_DESCRIPTION, API_VERSION, TERMS_OF_SERVICE_URL, jisfContact
				 , API_LICENSE_NAME, API_LICENSE_URL, Collections.EMPTY_LIST);
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("ex", new UriTemplate("/docs/rel_{rel}.html"));
    }
}
