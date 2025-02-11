<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Редактировать рецепт</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/editProfile.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<div class="edit-recipe-container">
    <h1>Редактировать профиль</h1>

    <div class="form-group">
        <c:if test="${not empty user.avatar}">
            <img src="${pageContext.request.contextPath}/image?file=${user.avatar}" alt="Аватар пользователя"
                 class="avatar">
        </c:if>

        <c:if test="${empty user.avatar}">
            <img src="${pageContext.request.contextPath}/image?file=${user.avatar}" alt="Аватар пользователя"
                 class="avatar">
        </c:if>
    </div>

    <form action="${pageContext.request.contextPath}/profile/edit" method="post" enctype="multipart/form-data">
        <input type="hidden" name="userId" value="${user.id}"/>

        <div class="form-group">
            <label for="avatar">Изменить аватар:</label>
            <input type="file" id="avatar" name="avatar" accept="image/*"/>
        </div>

        <div class="form-group">
            <label for="name">Имя пользователя:</label>
            <input type="text" id="name" name="name" value="${user.username}" required/>
        </div>

        <div class="form-group">
            <label for="email">Email:</label>
            <textarea id="email" name="email" required>${user.email}</textarea>
        </div>

        <div class="form-group">
            <label>Категория:</label>
            <div>
                <c:forEach var="preference" items="${preferences}">
                    <div>
                        <c:set var="isChecked" value="${fn:contains(category, preference)}"/>
                        <input type="checkbox" id="preference-${preference}" name="preferences" value="${preference}"
                               <c:if test="${isChecked}">checked</c:if>/>
                        <label for="preference-${preference}">${preference}</label>
                    </div>
                </c:forEach>
            </div>
        </div>


        <div class="form-group">
            <div>
                <label>Рейтинг:</label>
                <p>${userRating}</p>
            </div>
            <div>
                <label>Дата создания:</label>
                <p>${createdAt}</p>
            </div>

        </div>

        <button type="submit" class="btn btn-primary">Сохранить изменения</button>
        <a href="${previousPage != null ? previousPage : pageContext.request.contextPath + '/profile/' + user.id}"
           class="btn btn-secondary">Отмена</a>

    </form>
</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>
