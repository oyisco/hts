<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Spring MVC CSV Download Example</title>
</head>
<body>
<form:form action="downloadCSV" method="post" id="downloadCSV">
    <fieldset style="width: 400px;">
        <h3>Spring MVC CSV Download Example</h3>
        <input id="submitId" type="submit" value="Downlaod CSV">
    </fieldset>
</form:form>
</body>
</html>