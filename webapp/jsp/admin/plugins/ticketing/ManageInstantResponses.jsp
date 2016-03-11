<jsp:useBean id="manageinstantresponseInstantResponse" scope="session" class="fr.paris.lutece.plugins.ticketing.web.InstantResponseJspBean" />
<% String strContent = manageinstantresponseInstantResponse.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
