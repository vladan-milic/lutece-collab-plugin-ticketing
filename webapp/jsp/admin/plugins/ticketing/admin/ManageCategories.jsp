<jsp:useBean id="manageTicketingCategory" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.CategoryJspBean" />
<% String strContent = manageTicketingCategory.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
