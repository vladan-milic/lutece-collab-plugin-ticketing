<jsp:useBean id="ticketFormFields" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketFormFieldJspBean" />
<% String strContent = ticketFormFields.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
