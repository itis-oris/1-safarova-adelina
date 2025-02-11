<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Создать рецепт</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/createRecipe.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<div class="recipe-form-container">
    <h1>Создать новый рецепт</h1>

    <form class="create-recipe-form" action="${pageContext.request.contextPath}/recipe/create" method="post"
          enctype="multipart/form-data">
        <div class="form-group">
            <label for="title">Название рецепта:</label>
            <input type="text" id="title" name="title" required>
        </div>
        <div class="form-group">
            <label for="description">Описание:</label>
            <textarea id="description" name="description" rows="4" required></textarea>
        </div>

        <div class="form-group">
            <label for="ingredients">Ингредиенты (через запятую):</label>
            <textarea id="ingredients" name="ingredients" rows="4" required></textarea>
        </div>

        <div class="form-group">
            <label for="steps">Шаги приготовления (через запятую):</label>
            <textarea id="steps" name="steps" rows="6" required></textarea>
        </div>

        <div class="form-group">
            <label for="cookingTime">Время приготовления (в минутах):</label>
            <input type="number" id="cookingTime" name="cookingTime" required min="1">
        </div>

        <div class="form-group">
            <label for="servings">Количество порций:</label>
            <input type="number" id="servings" name="servings" required min="1">
        </div>


        <div class="form-group">
            <label>Категория:</label>
            <div>
                <c:forEach var="preference" items="${preferences}">
                    <div>
                        <input type="radio" id="preference-${preference}" name="preference" value="${preference}"
                               required>
                        <label for="preference-${preference}">${preference}</label>
                    </div>
                </c:forEach>
            </div>
            </select>
        </div>

        <label for="cover">Обложка рецепта:</label>
        <input type="file" id="cover" name="cover" multiple accept="image/*">
        <label for="images">Изображения(можно выбрать несколько):</label>
        <input type="file" id="images" name="images" multiple accept="image/*">


        <button class="publish-btn" type="submit">Опубликовать</button>
    </form>

    <c:if test="${not empty errorMessage}">
        <div class="error-message">${errorMessage}</div>
    </c:if>

</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>
<script>
    document.getElementById('cover').addEventListener('change', function (event) {
        if (this.files.length > 1) {
            alert('Вы можете выбрать только одну обложку.');
            this.value = '';
        }
    });
</script>
</body>
</html>

