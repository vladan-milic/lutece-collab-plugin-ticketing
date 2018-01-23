<jsp:useBean id="manageTicketingForm" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ManageFormsJspBean" />
<% String strContent = manageTicketingForm.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
