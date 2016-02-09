<jsp:useBean id="manageAdminUserPreferences" scope="session" class="fr.paris.lutece.plugins.ticketing.web.user.UserPreferencesJspBean" />
<% String strContent = manageAdminUserPreferences.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
