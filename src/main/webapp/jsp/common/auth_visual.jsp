<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    String buptLogoFile = null;
    try {
        if (application.getResource("/images/bupt-logo.svg") != null) {
            buptLogoFile = "bupt-logo.svg";
        } else if (application.getResource("/images/bupt-logo.png") != null) {
            buptLogoFile = "bupt-logo.png";
        }
    } catch (java.net.MalformedURLException ignored) {
        buptLogoFile = null;
    }
    pageContext.setAttribute("buptLogoFile", buptLogoFile);
%>
<div class="auth-hero-art" aria-hidden="true">
    <div class="auth-hero-art-media"
         style="background-image: url('${pageContext.request.contextPath}/images/auth-campus-hero.png');"></div>
    <c:if test="${not empty buptLogoFile}">
        <div class="auth-logo-badge">
            <img src="${pageContext.request.contextPath}/images/${buptLogoFile}" alt="BUPT logo">
        </div>
    </c:if>
</div>
