<jsp:useBean id="manageadminticketingTicketType" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.TicketTypeJspBean" />
<% String strContent = manageadminticketingTicketType.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
