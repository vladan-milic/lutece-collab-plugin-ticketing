<jsp:useBean id="manageTicketingCategoryType" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.CategoryTypeJspBean" />
<% String strContent = manageTicketingCategoryType.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
