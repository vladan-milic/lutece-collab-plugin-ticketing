<jsp:useBean id="manageadminticketingWorkflow" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.WorkflowJspBean" />
<% String strContent = manageadminticketingWorkflow.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
