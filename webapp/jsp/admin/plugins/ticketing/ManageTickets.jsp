<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="managetickets" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageTicketsJspBean" />

<% managetickets.init( request, managetickets.RIGHT_MANAGETICKETS ); %>
<%= managetickets.getManageTickets( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
