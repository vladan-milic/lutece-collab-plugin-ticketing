<jsp:useBean id="managetickets" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageTicketsJspBean" />

<% String strContent = managetickets.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<script src="js/jquery-ui.datepicker.min.js"></script>

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>