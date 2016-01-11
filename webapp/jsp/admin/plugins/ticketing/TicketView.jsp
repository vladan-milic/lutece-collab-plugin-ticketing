<jsp:useBean id="ticketview" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketViewJspBean" />

<% String strContent = ticketview.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>