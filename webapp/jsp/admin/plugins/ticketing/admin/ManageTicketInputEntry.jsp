<jsp:useBean id="ticketInputEntry" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.TicketInputEntryJspBean" />
<% String strContent = ticketInputEntry.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
