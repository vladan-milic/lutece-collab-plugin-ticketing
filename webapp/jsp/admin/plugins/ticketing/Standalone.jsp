<jsp:useBean id="standalone" scope="session" class="fr.paris.lutece.plugins.ticketing.web.StandaloneJspBean" />
<% String strContent = standalone.processController ( request , response ); %>

<%= strContent %>
