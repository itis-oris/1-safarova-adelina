<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/register.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>
<header>
    <nav>
        <a href="${pageContext.request.contextPath}/main">Главная</a>
    </nav>
</header>

<main>
    <h2>Регистрация нового пользователя</h2>
    <form action="${pageContext.request.contextPath}/register" method="post">
        <div>
            <label for="name">Имя:</label>
            <input type="text" id="name" name="name" required>
            <c:if test="${not empty errors['name']}">
                <span class="error">${errors['name']}</span>
            </c:if>
        </div>
        <div>
            <label for="reg-email">Email:</label>
            <input type="email" id="reg-email" name="email" required>
            <c:if test="${not empty errors['email']}">
                <span class="error">${errors['email']}</span>
            </c:if>
        </div>
        <div>
            <label for="reg-password">Пароль:</label>
            <input type="password" id="reg-password" name="password" required>
            <c:if test="${not empty errors['password']}">
                <span class="error">${errors['password']}</span>
            </c:if>
        </div>
        <div>
            <label for="reg-password-confirm">Подтвердите пароль:</label>
            <input type="password" id="reg-password-confirm" name="passwordConfirm" required>
            <c:if test="${not empty errors['passwordConfirm']}">
                <span class="error">${errors['passwordConfirm']}</span>
            </c:if>
        </div>
        <div>
            <label>Выберите предпочтения в кухне:</label>
            <div>
                <c:forEach var="preference" items="${preferences}">
                    <div>
                        <input type="checkbox" id="preference-${preference}" name="preferences" value="${preference}">
                        <label for="preference-${preference}">${preference}</label>
                    </div>
                </c:forEach>
            </div>
        </div>
        <div>
            <button type="submit">Зарегистрироваться</button>
        </div>
    </form>

    <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войти</a></p>
</main>

<%@ include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>

