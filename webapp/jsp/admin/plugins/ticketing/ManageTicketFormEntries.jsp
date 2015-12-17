<jsp:useBean id="ticketFormEntry" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketFormEntryJspBean" />
<% String strContent = ticketFormEntry.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
