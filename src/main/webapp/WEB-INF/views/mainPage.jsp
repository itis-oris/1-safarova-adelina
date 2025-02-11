<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>CulinaryExchange</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/mainPage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>
<c:if test="${notAdmin !=null}">
    <p>${notAdmin}</p>
</c:if>
<c:if test="${not empty recipes}">
    <ul class="recipe-list">
        <c:forEach var="recipe" items="${recipes}">
            <li class="recipe-item">
                <a href="${pageContext.request.contextPath}/recipe/${recipe.id}">
                    <c:if test="${not empty recipe.coverImagePath}">
                        <img src="${pageContext.request.contextPath}/image?file=${recipe.coverImagePath}" alt="${recipe.name}"
                             class="recipe-cover">
                    </c:if>

                    <h3>${recipe.name}</h3>
                    <p>${recipe.description}</p>
                </a>
            </li>
        </c:forEach>
    </ul>
</c:if>
<c:if test="${empty recipes}">
    <p>К сожалению, рецепты не найдены.</p>
</c:if>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>




