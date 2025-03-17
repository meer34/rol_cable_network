package rcn.security.url_encoding;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlObfuscationFilter implements Filter {

	// Pattern to match URIs starting with /css, /js, or /images
	private static final Pattern EXCLUDED_PATTERN = Pattern.compile("^/(css|js|images)/.*|^/(login|logout)/?$");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// Wrap the request with the new URI
		HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(httpRequest) {
			@Override
			public String getRequestURI() {
				// Rewrite the URL
				String requestURI = httpRequest.getRequestURI();
				log.info("##################Normal URL is: " + requestURI);
				if (!EXCLUDED_PATTERN.matcher(requestURI).matches()) {
					requestURI = decode(requestURI.substring(1));
					log.info("##################Encoded URL is: " + requestURI);
				}
				return requestURI;
			}
		};

		chain.doFilter(wrappedRequest, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Initialization logic (if needed)
	}

	@Override
	public void destroy() {
		// Cleanup logic (if needed)
	}

	// Encode a URL path
	public static String encode(String path) {
		return Base64.getUrlEncoder().encodeToString(path.getBytes(StandardCharsets.UTF_8));
	}

	// Decode a URL path
	public static String decode(String encodedPath) {
		return new String(Base64.getUrlDecoder().decode(encodedPath), StandardCharsets.UTF_8);
	}

}