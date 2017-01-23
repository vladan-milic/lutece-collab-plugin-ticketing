<jsp:useBean id="personalDataAppBean" scope="request" class="fr.paris.lutece.plugins.ticketing.web.PersonalDataApp" />

<%= personalDataAppBean.getDeltaPersonalData( request ) %>