<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Поиск рецептов</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/search.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/mainPage.css">
</head>
<body>

<div class="search-container">
    <h1>Поиск рецептов</h1>
    <form action="${pageContext.request.contextPath}/search" method="get">
        <input type="text" name="query" placeholder="Введите ключевые слова..."
               value="${param.query != null ? param.query : ''}">

        <label for="category">Категория:</label>
        <select name="category" id="category">
            <option value="" ${param.category == null ? 'selected' : ''}>Выберете категорию</option>

            <c:forEach var="preference" items="${preferences}">
                <option value="${preference}" ${param.category == preference ? 'selected' : ''}>
                    <li><a href="#${preference}">${preference}</a></li>
                </option>
            </c:forEach>


        </select>

        <button type="submit">Найти</button>
    </form>
</div>

<div class="results-container">
    <h2>Результаты поиска:</h2>
    <c:if test="${not empty recipes}">
        <ul class="recipe-list">
            <c:forEach var="recipe" items="${recipes}">
                <li class="recipe-item">
                    <a href="${pageContext.request.contextPath}/recipe/${recipe.id}">
                        <div class="recipe-cover-wrapper">
                            <c:if test="${not empty recipe.coverImagePath}">
                                <img src="${pageContext.request.contextPath}/image?file=${recipe.coverImagePath}" alt="${recipe.name}"
                                     class="recipe-cover">
                            </c:if>

                        </div>
                        <h3>${recipe.name}</h3>
                        <p>${recipe.description}</p>
                    </a>
                </li>
            </c:forEach>
        </ul>
    </c:if>
    <c:if test="${empty recipes}">
        <p>К сожалению, рецепты не найдены. Попробуйте изменить параметры поиска.</p>
    </c:if>
</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>

