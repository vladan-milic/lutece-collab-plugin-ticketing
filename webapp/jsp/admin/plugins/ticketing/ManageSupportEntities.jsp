<jsp:useBean id="manageadminticketingSupportEntities" scope="session" class="fr.paris.lutece.plugins.ticketing.web.SupportEntityJspBean" />
<% String strContent = manageadminticketingSupportEntities.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
