/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.ticketing.service;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.FieldHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.AbstractEntryTypeUpload;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticketform.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.business.ticketform.TicketForm;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * Service for ticketing forms
 */
public class TicketFormService implements Serializable
{
    /**
     * Name of the bean of the service
     */
    public static final String BEAN_NAME = "ticketing.ticketFormService";

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 1L;
    private static final String PARAMETER_ID_FORM = "id_form";
    private static final String PREFIX_ATTRIBUTE = "attribute";
    private static final String EMPTY_STRING = "";

    // marks
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_FIELD = "field";
    private static final String MARK_STR_LIST_CHILDREN = "str_list_entry_children";
    private static final String MARK_FORM = "form";
    private static final String MARK_FORM_ERRORS = "form_errors";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_USER = "user";
    private static final String MARK_LIST_RESPONSES = "list_responses";
    private static final String MARK_TICKET = "ticket";
    private static final String MARK_UPLOAD_HANDLER = "uploadHandler";
    private static final String MARK_LIST_ERRORS = "listAllErrors";

    // Templates
    private static final String TEMPLATE_DIV_CONDITIONAL_ENTRY = "skin/plugins/ticketing/html_code_div_conditional_entry.html";

    /**
     * Get an Entry Filter
     *
     * @param idForm
     *            the id form
     * @return List a filter Entry
     */
    private static List<Entry> getFilter( int idForm )
    {
        EntryFilter filter = new EntryFilter(  );
        filter.setIdResource( idForm );
        filter.setResourceType( TicketForm.RESOURCE_TYPE );
        filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
        filter.setFieldDependNull( EntryFilter.FILTER_TRUE );

        List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

        return listEntryFirstLevel;
    }

    /**
     * Return the HTML code of the form
     * @param ticket the ticket
     * @param form The form messages associated with the form
     * @param locale the locale
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     * @return the HTML code of the form
     */
    public String getHtmlForm( Ticket ticket, TicketForm form, Locale locale, boolean bDisplayFront,
        HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        StringBuffer strBuffer = new StringBuffer(  );

        List<Entry> listEntryFirstLevel = getFilter( form.getIdForm(  ) );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( entry.getIdEntry(  ), strBuffer, locale, bDisplayFront, request );
        }

        model.put( MARK_FORM, form );
        model.put( MARK_STR_ENTRY, strBuffer.toString(  ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_TICKET, ticket );

        List<GenericAttributeError> listErrors = (List<GenericAttributeError>) request.getSession(  )
                                                                                      .getAttribute( TicketingConstants.SESSION_TICKET_FORM_ERRORS );

        model.put( MARK_FORM_ERRORS, listErrors );
        model.put( MARK_LIST_ERRORS, getAllErrors( request ) );

        // HtmlTemplate template = AppTemplateService.getTemplate( bDisplayFront
        // ? TEMPLATE_HTML_CODE_FORM
        // : TEMPLATE_HTML_CODE_FORM_ADMIN, locale,
        // model );

        // return template.getHtml( );
        return strBuffer.toString(  );
    }

    /**
     * Return the HTML code of the form for the specified list of entries
     * @param listEntryFirstLevel the list of entries
     * @param locale the locale
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     * @return the HTML code of the form
     */
    public String getHtmlForm( List<Entry> listEntryFirstLevel, Locale locale, boolean bDisplayFront,
        HttpServletRequest request )
    {
        StringBuffer strBuffer = new StringBuffer(  );

        for ( Entry entry : listEntryFirstLevel )
        {
            getHtmlEntry( entry.getIdEntry(  ), strBuffer, locale, bDisplayFront, request );
        }

        return strBuffer.toString(  );
    }

    /**
     * returns all errors
     * @param request request
     * @return List of errors
     */
    private List<String> getAllErrors( HttpServletRequest request )
    {
        List<String> listAllErrors = new ArrayList<String>(  );

        // TODO
        return listAllErrors;
    }

    /**
     * Insert in the string buffer the content of the HTML code of the entry
     * @param nIdEntry the key of the entry which HTML code must be insert in
     *            the stringBuffer
     * @param stringBuffer the buffer which contains the HTML code
     * @param locale the locale
     * @param bDisplayFront True if the entry will be displayed in Front Office,
     *            false if it will be displayed in Back Office.
     * @param request HttpServletRequest
     */
    public void getHtmlEntry( int nIdEntry, StringBuffer stringBuffer, Locale locale, boolean bDisplayFront,
        HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        StringBuffer strConditionalQuestionStringBuffer = null;
        HtmlTemplate template;
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            StringBuffer strGroupStringBuffer = new StringBuffer(  );

            for ( Entry entryChild : entry.getChildren(  ) )
            {
                getHtmlEntry( entryChild.getIdEntry(  ), strGroupStringBuffer, locale, bDisplayFront, request );
            }

            model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString(  ) );
        }
        else
        {
            if ( entry.getNumberConditionalQuestion(  ) != 0 )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    field.setConditionalQuestions( FieldHome.findByPrimaryKey( field.getIdField(  ) )
                                                            .getConditionalQuestions(  ) );
                }
            }
        }

        if ( entry.getNumberConditionalQuestion(  ) != 0 )
        {
            strConditionalQuestionStringBuffer = new StringBuffer(  );

            for ( Field field : entry.getFields(  ) )
            {
                if ( field.getConditionalQuestions(  ).size(  ) != 0 )
                {
                    StringBuffer strGroupStringBuffer = new StringBuffer(  );

                    for ( Entry entryConditional : field.getConditionalQuestions(  ) )
                    {
                        getHtmlEntry( entryConditional.getIdEntry(  ), strGroupStringBuffer, locale, bDisplayFront,
                            request );
                    }

                    model.put( MARK_STR_LIST_CHILDREN, strGroupStringBuffer.toString(  ) );
                    model.put( MARK_FIELD, field );
                    template = AppTemplateService.getTemplate( TEMPLATE_DIV_CONDITIONAL_ENTRY, locale, model );
                    strConditionalQuestionStringBuffer.append( template.getHtml(  ) );
                }
            }

            model.put( MARK_STR_LIST_CHILDREN, strConditionalQuestionStringBuffer.toString(  ) );
        }

        model.put( MARK_ENTRY, entry );
        model.put( MARK_LOCALE, locale );

        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( ( user == null ) && SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isExternalAuthentication(  ) )
        {
            try
            {
                user = SecurityService.getInstance(  ).getRemoteUser( request );
            }
            catch ( UserNotSignedException e )
            {
                // Nothing to do : lutece user is not mandatory
            }
        }

        model.put( MARK_USER, user );

        if ( request != null )
        {
            Ticket ticket = getTicketFromSession( request.getSession(  ) );

            if ( ticket != null )
            {
                model.put( MARK_LIST_RESPONSES, ticket.getListResponse(  ) );
            }
        }

        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( entry );

        // If the entry type is a file, we add the 
        if ( entryTypeService instanceof AbstractEntryTypeUpload )
        {
            model.put( MARK_UPLOAD_HANDLER,
                ( (AbstractEntryTypeUpload) entryTypeService ).getAsynchronousUploadHandler(  ) );
        }

        template = AppTemplateService.getTemplate( entryTypeService.getTemplateHtmlForm( entry, bDisplayFront ),
                locale, model );
        stringBuffer.append( template.getHtml(  ) );
    }

    /**
     * Get the responses associated with an entry.<br />
     * Return null if there is no error in the response, or return the list of
     * errors Response created are stored the map of {@link Ticket}. The key of
     * the map is this id of the entry, and the value the list of responses
     *
     * @param request
     *            the request
     * @param nIdEntry
     *            the key of the entry
     * @param locale
     *            the locale
     * @param ticket
     *            The ticket
     * @return null if there is no error in the response or the list of errors
     *         found
     */
    public List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry, Locale locale,
        Ticket ticket )
    {
        if ( ticket.getListResponse(  ) == null )
        {
            List<Response> listResponse = new ArrayList<Response>(  );
            ticket.setListResponse( listResponse );
        }

        return getResponseEntry( request, nIdEntry, ticket.getListResponse(  ), false, locale, ticket );
    }

    /**
     * Get the responses associated with an entry.<br />
     * Return null if there is no error in the response, or return the list of
     * errors
     * @param request the request
     * @param nIdEntry the key of the entry
     * @param listResponse The list of response to add responses found in
     * @param bResponseNull true if the response created must be null
     * @param locale the locale
     * @param ticket The ticket
     * @return null if there is no error in the response or the list of errors
     *         found
     */
    private List<GenericAttributeError> getResponseEntry( HttpServletRequest request, int nIdEntry,
        List<Response> listResponse, boolean bResponseNull, Locale locale, Ticket ticket )
    {
        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );
        Entry entry = EntryHome.findByPrimaryKey( nIdEntry );

        List<Field> listField = new ArrayList<Field>(  );

        for ( Field field : entry.getFields(  ) )
        {
            field = FieldHome.findByPrimaryKey( field.getIdField(  ) );
            listField.add( field );
        }

        entry.setFields( listField );

        if ( entry.getEntryType(  ).getGroup(  ) )
        {
            for ( Entry entryChild : entry.getChildren(  ) )
            {
                List<Response> listResponseChild = new ArrayList<Response>(  );
                ticket.getListResponse(  ).addAll( listResponseChild );

                listFormErrors.addAll( getResponseEntry( request, entryChild.getIdEntry(  ), listResponseChild, false,
                        locale, ticket ) );
            }
        }
        else if ( !entry.getEntryType(  ).getComment(  ) )
        {
            GenericAttributeError formError = null;

            if ( !bResponseNull )
            {
                formError = EntryTypeServiceManager.getEntryTypeService( entry )
                                                   .getResponseData( entry, request, listResponse, locale );

                if ( formError != null )
                {
                    formError.setUrl( getEntryUrl( entry, ticket.getIdTicketCategory(  ) ) );
                }
            }
            else
            {
                Response response = new Response(  );
                response.setEntry( entry );
                listResponse.add( response );
            }

            if ( formError != null )
            {
                entry.setError( formError );
                listFormErrors.add( formError );
            }

            if ( entry.getNumberConditionalQuestion(  ) != 0 )
            {
                for ( Field field : entry.getFields(  ) )
                {
                    boolean bIsFieldInResponseList = isFieldInTheResponseList( field.getIdField(  ), listResponse );

                    for ( Entry conditionalEntry : field.getConditionalQuestions(  ) )
                    {
                        List<Response> listResponseChild = new ArrayList<Response>(  );
                        listFormErrors.addAll( getResponseEntry( request, conditionalEntry.getIdEntry(  ),
                                listResponseChild, !bIsFieldInResponseList, locale, ticket ) );
                    }
                }
            }
        }

        return listFormErrors;
    }

    /**
     * Check if a field is in a response list
     * @param nIdField the id of the field to search
     * @param listResponse the list of responses
     * @return true if the field is in the response list, false otherwise
     */
    public Boolean isFieldInTheResponseList( int nIdField, List<Response> listResponse )
    {
        for ( Response response : listResponse )
        {
            if ( ( response.getField(  ) != null ) && ( response.getField(  ).getIdField(  ) == nIdField ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the URL of the anchor of an entry
     * @param entry the entry
     * @param nIdform id of form
     * @return The URL of the anchor of an entry
     */
    public String getEntryUrl( Entry entry, int nIdform )
    {
        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( XPageAppService.PARAM_XPAGE_APP, TicketingPlugin.PLUGIN_NAME );

        if ( ( entry != null ) && ( entry.getIdResource(  ) > 0 ) )
        {
            url.addParameter( PARAMETER_ID_FORM, entry.getIdResource(  ) );
            url.setAnchor( PREFIX_ATTRIBUTE + entry.getIdEntry(  ) );
        }

        return url.getUrl(  );
    }

    /**
     * Get list of response from the list of generic attributs response
     * listResponse of the current ticket for the recap_ticket view
     *
     * @param listResponse
     *            The listReponse of the ticket
     * @return list of ResponseRecap.
     */
    public List<ResponseRecap> getListResponseRecap( List<Response> listResponse )
    {
        Map<Integer, ResponseRecap> mapResponseRecap = new TreeMap<Integer, ResponseRecap>(  );

        if ( listResponse != null )
        {
            for ( Response response : listResponse )
            {
                ResponseRecap responseRecap = mapResponseRecap.get( Integer.valueOf( response.getEntry(  ).getIdEntry(  ) ) );

                if ( responseRecap == null )
                {
                    responseRecap = new ResponseRecap(  );
                    responseRecap.setTitle( response.getEntry(  ).getTitle(  ) );
                }

                if ( response.getField(  ) != null )
                {
                    responseRecap.addValue( response.getField(  ).getTitle(  ) );
                }
                else
                {
                    if ( response.getFile(  ) != null )
                    {
                        responseRecap.addValue( response.getFile(  ).getTitle(  ) );
                    }
                    else
                    {
                        if ( response.getResponseValue(  ) != null )
                        {
                            responseRecap.addValue( response.getResponseValue(  ) );
                        }
                        else
                        {
                            responseRecap.addValue( EMPTY_STRING );
                        }
                    }
                }

                mapResponseRecap.put( Integer.valueOf( response.getEntry(  ).getIdEntry(  ) ), responseRecap );
            }
        }

        List<ResponseRecap> listResponseRecap = new ArrayList<ResponseRecap>( mapResponseRecap.values(  ) );

        return listResponseRecap;
    }

    /**
     * Save an ticketing in the session of the user
     *
     * @param session
     *            The session
     * @param ticket
     *            The ticketing to save
     */
    public void saveTicketInSession( HttpSession session, Ticket ticket )
    {
        session.setAttribute( TicketingConstants.SESSION_NOT_VALIDATED_TICKET, ticket );
    }

    /**
     * Get the current ticketing form from the session
     * @param session The session of the user
     * @return The ticketing form
     */
    public Ticket getTicketFromSession( HttpSession session )
    {
        return (Ticket) session.getAttribute( TicketingConstants.SESSION_NOT_VALIDATED_TICKET );
    }

    /**
     * Remove any ticketing form responses stored in the session of the user
     * @param session The session
     */
    public void removeTicketFromSession( HttpSession session )
    {
        session.removeAttribute( TicketingConstants.SESSION_NOT_VALIDATED_TICKET );
    }

    /**
     * Save a validated ticketing into the session of the user
     * @param session The session
     * @param ticketing The ticketing to save
     */
    public void saveValidatedTicketForm( HttpSession session, Ticket ticketing )
    {
        removeTicketFromSession( session );
        session.setAttribute( TicketingConstants.SESSION_VALIDATED_TICKET_FORM, ticketing );
    }

    /**
     * Get a validated ticketing from the session
     * @param session The session of the user
     * @return The ticketing
     */
    public Ticket getValidatedTicketFromSession( HttpSession session )
    {
        return (Ticket) session.getAttribute( TicketingConstants.SESSION_VALIDATED_TICKET_FORM );
    }

    /**
     * Remove a validated ticketing stored in the session of the user
     * @param session The session
     */
    public void removeValidatedTicketFromSession( HttpSession session )
    {
        session.removeAttribute( TicketingConstants.SESSION_VALIDATED_TICKET_FORM );
    }
}
