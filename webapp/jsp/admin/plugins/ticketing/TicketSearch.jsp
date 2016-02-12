<jsp:useBean id="ticketsearch" scope="session" class="fr.paris.lutece.plugins.ticketing.web.search.TicketSearchJspBean" />

<% String strContent = ticketsearch.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>