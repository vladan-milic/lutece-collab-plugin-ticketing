<jsp:useBean id="manageadminticketingViewing" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.ViewingJspBean" /> 
<% String strContent = manageadminticketingViewing.processController ( request , response ); %> 
 
<%@ page errorPage="../../../ErrorPage.jsp" %> 
<jsp:include page="../../../AdminHeader.jsp" /> 
 
<%= strContent %> 
 
<%@ include file="../../../AdminFooter.jsp" %>