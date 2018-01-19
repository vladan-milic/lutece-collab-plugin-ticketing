<jsp:useBean id="manageForm" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ManageFormsJspBean" />
<% String strContent = manageForm.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
