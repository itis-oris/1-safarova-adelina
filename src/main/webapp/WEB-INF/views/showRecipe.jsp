<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/header.jsp" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>${recipe.name}</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Bootstrap JS Bundle (включает Popper.js) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/normalize.8.0.1.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/showRecipe.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/footer.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>
<div class="recipe-container">
    <h1>${recipe.name}</h1>
    <p>автор:
        <a href="${pageContext.request.contextPath}/profile/${authorId}">
            ${authorName}
        </a></p>


    <c:if test="${not empty recipe.coverImagePath}">
        <img src="${pageContext.request.contextPath}/image?file=${recipe.coverImagePath}" alt="Обложка рецепта"
             class="recipe-cover-image">
    </c:if>

    <div class="recipe-details">
        <p><strong>Описание:</strong> ${recipe.description}</p>
        <p><strong>Категории:</strong> ${recipe.category}</p>
        <p><strong>Время приготовления:</strong> ${recipe.preparationTime} минут</p>
        <p><strong>Порции:</strong> ${recipe.servings}</p>


        <h2>Ингредиенты (через запятую):</h2>
        <ul>
            <c:forEach var="ingredient" items="${recipe.ingredients}">
                <li>${ingredient}</li>
            </c:forEach>
        </ul>


        <h2>Шаги приготовления (через запятую):</h2>
        <ol>
            <c:forEach var="step" items="${recipe.steps}">
                <li>${step}</li>
            </c:forEach>
        </ol>
        <c:if test="${not empty images}">
            <div id="recipeCarousel" class="carousel slide" data-bs-ride="carousel">
                <!-- Индикаторы -->
                <div class="carousel-indicators">
                    <c:forEach var="image" items="${images}" varStatus="status">
                        <button type="button" data-bs-target="#recipeCarousel" data-bs-slide-to="${status.index}"
                                class="${status.first ? 'active' : ''}"
                                aria-current="${status.first ? 'true' : 'false'}"
                                aria-label="Slide ${status.index + 1}"></button>
                    </c:forEach>
                </div>

                <!-- Слайды -->
                <div class="carousel-inner">
                    <c:forEach var="image" items="${images}" varStatus="status">
                        <div class="carousel-item ${status.first ? 'active' : ''}">
                            <img src="${pageContext.request.contextPath}/image?file=${image.filePath}" alt="Изображение рецепта"
                                 class="d-block w-100">
                        </div>
                    </c:forEach>

                </div>

                <!-- Кнопки управления -->
                <button class="carousel-control-prev" type="button" data-bs-target="#recipeCarousel"
                        data-bs-slide="prev">
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Предыдущий</span>
                </button>
                <button class="carousel-control-next" type="button" data-bs-target="#recipeCarousel"
                        data-bs-slide="next">
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="visually-hidden">Следующий</span>
                </button>
            </div>
        </c:if>

        <p><strong>Дата создания:</strong> ${recipe.createdAt}</p>
    </div>

    <div class="actions">
        <form action="${pageContext.request.contextPath}/saveToFavorites" method="post">
            <input type="hidden" name="recipeId" value="${recipe.id}">
            <button type="submit">Сохранить рецепт в любимые</button>
        </form>

        <c:if test="${recipe.user.id == user.id}">
            <form action="${pageContext.request.contextPath}/recipe/edit/${recipe.id}" class="edit-btn" method="get">
                <button type="submit">Редактировать</button>
            </form>
            <form action="${pageContext.request.contextPath}/recipe/${recipe.id}" method="post">
                <input type="hidden" name="action" value="delete">
                <button type="submit"
                        onclick="return confirm('Вы уверены, что хотите удалить этот рецепт?');">Удалить
                </button>
            </form>
        </c:if>
    </div>

    <div class="rating">
        <h2>Средняя оценка: ${averageRating}</h2>
        <c:if test="${recipe.user.id != user.id}">
            <form action="${pageContext.request.contextPath}/recipe/${recipe.id}" method="post">
                <input type="hidden" name="action" value="rate">
                <input type="hidden" name="recipeId" value="${recipe.id}">
                <select name="rating" required>
                    <option value="">Выберите оценку</option>
                    <c:forEach var="i" begin="1" end="5">
                        <option value="${i}">
                            <c:choose>
                                <c:when test="${i == 1}">
                                    ${i} звезда
                                </c:when>
                                <c:when test="${i >= 2 && i <= 4}">
                                    ${i} звезды
                                </c:when>
                                <c:otherwise>
                                    ${i} звёзд
                                </c:otherwise>
                            </c:choose>
                        </option>
                    </c:forEach>
                </select>
                <button type="submit">Оценить</button>
            </form>
        </c:if>
    </div>

    <div class="comments-section" id="comment-${comment.id}">

        <h3>Добавить комментарий:</h3>
        <form action="${pageContext.request.contextPath}/recipe/${recipe.id}" method="post">
            <input type="hidden" name="action" value="addComment">
            <textarea name="commentText" rows="4" required></textarea>
            <button type="submit">Отправить</button>
        </form>
        <h2>Комментарии:</h2>
        <c:choose>
            <c:when test="${not empty comments}">
                <c:forEach var="comment" items="${comments}">
                    <div class="comment" id="comment-${comment.id}">
                        <p><strong>${comment.user.username}:</strong> ${comment.content}</p>
                        <p><em>${comment.createdAt}</em></p>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p>Комментариев пока нет</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jsp" %>

</body>
</html>

