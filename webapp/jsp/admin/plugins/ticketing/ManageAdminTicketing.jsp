<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="manageadminticketing" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageAdminTicketingJspBean" />

<% manageadminticketing.init( request, manageadminticketing.RIGHT_MANAGEADMINTICKETING ); %>
<%= manageadminticketing.getManageAdminTicketingHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
