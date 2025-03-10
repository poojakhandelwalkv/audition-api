package com.audition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.LowerCamelCaseStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    /**
     * Creates and configures an ObjectMapper bean for JSON serialization and deserialization.
     *
     * @return a customized ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {

        final ObjectMapper objectMapper = new ObjectMapper();
        //  1. allows for date format as yyyy-MM-dd
        objectMapper.setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.ENGLISH));
        //  2. Does not fail on unknown properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        //  3. maps to camelCase
        objectMapper.setPropertyNamingStrategy(new LowerCamelCaseStrategy());
        //  4. Does not include null values or empty values
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.setSerializationInclusion(Include.NON_EMPTY);
        //  5. does not write dates as timestamps.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));
        restTemplate.getMessageConverters().add(0, getCustomMappingJackson2HttpMessageConverter());

        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(new LoggingInterceptor());
        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    /**
     * Creates a custom MessageConverter with a custom ObjectMapper.
     *
     * @return a custom message converter for JSON processing
     */
    private MappingJackson2HttpMessageConverter getCustomMappingJackson2HttpMessageConverter() {
        final MappingJackson2HttpMessageConverter customConverter = new MappingJackson2HttpMessageConverter();
        customConverter.setObjectMapper(objectMapper());
        return customConverter;
    }


    /**
     * Creates a custom SimpleClientHttpRequestFactory with output streaming disabled.
     *
     * @return customized request factory
     */
    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }

    /**
     * Adds custom interceptors to the application's interceptor registry.
     *
     * @param registry the InterceptorRegistry to register interceptors
     */
    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        final Tracer tracer = GlobalOpenTelemetry.getTracer("audition-api-instrumentation");
        final HandlerInterceptor interceptor = new ResponseHeaderInjector(tracer);
        registry.addInterceptor(interceptor);
    }

}
