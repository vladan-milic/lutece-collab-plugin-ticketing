<jsp:useBean id="manageadminticketingContactMode" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ContactModeJspBean" />
<% String strContent = manageadminticketingContactMode.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
