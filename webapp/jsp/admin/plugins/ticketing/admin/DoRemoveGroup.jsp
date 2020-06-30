<%@ page errorPage="../ErrorPage.jsp" %>

<jsp:useBean id="manageActionButtonRemove" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ManageActionButtonJspBean" />

<% 
	response.sendRedirect( manageActionButtonRemove.doRemoveGroup( request ) );
%>

