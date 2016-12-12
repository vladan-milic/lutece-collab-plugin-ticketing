<jsp:useBean id="manageTicketInputs" scope="session" class="fr.paris.lutece.plugins.ticketing.web.admin.TicketInputsJspBean" />

<% manageTicketInputs.init( request, manageTicketInputs.RIGHT_MANAGETICKETINPUTS ); %>
<% String strContent = manageTicketInputs.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
