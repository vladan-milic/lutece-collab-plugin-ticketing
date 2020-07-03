<%@ page errorPage="../ErrorPage.jsp" %>

<jsp:useBean id="manageActionButtonRemove" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ManageGroupActionButtonJspBean" />

<% 
	response.sendRedirect( manageActionButtonRemove.getRemoveGroup( request ) );
%>

