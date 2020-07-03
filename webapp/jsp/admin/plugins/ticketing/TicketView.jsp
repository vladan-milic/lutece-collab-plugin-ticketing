<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<jsp:useBean id="ticketview" scope="session" class="fr.paris.lutece.plugins.ticketing.web.TicketViewJspBean" />

<% String strContent = ticketview.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<link rel="stylesheet" href="css/admin/plugins/ticketing/ticketing.css" type="text/css"/>

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>