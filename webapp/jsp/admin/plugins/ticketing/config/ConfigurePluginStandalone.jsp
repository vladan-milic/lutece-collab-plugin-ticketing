<jsp:useBean id="ticketingPluginConfiguration" scope="session" class="fr.paris.lutece.plugins.ticketing.web.config.PluginConfigurationJspBean" />

<%= ticketingPluginConfiguration.processController ( request , response ) %>