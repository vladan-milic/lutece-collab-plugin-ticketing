<jsp:useBean id="ticketInputFields" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.TicketInputFieldJspBean" />
<% String strContent = ticketInputFields.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
