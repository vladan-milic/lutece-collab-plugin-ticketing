<jsp:useBean id="manageadminticketingTicketDomain" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketDomainJspBean" />
<% String strContent = manageadminticketingTicketDomain.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
