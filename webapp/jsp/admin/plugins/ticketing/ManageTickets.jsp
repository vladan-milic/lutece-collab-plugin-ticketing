<jsp:useBean id="managetickets" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageTicketsJspBean" />

<% String strContent = managetickets.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>