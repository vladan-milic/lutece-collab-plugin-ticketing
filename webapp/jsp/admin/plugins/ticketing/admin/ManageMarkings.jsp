<jsp:useBean id="manageticketingMarking" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.MarkingJspBean" />
<% String strContent = manageticketingMarking.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
