<jsp:useBean id="manageadminticketingTicketCategory" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketCategoryJspBean" />
<% String strContent = manageadminticketingTicketCategory.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
