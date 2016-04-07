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
package fr.paris.lutece.plugins.ticketing.web;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.EntryFilter;
import fr.paris.lutece.plugins.genericattributes.business.EntryHome;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.ticketing.business.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.TicketForm;
import fr.paris.lutece.plugins.ticketing.business.TicketFormHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.plugins.ticketing.web.workflow.WorkflowCapableXPage;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * This class provides the user interface to manage Ticket xpages ( manage, create, modify, remove )
 */
@Controller( xpageName = "ticket", pageTitleI18nKey = "ticketing.xpage.ticket.pageTitle", pagePathI18nKey = "ticketing.xpage.ticket.pagePathLabel" )
public class TicketXPage extends WorkflowCapableXPage
{
    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_CREATE_TICKET = "/skin/plugins/ticketing/create_ticket.html";
    private static final String TEMPLATE_TICKET_FORM = "/skin/plugins/ticketing/ticket_form.html";
    private static final String TEMPLATE_RECAP_TICKET = "/skin/plugins/ticketing/recap_ticket.html";
    private static final String TEMPLATE_CONFIRM_TICKET = "/skin/plugins/ticketing/confirm_ticket.html";
    private static final String TEMPLATE_MESSAGE_CONFIRM = "/skin/plugins/ticketing/message_confirm_ticket.html";

    // Marks
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_FORM = "ticket_form";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_CONTACT_MODES_LIST = "contact_modes_list";
    private static final String MARK_TICKET_ACTION = "ticket_action";
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_USERTITLE = "userTitle";
    private static final String MARK_FIRSTNAME = "firstName";
    private static final String MARK_LASTNAME = "lastName";
    private static final String MARK_EMAIL = "email";
    private static final String MARK_FIX_PHONE_NUMBER = "fixedPhoneNumber";
    private static final String MARK_MOBILE_PHONE_NUMBER = "mobilePhoneNumber";
    private static final String MARK_RESPONSE_RECAP_LIST = "response_recap_list";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";
    private static final String PARAMETER_RESET_RESPONSE = "reset_response";
    private static final String PARAMETER_DISPLAY_FRONT = "display_front";

    // Views
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_RECAP_TICKET = "recapTicket";
    private static final String VIEW_CONFIRM_TICKET = "confirmTicket";
    private static final String VIEW_REDIRECT_AFTER_CREATE_ACTION = "redirectAfterCreateAction";
    private static final String VIEW_TICKET_FORM = "ticketForm";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";
    private static final String ACTION_RECAP_TICKET = "recapTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";

    // Errors
    private static final String ERROR_PHONE_NUMBER_MISSING = "ticketing.error.phonenumber.missing";
    private static final String ERROR_INCONSISTENT_CONTACT_MODE_WITH_PHONE_NUMBER_FILLED = "ticketing.error.contactmode.inconsistent";
    private static final String ERROR_TICKET_CREATION_ABORTED = "ticketing.error.ticket.creation.aborted.frontoffice";

    // Session keys
    private static final String SESSION_ACTION_TYPE = "ticketing.session.actionType";

    // Session variable to store working values
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );

    /**
     * Returns the form to create a ticket
     *
     * @param request The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_CREATE_TICKET, defaultView = true )
    public XPage getCreateTicket( HttpServletRequest request )
    {
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        if ( ticket == null )
        {
            ticket = new Ticket(  );
            TicketAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );
        }

        prefillTicketWithUserInfo( request, ticket );

        Map<String, Object> model = getModel(  );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale(  ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList(  ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList(  ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );

        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList(  ) );

        saveActionTypeInSession( request.getSession(  ), ACTION_CREATE_TICKET );

        return getXPage( TEMPLATE_CREATE_TICKET, request.getLocale(  ), model );
    }

    /**
     * Prefill User's informations
     * @param request The HTTP request
     * @param ticket The ticket
     */
    private void prefillTicketWithUserInfo( HttpServletRequest request, Ticket ticket )
    {
        LuteceUser user = SecurityService.getInstance(  ).getRegisteredUser( request );

        if ( user != null )
        {
            ticket.setGuid( user.getName(  ) );

            String strFirstname = user.getUserInfo( LuteceUser.NAME_GIVEN );
            String strLastname = user.getUserInfo( LuteceUser.NAME_FAMILY );
            String strEmail = user.getEmail(  );

            if ( !StringUtils.isEmpty( strFirstname ) && StringUtils.isEmpty( ticket.getFirstname(  ) ) )
            {
                ticket.setFirstname( strFirstname );
            }

            if ( !StringUtils.isEmpty( strLastname ) && StringUtils.isEmpty( ticket.getLastname(  ) ) )
            {
                ticket.setLastname( strLastname );
            }

            if ( !StringUtils.isEmpty( strEmail ) && StringUtils.isEmpty( ticket.getEmail(  ) ) )
            {
                ticket.setEmail( strEmail );
            }
        }
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public XPage doCreateTicket( HttpServletRequest request )
    {
        try
        {
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
            ticket.setIdChannel( TicketingConstants.WEB_ID_CHANNEL );
            TicketHome.create( ticket );
    
            doProcessNextWorkflowAction( ticket, request );
    
            if ( ( ticket.getListResponse(  ) != null ) && !ticket.getListResponse(  ).isEmpty(  ) )
            {
                for ( Response response : ticket.getListResponse(  ) )
                {
                    ResponseHome.create( response );
                    TicketHome.insertTicketResponse( ticket.getId(  ), response.getIdResponse(  ) );
                }
            }
    
            TicketAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );
    
            addInfo( INFO_TICKET_CREATED, getLocale( request ) );
        }
        catch ( Exception e )
        {
            addError( ERROR_TICKET_CREATION_ABORTED, request.getLocale(  ) );
            AppLogService.error( e );
            return redirectView( request, VIEW_CREATE_TICKET );
         }

        return redirectView( request, VIEW_CONFIRM_TICKET );
    }

    /**
     * Returns the form to recapitulate a ticket
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_RECAP_TICKET )
    public XPage getRecapTicket( HttpServletRequest request )
    {
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        List<ResponseRecap> listResponseRecap = _ticketFormService.getListResponseRecap( ticket.getListResponse(  ) );

        Map<String, Object> model = getModel(  );
        model.put( MARK_TICKET_ACTION, getActionTypeFromSession( request.getSession(  ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_RESPONSE_RECAP_LIST, listResponseRecap );

        removeActionTypeFromSession( request.getSession(  ) );

        return getXPage( TEMPLATE_RECAP_TICKET, request.getLocale(  ), model );
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_RECAP_TICKET )
    public XPage doRecapTicket( HttpServletRequest request )
    {
        boolean bIsFormValid = true;
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );
        ticket = ( ticket != null ) ? ticket : new Ticket(  );
        populate( ticket, request );
        ticket.setListResponse( new ArrayList<Response>(  ) );

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>(  );

        if ( ticket.getIdTicketCategory(  ) > 0 )
        {
            EntryFilter filter = new EntryFilter(  );
            TicketForm form = TicketFormHome.findByCategoryId( ticket.getIdTicketCategory(  ) );

            if ( form != null )
            {
                filter.setIdResource( TicketFormHome.findByCategoryId( ticket.getIdTicketCategory(  ) ).getIdForm(  ) );
                filter.setResourceType( TicketForm.RESOURCE_TYPE );
                filter.setEntryParentNull( EntryFilter.FILTER_TRUE );
                filter.setFieldDependNull( EntryFilter.FILTER_TRUE );
                filter.setIdIsComment( EntryFilter.FILTER_FALSE );

                List<Entry> listEntryFirstLevel = EntryHome.getEntryList( filter );

                for ( Entry entry : listEntryFirstLevel )
                {
                    listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry(  ),
                            getLocale( request ), ticket ) );
                }
            }
        }

        // Check constraints
        bIsFormValid = validateBean( ticket );

        if ( ticket.hasNoPhoneNumberFilled(  ) )
        {
            addError( ERROR_PHONE_NUMBER_MISSING, getLocale( request ) );
            bIsFormValid = false;
        }

        if ( ticket.isInconsistentContactModeWithPhoneNumberFilled(  ) )
        {
            addError( ERROR_INCONSISTENT_CONTACT_MODE_WITH_PHONE_NUMBER_FILLED, getLocale( request ) );
            bIsFormValid = false;
        }

        if ( listFormErrors.size(  ) > 0 )
        {
            bIsFormValid = false;
        }

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
        ticket.setTicketCategory( ticketCategory.getLabel(  ) );
        ticket.setTicketDomain( ticketDomain.getLabel(  ) );

        ticket.setTicketType( TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType(  ) ).getLabel(  ) );
        ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) ).getLabel(  ) );
        ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle(  ) ).getLabel(  ) );
        ticket.setConfirmationMsg( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) ).getConfirmationMsg(  ) );

        _ticketFormService.saveTicketInSession( request.getSession(  ), ticket );

        if ( !bIsFormValid && getActionTypeFromSession( request.getSession(  ) ).equals( ACTION_CREATE_TICKET ) )
        {
            return redirectView( request, VIEW_CREATE_TICKET );
        }
        else
        {
            return redirectView( request, VIEW_RECAP_TICKET );
        }
    }

    /**
     * Returns the form to confirm a ticket
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_CONFIRM_TICKET )
    public XPage getConfirmTicket( HttpServletRequest request )
    {
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        Map<String, Object> model = getModel(  );
        String strContent = fillTemplate( request, ticket );
        model.put( MARK_MESSAGE, strContent );
        model.put( TicketingConstants.MARK_TICKET, ticket );

        _ticketFormService.removeTicketFromSession( request.getSession(  ) );
        removeActionTypeFromSession( request.getSession(  ) );

        return getXPage( TEMPLATE_CONFIRM_TICKET, request.getLocale(  ), model );
    }

    /**
     * Computes redirection for creation action
     *
     * @param request
     *            The Http request
     * @return the correct XPage
     */
    @View( value = VIEW_REDIRECT_AFTER_CREATE_ACTION )
    public XPage redirectAfterCreateAction( HttpServletRequest request )
    {
        String strRedirectUrl = TicketUtils.getParameter( request, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        return redirectView( request, VIEW_CREATE_TICKET );
    }

    /**
     * Returns the template with the confirmation message with freemarker labels
     * filled
     *
     * @param request
     *            The Http request
     * @param ticket
     *            the ticket
     * @return the template with confirmation message
     */
    private String fillTemplate( HttpServletRequest request, Ticket ticket )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_MESSAGE, ticket.getConfirmationMsg(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MESSAGE_CONFIRM, request.getLocale(  ), model );

        model.put( MARK_USERTITLE, ticket.getUserTitle(  ) );
        model.put( MARK_FIRSTNAME, ticket.getFirstname(  ) );
        model.put( MARK_LASTNAME, ticket.getLastname(  ) );
        model.put( MARK_EMAIL, ticket.getEmail(  ) );
        model.put( MARK_FIX_PHONE_NUMBER, ticket.getFixedPhoneNumber(  ) );
        model.put( MARK_MOBILE_PHONE_NUMBER, ticket.getMobilePhoneNumber(  ) );

        String strContent = AppTemplateService.getTemplateFromStringFtl( template.getHtml(  ), request.getLocale(  ),
                model ).getHtml(  );

        return strContent;
    }

    /**
     * Display ticket form linked to the category used for AJAX request ie.
     * standalone mode (no header/footer lutece)
     *
     * @param request
     *            http request, id_ticket_category must be set
     * @return form to be displayed
     */
    @View( VIEW_TICKET_FORM )
    public XPage getTicketForm( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        String strResetResponse = request.getParameter( PARAMETER_RESET_RESPONSE );
        String strDisplayFront = request.getParameter( PARAMETER_DISPLAY_FRONT );
        boolean bDisplayFront = false;
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession(  ) );

        if ( StringUtils.isNotEmpty( strResetResponse ) &&
                strResetResponse.equalsIgnoreCase( Boolean.TRUE.toString(  ) ) )
        {
            TicketAsynchronousUploadHandler.getHandler(  ).removeSessionFiles( request.getSession(  ).getId(  ) );

            if ( ticket != null )
            {
                ticket.setListResponse( new ArrayList<Response>(  ) );
            }
        }

        if ( StringUtils.isNotEmpty( strDisplayFront ) &&
                strDisplayFront.equalsIgnoreCase( Boolean.TRUE.toString(  ) ) )
        {
            bDisplayFront = true;
        }

        Map<String, Object> model = getModel(  );

        if ( !StringUtils.isEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );
            TicketForm form = TicketFormHome.findByCategoryId( nIdCategory );

            if ( form != null )
            {
                model.put( MARK_TICKET_FORM,
                    _ticketFormService.getHtmlForm( ticket, form, request.getLocale(  ), bDisplayFront, request ) );
            }
        }

        XPage page = getXPage( TEMPLATE_TICKET_FORM, request.getLocale(  ), model );
        page.setStandalone( true );

        return page;
    }

    /**
     * Save the current action type in the session of the user
     *
     * @param session
     *            The session
     * @param actionType
     *            The action type to save
     */
    private void saveActionTypeInSession( HttpSession session, String actionType )
    {
        session.setAttribute( SESSION_ACTION_TYPE, actionType );
    }

    /**
     * Get the current actionType from the session
     *
     * @param session
     *            The session of the user
     * @return The actionType
     */
    private String getActionTypeFromSession( HttpSession session )
    {
        return (String) session.getAttribute( SESSION_ACTION_TYPE );
    }

    /**
     * Remove any action type stored in the session of the user
     *
     * @param session
     *            The session
     */
    private void removeActionTypeFromSession( HttpSession session )
    {
        session.removeAttribute( SESSION_ACTION_TYPE );
    }

    @Override
    protected XPage redirectAfterWorkflowAction( HttpServletRequest request )
    {
        return redirectView( request, VIEW_CREATE_TICKET );
    }

    @Override
    protected XPage redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        return redirectView( request, VIEW_CREATE_TICKET );
    }

    @Override
    protected XPage defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        return redirectView( request, VIEW_CREATE_TICKET );
    }
}
