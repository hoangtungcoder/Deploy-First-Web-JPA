<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Email List</title>
    <link rel="stylesheet" href="main.css">
</head>
<body>
    <h1>Join our email list</h1>

    <p>Nhập họ tên và email để đăng ký.</p>

    <c:if test="${not empty message}">
        <p style="color: red;">
            <c:out value="${message}" />
        </p>
    </c:if>

    <form action="emailList" method="post">
        <label for="email">Email:</label>
        <input type="email"
               id="email"
               name="email"
               maxlength="255"
               value="<c:out value='${user.email}' />"
               required>
        <br>

        <label for="firstName">First Name:</label>
        <input type="text"
               id="firstName"
               name="firstName"
               maxlength="50"
               value="<c:out value='${user.firstName}' />"
               required>
        <br>

        <label for="lastName">Last Name:</label>
        <input type="text"
               id="lastName"
               name="lastName"
               maxlength="50"
               value="<c:out value='${user.lastName}' />"
               required>
        <br>

        <label>&nbsp;</label>
        <input type="submit" value="Join Now" id="submit">
    </form>
</body>
</html>