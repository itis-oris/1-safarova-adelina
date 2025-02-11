<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Страница администратора</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/admin.css">
</head>
<body>

<main class="admin-container">
    <div class="preferences-container">
        <h1>Изменить категории</h1>
        <c:if test="${error != null}">
            <input type="hidden" class="error" value="${error}">
            <script>
                const err = document.querySelector(".error")
                alert(err.value);
            </script>
        </c:if>
        <c:forEach var="preference" items="${preferences}">
            <li>
                <p class="preference-name">${preference}</p>
                <form id="delete-category-form" method="post">
                    <input type="hidden" name="deleteCategoryName" value="${preference}">
                    <button class="delete-category" id="delete-category" type="submit">Удалить</button>
                </form>
            </li>
        </c:forEach>
        <form id="add-category-form" method="post">
            <input class="input-category" type="text" id="new-category" name="newCategory">
            <button class="add-category" type="submit">Добавить категорию</button>
        </form>
    </div>

    <div class="user-container">
        <h1>Выдать права администратора пользователю/ Лишить прав администратора пользователя</h1>


        <h1>Поиск пользователей</h1>
        <form action="${pageContext.request.contextPath}/admin" method="get">
            <input type="text" name="query" placeholder="Введите ключевые слова..."
                   value="${param.query != null ? param.query : ''}">

            <button class="add-category" type="submit">Найти</button>
        </form>

        <h2>Результаты поиска:</h2>
        <c:if test="${not empty users}">
            <ul class="user-list">
                <c:forEach var="user" items="${users}">
                    <li class="user-item">
                        <a href="${pageContext.request.contextPath}/profile/${user.id}">

                            <c:if test="${not empty user.avatar}">
                                <form action="${pageContext.request.contextPath}/image" method="get">
                                    <input type="hidden" name="fileName" value="${user.avatar}">
                                </form>
                                <img src="${pageContext.request.contextPath}/image?file=${user.avatar}" alt="Аватар пользователя"
                                     class="avatar">
                            </c:if>
                            <c:if test="${empty user.avatar}">
                                <img src="${pageContext.request.contextPath}/image?file=${user.avatar}" alt="Аватар пользователя"
                                     class="avatar">
                            </c:if>
                            <h3>${user.username}</h3>
                        </a>
                        <c:if test="${user.isAdmin}">
                            <p>Администратор</p>
                            <form id="revoke-rights-form" method="post">
                                <input type="hidden" name="revokeRights" value="${user.id}">
                                <button class="revoke-rights-btn" type="submit">Лишить прав администратора</button>
                            </form>
                        </c:if>
                        <c:if test="${!user.isAdmin}">
                            <p>Пользователь</p>
                            <form id="grant-rights-form" method="post">
                                <input type="hidden" name="grantRights" value="${user.id}">
                                <button class="grant-rights-btn" type="submit">Выдать права администратора</button>
                            </form>
                        </c:if>

                    </li>
                </c:forEach>
            </ul>
        </c:if>
        <c:if test="${empty users}">
            <p>К сожалению, пользователи не найдены. Попробуйте изменить параметры поиска.</p>
        </c:if>
    </div>

</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>
