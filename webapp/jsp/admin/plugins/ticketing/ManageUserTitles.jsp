<jsp:useBean id="manageadminticketingUserTitle" scope="session" class="fr.paris.lutece.plugins.ticketing.web.UserTitleJspBean" />
<% String strContent = manageadminticketingUserTitle.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
