<jsp:useBean id="managetickets" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageTicketsJspBean" />

<% String strContent = managetickets.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<script src="js/jquery/plugins/ui/jquery-ui.min.js"></script>
<link rel="stylesheet" href="js/jquery/plugins/ui/css/jquery-ui.min.css" type="text/css"/>

<script src="js/jquery/plugins/ui/ui.datepicker-fr.js" charset="utf-8"></script>

<link rel="stylesheet" href="css/admin/plugins/ticketing/ticketing.css" type="text/css"/>

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>