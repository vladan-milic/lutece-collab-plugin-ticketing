<jsp:useBean id="manageTicketForm" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketFormJspBean" />

<% manageTicketForm.init( request, manageTicketForm.RIGHT_MANAGETICKETFORM ); %>
<% String strContent = manageTicketForm.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
