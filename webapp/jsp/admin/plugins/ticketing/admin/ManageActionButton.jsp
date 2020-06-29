<jsp:useBean id="manageActionButton" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ManageActionButtonJspBean" />
<% String strContent = manageActionButton.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
