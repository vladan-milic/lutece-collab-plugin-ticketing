<jsp:useBean id="managetickets" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageTicketsJspBean" />

<% String strContent = managetickets.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<script src="js/bootstrap-datepicker.js"></script>
<script src="js/locales/bootstrap-datepicker.fr.js" charset="utf-8"></script>

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>