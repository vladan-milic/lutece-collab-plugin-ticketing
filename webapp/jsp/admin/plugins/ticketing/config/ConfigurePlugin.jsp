<jsp:useBean id="ticketingPluginConfiguration" scope="session" class="fr.paris.lutece.plugins.ticketing.web.config.PluginConfigurationJspBean" />
<% String strContent = ticketingPluginConfiguration.processController ( request , response ); %>

<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../../AdminFooter.jsp" %>
