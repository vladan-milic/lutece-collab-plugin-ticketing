<jsp:useBean id="manageticketingTypepicalResponse" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.TypicalResponseJspBean" />
<% String strContent = manageticketingTypepicalResponse.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
