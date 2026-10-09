<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Available cars | Meghu Holidays</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <a class="brand" href="${pageContext.request.contextPath}/"><span class="brand-mark">M</span> meghu<span>holidays</span></a>
    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/booking.html">Plan your trip</a>
        <c:if test="${not empty sessionScope.user}">
            <a href="${pageContext.request.contextPath}/my-bookings">My bookings</a>
            <form action="${pageContext.request.contextPath}/logout" method="post"><button class="nav-button">Sign out</button></form>
        </c:if>
    </nav>
</header>
<main class="section fleet-page">
    <div class="section-heading">
        <div><span class="eyebrow">MEGHU HOLIDAYS · OUR CARS</span><h1>Find your ride<br>for the road ahead.</h1></div>
        <p>your dream is our destination. Pick a comfortable car for a quick city ride, a family break, or a journey out of town.</p>
    </div>
    <c:if test="${empty cars}"><div class="empty-state"><span>✦</span><h2>Our fleet is taking a breather.</h2><p>Please check back soon for available rides.</p></div></c:if>
    <div class="car-grid">
        <c:forEach var="car" items="${cars}">
            <article class="car-card">
                <div class="car-image">
                    <c:choose>
                        <c:when test="${not empty car.imageUrl}"><img src="<c:out value='${car.imageUrl}'/>" alt="<c:out value='${car.name}'/>" loading="lazy"></c:when>
                        <c:otherwise><div class="car-placeholder">M</div></c:otherwise>
                    </c:choose>
                    <span class="car-category"><c:out value="${car.category}"/></span>
                </div>
                <div class="car-details">
                    <div class="car-title"><h2><c:out value="${car.name}"/></h2><span>✦</span></div>
                    <p><c:out value="${car.description}"/></p>
                    <div class="car-meta"><span>♧ <c:out value="${car.seats}"/> seats</span><span>with driver</span></div>
                    <div class="car-book-row">
                        <div><strong>₹<c:out value="${car.pricePerDay}"/></strong><small> / day</small></div>
                        <c:choose>
                            <c:when test="${not empty sessionScope.user}"><a class="button button-small" href="${pageContext.request.contextPath}/booking?carId=${car.id}">Book this car <span aria-hidden="true">→</span></a></c:when>
                            <c:otherwise><c:url var="enquiryUrl" value="/booking.html"><c:param name="car" value="${car.name}"/></c:url><a class="button button-small" href="${enquiryUrl}">Enquire <span aria-hidden="true">→</span></a></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </article>
        </c:forEach>
    </div>
</main>
<footer class="site-footer">
    <a class="brand brand-light" href="${pageContext.request.contextPath}/"><span class="brand-mark">M</span> meghu<span>holidays</span></a>
    <span>your dream is our destination</span><span>© 2026 Meghu Holidays</span>
</footer>
</body>
</html>
