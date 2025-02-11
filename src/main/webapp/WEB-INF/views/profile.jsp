<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Профиль пользователя</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/profile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<div class="profile-container">
    <h1>Профиль пользователя</h1>

    <p class="user-info">
    <div class="avatar-container">
        <img src="${pageContext.request.contextPath}/image?file=${profileUser.avatar}" alt="Аватар пользователя"
             class="avatar">

    </div>

    <h2>${profileUser.username}</h2>
    <p>Email: ${profileUser.email}</p>
    <p>Любимые кухни:
        <c:forEach var="userPreferenceEl" items="${userPreference}">
    <ul class="user-preference-list">
        <li>
                ${userPreferenceEl}
        </li>
    </ul>
    </c:forEach>
    </p>
    <p>Рейтинг: ${userRating}</p>
    <p>Профиль был создан: ${createdAt}</p>

    <c:if test="${user.id == profileUser.id}">
        <a href="${pageContext.request.contextPath}/profile/edit" class="btn">Редактировать профиль</a>
    </c:if>
    <c:if test="${user.id == profileUser.id}">
        <form action="${pageContext.request.contextPath}/profile/${recipe.id}" method="post">
            <input type="hidden" name="action" value="delete">
            <button type="submit" class="delete-btn">Удалить профиль</button>
        </form>
    </c:if>
</div>

<div class="user-recipes">
    <h3>Мои рецепты</h3>
    <p>Создано ${createdRecipes.size()} рецептов.</p>
    <c:if test="${user.id == profileUser.id}">
        <a href="${pageContext.request.contextPath}/cookbook" class="btn">Посмотреть мои рецепты</a>
    </c:if>
</div>

<div class="saved-recipes">
    <h3>Избранные рецепты</h3>
    <p>Добавлено ${favoriteRecipes.size()} рецептов в избранное.</p>
    <c:if test="${user.id == profileUser.id}">
        <a href="${pageContext.request.contextPath}/favoriteRecipes" class="btn">Перейти к избранным</a>
    </c:if>
</div>

<div class="interaction-history">
    <h3>История взаимодействий</h3>
    <c:if test="${not empty comments}">
        <ul>
            <c:forEach var="comment" items="${comments}">
                <li>
                    <a href="${pageContext.request.contextPath}/recipe/${comment.recipe.id}#comment-${comment.id}">
                            ${comment.content}
                    </a> - ${comment.createdAt}
                </li>
            </c:forEach>
        </ul>
    </c:if>
    <c:if test="${empty comments}">
        <p>Нет истории взаимодействий.</p>
    </c:if>
</div>


</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>

