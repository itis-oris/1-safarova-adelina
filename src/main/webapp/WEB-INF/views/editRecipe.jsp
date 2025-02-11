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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/editRecipe.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<div class="edit-recipe-container">
    <h1>Редактировать рецепт</h1>

    <form action="${pageContext.request.contextPath}/recipe/edit/${recipe.id}" method="post" enctype="multipart/form-data">
        <input type="hidden" name="recipeId" value="${recipe.id}"/>

        <div class="form-group">
            <label for="name">Название рецепта:</label>
            <input type="text" id="name" name="name" value="${recipe.name}" required/>
        </div>

        <div class="form-group">
            <label>Обложка рецепта:</label>
            <img src="${pageContext.request.contextPath}/image?file=${recipe.coverImagePath}" alt="Обложка рецепта"
                 class="recipe-cover-image">
        </div>

        <div class="form-group">
            <label for="description">Описание:</label>
            <textarea id="description" name="description" required>${recipe.description}</textarea>
        </div>

        <div class="form-group">
            <label>Категория:</label>
            <div>
                <select id="preferences" name="preferences" multiple>
                    <c:forEach items="${preferences}" var="preference">
                        <option value="${preference}"
                                <c:if test="${userPreferences.contains(preference)}">selected</c:if>>${preference}</option>
                    </c:forEach>
                </select>
            </div>
        </div>

        <div class="form-group">
            <label for="preparationTime">Время приготовления (мин):</label>
            <input type="number" id="preparationTime" name="preparationTime" value="${recipe.preparationTime}"
                   required/>
        </div>

        <div class="form-group">
            <label for="servings">Порции:</label>
            <input type="number" id="servings" name="servings" value="${recipe.servings}" required/>
        </div>

        <div class="form-group">
            <label for="ingridients">Ингредиенты:</label>
            <textarea id="ingridients" name="ingridients" required>${recipe.ingredients}</textarea>
        </div>

        <div class="form-group">
            <label for="steps">Шаги приготовления (через запятую):</label>
            <textarea id="steps" name="steps" required>${recipe.steps}</textarea>
        </div>


        <div class="form-group">
            <label>Дата создания:</label>
            <p>${createdAt}</p>
        </div>

        <div class="form-group">
            <label>Другие изображения:</label>
            <c:forEach var="image" items="${images}">
                <img src="${pageContext.request.contextPath}/image?file=${image.filePath}" alt="Изображение рецепта"
                     class="recipe-image">
            </c:forEach>

        </div>
        <label for="cover">Выбрать новую обложку рецепта:</label>
        <input type="file" id="cover" name="cover" multiple accept="image/*">
        <label for="images">Выбрать новые изображения(можно выбрать несколько):</label>
        <input type="file" id="images" name="images" multiple accept="image/*">


        <button type="submit" class="btn btn-primary">Сохранить изменения</button>
        <a href="${previousPage != null ? previousPage : pageContext.request.contextPath + '/recipe/' + recipe.id}"
           class="btn btn-secondary">Отмена</a>

    </form>
</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>

