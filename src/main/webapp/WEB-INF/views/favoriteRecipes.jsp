<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Избранные рецепты</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/cookbook.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<main class="cookbook-container">
    <h1>Избранные рецепты</h1>

    <c:if test="${not empty user.favoriteRecipes}">
        <section class="categories-section">
            <h2>Категории</h2>
            <h3>Быстрый переход по категориям</h3>

            <div class="categories">
                <c:forEach var="preference" items="${preferences}">
                    <li><a href="#${preference}" class="category-link">${preference}</a></li>
                </c:forEach>
            </div>

            <div class="recipes">
                <c:forEach var="preference" items="${preferences}">
                    <section class="category-recipes">
                        <h3 id="${preference}" class="category-title">${preference}</h3>
                        <div class="recipe-list">
                            <c:forEach var="recipe" items="${user.favoriteRecipes}" varStatus="status">
                                <c:if test="${recipe.category == preference}">
                                    <div class="recipe-item">
                                        <h4>
                                            <a href="${pageContext.request.contextPath}/recipe/${recipe.id}">${recipe.name}</a>
                                        </h4>
                                        <div class="recipe-cover-wrapper">
                                            <c:if test="${not empty recipe.coverImagePath}">
                                                <img src="${pageContext.request.contextPath}/image?file=${recipe.coverImagePath}" alt="${recipe.name}"
                                                     class="recipe-cover">
                                            </c:if>

                                        </div>
                                        <p class="recipe-description">${recipe.description}</p>
                                        <p class="recipe-time">Время приготовления: ${recipe.preparationTime} мин</p>
                                        <div class="recipe-actions">
                                            <a href="#"
                                               onclick="event.preventDefault(); document.getElementById('removeForm-${recipe.id}').submit();"
                                               class="delete-btn">Удалить из избранного</a>
                                            <form id="removeForm-${recipe.id}"
                                                  action="${pageContext.request.contextPath}/favoriteRecipes"
                                                  method="post" style="display: none;">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="recipeId" value="${recipe.id}">
                                            </form>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </section>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <c:if test="${empty user.favoriteRecipes}">
        <p class="empty-message">У вас пока нет избранных рецептов. Добавьте рецепты в избранное, чтобы они отображались
            здесь.</p>
    </c:if>

</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>