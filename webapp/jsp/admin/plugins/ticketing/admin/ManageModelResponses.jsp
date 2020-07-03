<jsp:useBean id="manageticketingModelResponse" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ModelResponseJspBean" />
<% String strContent = manageticketingModelResponse.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<link rel="stylesheet" href="css/admin/plugins/ticketing/ticketing.css" type="text/css"/>

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
