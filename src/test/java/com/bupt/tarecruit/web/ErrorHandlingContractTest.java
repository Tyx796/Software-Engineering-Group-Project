package com.bupt.tarecruit.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ErrorHandlingContractTest {
    @Test
    void webXmlMapsGlobalErrorHandlingFilterBeforeProtectedRouteFilters() throws Exception {
        String webXml = read("src/main/webapp/WEB-INF/web.xml");
        int errorFilter = webXml.indexOf("<filter-name>ErrorHandlingFilter</filter-name>");
        int authenticationFilter = webXml.indexOf("<filter-name>AuthenticationFilter</filter-name>");

        assertTrue(errorFilter >= 0, "web.xml should declare ErrorHandlingFilter.");
        assertTrue(authenticationFilter > errorFilter,
                "ErrorHandlingFilter should be declared before authentication and authorization filters.");
        assertTrue(webXml.contains("<filter-class>com.bupt.tarecruit.filter.ErrorHandlingFilter</filter-class>"));
        assertTrue(webXml.contains("<url-pattern>/*</url-pattern>"));
        assertTrue(webXml.contains("<location>/jsp/error/error.jsp</location>"));
    }

    @Test
    void errorHandlingFilterLogsUnexpectedErrorsAndForwardsToGenericErrorPage() throws Exception {
        String filter = read("src/main/java/com/bupt/tarecruit/filter/ErrorHandlingFilter.java");

        assertTrue(filter.contains("Logger.getLogger(ErrorHandlingFilter.class.getName())"));
        assertTrue(filter.contains("httpRequest.getMethod() + \" \" + httpRequest.getRequestURI()"));
        assertTrue(filter.contains("HttpServletResponse.SC_INTERNAL_SERVER_ERROR"));
        assertTrue(filter.contains("/jsp/error/error.jsp"));
        assertTrue(filter.contains("An unexpected error occurred. Please try again later."));
    }

    @Test
    void cvUploadDoesNotExposeStorageExceptionMessagesToUsers() throws Exception {
        String servlet = read("src/main/java/com/bupt/tarecruit/servlet/CvUploadServlet.java");

        assertTrue(servlet.contains("We could not save your CV. Please try again later."));
        assertTrue(servlet.contains("LOGGER.log"));
        assertFalse(servlet.contains("IllegalArgumentException | IllegalStateException exception"),
                "Validation messages may be shown to users, but storage exception messages must be hidden.");
    }

    private String read(final String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
