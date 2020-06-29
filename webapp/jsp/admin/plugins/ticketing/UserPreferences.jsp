<jsp:useBean id="manageAdminUserPreferences" scope="session" class="fr.paris.lutece.plugins.ticketing.web.user.UserPreferencesJspBean" />
<% String strContent = manageAdminUserPreferences.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<link rel="stylesheet" href="css/admin/plugins/ticketing/ticketing.css" type="text/css"/>

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
