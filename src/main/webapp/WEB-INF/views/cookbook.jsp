<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Моя кулинарная книга</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/cookbook.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<main class="cookbook-container">
    <h1>Моя кулинарная книга</h1>

    <c:if test="${not empty user.createdRecipes}">
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
                            <c:forEach var="recipe" items="${user.createdRecipes}" varStatus="status">
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
                                            <a href="${pageContext.request.contextPath}/recipe/edit/${recipe.id}"
                                               class="edit-btn">Редактировать</a>
                                            <form id="deleteForm-${recipe.id}"
                                                  action="${pageContext.request.contextPath}/cookbook" method="post"
                                                  class="delete-form">
                                                <input type="hidden" name="recipeId" value="${recipe.id}">
                                                <button class="delete-btn" type="submit"
                                                        onclick="return confirm('Вы уверены, что хотите удалить этот рецепт?');">Удалить
                                                </button>
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

    <c:if test="${empty user.createdRecipes}">
        <p class="empty-message">Ваша кулинарная книга пуста. Сохраните рецепты, чтобы они отображались здесь.</p>
    </c:if>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>