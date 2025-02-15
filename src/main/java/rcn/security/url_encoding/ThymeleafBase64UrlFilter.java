package rcn.security.url_encoding;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ThymeleafBase64UrlFilter implements Filter {

    private static final Pattern HREF_PATTERN = Pattern.compile("href=\"([^\"]+)\"");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip filter if not HTML content
        if (httpResponse.getContentType() == null || !httpResponse.getContentType().contains("text/html")) {
            chain.doFilter(request, response);
            return;
        }

        CharResponseWrapper responseWrapper = new CharResponseWrapper(httpResponse);
        chain.doFilter(request, responseWrapper);

        String originalContent = responseWrapper.toString();
        String modifiedContent = encodeUrls(originalContent);

        response.setContentLength(modifiedContent.getBytes(StandardCharsets.UTF_8).length);
        response.getWriter().write(modifiedContent);
    }

    private String encodeUrls(String content) {
        Matcher matcher = HREF_PATTERN.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String originalUrl = matcher.group(1);
            if (!originalUrl.startsWith("http") && !originalUrl.contains("base64")) { 
                String encodedUrl = "data:text/plain;base64," + Base64.getUrlEncoder()
                        .encodeToString(originalUrl.getBytes(StandardCharsets.UTF_8));
                
                // Escape special characters before replacement
                String safeUrl = Matcher.quoteReplacement(encodedUrl);
                matcher.appendReplacement(sb, "href=\"" + safeUrl + "\"");
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public void destroy() {}

    private static class CharResponseWrapper extends HttpServletResponseWrapper {
        private final CharArrayWriter charArrayWriter = new CharArrayWriter();
        private final PrintWriter writer = new PrintWriter(charArrayWriter);
        private ServletOutputStream outputStream;
        private boolean isWriterUsed = false;
        private boolean isStreamUsed = false;

        public CharResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public PrintWriter getWriter() {
            if (isStreamUsed) {
                throw new IllegalStateException("getOutputStream() already called.");
            }
            isWriterUsed = true;
            return writer;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (isWriterUsed) {
                throw new IllegalStateException("getWriter() already called.");
            }
            if (outputStream == null) {
                outputStream = super.getOutputStream();
            }
            isStreamUsed = true;
            return outputStream;
        }

        @Override
        public String toString() {
            writer.flush();
            return charArrayWriter.toString();
        }
    }
}
