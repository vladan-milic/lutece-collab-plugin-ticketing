<jsp:useBean id="ajax" scope="session" class="fr.paris.lutece.plugins.ticketing.web.ajax.AjaxJspBean" />
<% String strContent = ajax.processController ( request , response ); %>

<%= strContent %>
