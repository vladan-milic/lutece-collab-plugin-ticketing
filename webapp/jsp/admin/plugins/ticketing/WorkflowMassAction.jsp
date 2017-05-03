<jsp:useBean id="massaction" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ManageTicketsJspBean" />
<% String strContent = massaction.processController ( request , response ); %>

<%= strContent %>
