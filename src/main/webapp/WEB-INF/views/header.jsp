<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<nav>
    <ul>
        <div class="logo">
            <li><h1 class="site-title">Culinary Exchange</h1></li>
        </div>
        <li><a href="${pageContext.request.contextPath}/">Главная</a></li>
        <c:if test="${user.isAdmin}">
            <li><a href="${pageContext.request.contextPath}/admin">Страница администратора</a></li>
        </c:if>
        <c:if test="${not empty user}">
            <li><a href="${pageContext.request.contextPath}/profile/${user.id}">Профиль</a></li>
        </c:if>
        <li><a href="${pageContext.request.contextPath}/recipe/create">Создать рецепт</a></li>
        <li><a href="${pageContext.request.contextPath}/cookbook">Мои рецепты</a></li>
        <li><a href="${pageContext.request.contextPath}/favoriteRecipes">Любимые рецепты</a></li>
        <li><a href="${pageContext.request.contextPath}/search">Поиск рецептов</a></li>
        <form action="${pageContext.request.contextPath}/search" method="get">
            <input type="text" name="query" placeholder="Поиск рецептов...">
            <button type="submit">Поиск</button>
        </form>
        <div class="user-info">
            <c:choose>
                <c:when test="${not empty user}">
                    <p>Добро пожаловать, ${user.username}!</p>
                    <a href="${pageContext.request.contextPath}/logout">Выйти</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Войти</a> | <a
                        href="${pageContext.request.contextPath}/register">Зарегистрироваться</a>
                </c:otherwise>
            </c:choose>
        </div>
    </ul>

</nav>


<div class="messages">
    <c:if test="${not empty messages}">
        <c:forEach var="message" items="${messages}">
            <div class="message">${message}</div>
        </c:forEach>
    </c:if>
</div>


