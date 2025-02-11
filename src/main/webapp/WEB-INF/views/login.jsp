<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/login.css">
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
    <h2>Вход в систему</h2>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <div>
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" required>
            <c:if test="${not empty errors['email']}">
                <span class="error">${errors['email']}</span>
            </c:if>
        </div>
        <div>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
            <c:if test="${not empty errors['password']}">
                <span class="error">${errors['password']}</span>
            </c:if>
        </div>
        <c:if test="${not empty param.returnUrl}">
            <input type="hidden" name="returnUrl" value="${param.returnUrl}">
        </c:if>
        <div>
            <button type="submit">Войти</button>
        </div>
    </form>


    <a class="nav-link text-center mt-3" href="${pageContext.request.contextPath}/register">Регистрация</a>

</main>
</body>
</html>
