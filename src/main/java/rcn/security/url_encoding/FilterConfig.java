package rcn.security.url_encoding;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<UrlObfuscationFilter> urlObfuscationFilter() {
        FilterRegistrationBean<UrlObfuscationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UrlObfuscationFilter());
        registrationBean.addUrlPatterns("/*"); // Apply to all URLs
        return registrationBean;
    }
    
    @Bean
    public FilterRegistrationBean<ThymeleafBase64UrlFilter> thymeleafBase64UrlFilter() {
        FilterRegistrationBean<ThymeleafBase64UrlFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ThymeleafBase64UrlFilter());
        registrationBean.addUrlPatterns("/*"); // Apply filter to all requests
        registrationBean.setOrder(Integer.MAX_VALUE); // Ensure it runs last
        return registrationBean;
    }
}