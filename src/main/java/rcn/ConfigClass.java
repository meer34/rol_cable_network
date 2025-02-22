package rcn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import rcn.web.util.DateFormatter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebMvc
public class ConfigClass extends WebMvcConfigurerAdapter {
	public void addFormatters(FormatterRegistry formatterRegistry) {
		formatterRegistry.addFormatter(dateFormatter());
	}
	@Bean
	public DateFormatter dateFormatter() {
		return new DateFormatter();
	}
}