package org.hhplus.ticketing.support.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직
        log.info("LoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // 요청 정보 로깅
            logRequestDetails(httpRequest);

            // 응답 정보를 로깅하기 위해 ResponseWrapper 사용
            ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);

            // 필터 체인을 통해 다음 필터 또는 서블릿으로 요청을 전달
            chain.doFilter(request, responseWrapper);

            // 응답 정보 로깅
            logResponseDetails(responseWrapper);

            byte[] content = responseWrapper.getContent();
            httpResponse.getOutputStream().write(content);
        } else {
            chain.doFilter(request, response);
        }
    }

    private void logRequestDetails(HttpServletRequest request) {
        log.info("Request URL: {}", request.getRequestURL().toString());
        log.info("Request Method: {}", request.getMethod());
        log.info("Request Headers: {}", getHeaders(request));
        log.info("Request Parameters: {}", getParameters(request));
    }

    private void logResponseDetails(ResponseWrapper responseWrapper) throws IOException {
        log.info("Response Status: {}", responseWrapper.getStatus());
        log.info("Response Body: {}", new String(responseWrapper.getContent(), responseWrapper.getCharacterEncoding()));
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.append(headerName).append(": ").append(headerValue).append(" ");
        }
        return headers.toString().trim();
    }

    private String getParameters(HttpServletRequest request) {
        StringBuilder params = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                params.append(paramName).append("=").append(paramValue).append(" ");
            }
        } else {
            params.append("None");
        }
        return params.toString().trim();
    }

    @Override
    public void destroy() {
        // 필터 클린업
        log.info("LoggingFilter destroyed");
    }

    private static class ResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        private ServletOutputStream outputStream;
        private PrintWriter writer;

        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }
            if (outputStream == null) {
                outputStream = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        byteArrayOutputStream.write(b);
                    }

                    @Override
                    public boolean isReady() {
                        return true;
                    }

                    @Override
                    public void setWriteListener(WriteListener writeListener) {
                        // No-op implementation
                    }
                };
            }
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }
            if (writer == null) {
                writer = new PrintWriter(byteArrayOutputStream);
            }
            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            } else if (outputStream != null) {
                outputStream.flush();
            }
            super.flushBuffer();
        }

        public byte[] getContent() throws IOException {
            flushBuffer();
            return byteArrayOutputStream.toByteArray();
        }
    }
}