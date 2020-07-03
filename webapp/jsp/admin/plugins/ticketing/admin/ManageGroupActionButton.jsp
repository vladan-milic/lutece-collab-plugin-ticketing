<jsp:useBean id="manageGroupActionButton" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ManageGroupActionButtonJspBean" />
<% String strContent = manageGroupActionButton.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
