<jsp:useBean id="manageticketingreponsestypesTypeResponse" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TypeResponseJspBean" />
<% String strContent = manageticketingreponsestypesTypeResponse.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
