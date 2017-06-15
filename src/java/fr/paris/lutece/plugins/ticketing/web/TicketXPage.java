/*
 * Copyright (c) 2002-2016, Mairie de Paris
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityNotFoundException;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.util.FormValidator;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidatorFactory;
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * This class provides the user interface to manage Ticket xpages ( manage, create, modify, remove )
 */
@Controller( xpageName = "ticket", pageTitleI18nKey = "ticketing.xpage.ticket.pageTitle", pagePathI18nKey = "ticketing.xpage.ticket.pagePathLabel" )
public class TicketXPage extends WorkflowCapableXPage
{
    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_CREATE_TICKET = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "create_ticket.html";
    private static final String TEMPLATE_TICKET_FORM = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "ticket_form.html";
    private static final String TEMPLATE_RECAP_TICKET = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "recap_ticket.html";
    private static final String TEMPLATE_CONFIRM_TICKET = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "confirm_ticket.html";
    private static final String TEMPLATE_MESSAGE_CONFIRM = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "message_confirm_ticket.html";

    // Marks
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_FORM = "ticket_form";
    private static final String MARK_TICKET_TYPES_LIST = "ticket_types_list";
    private static final String MARK_TICKET_DOMAINS_LIST = "ticket_domains_list";
    private static final String MARK_TICKET_CATEGORIES_LIST = "ticket_categories_list";
    private static final String MARK_TICKET_PRECISIONS_LIST = "ticket_precisions_list";
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
    private static final String ERROR_TICKET_CREATION_ABORTED = "ticketing.error.ticket.creation.aborted.frontoffice";
    private static final String MESSAGE_ERROR_COMMENT_VALIDATION = "ticketing.validation.ticket.TicketComment.size";

    // Session keys
    private static final String SESSION_ACTION_TYPE = "ticketing.session.actionType";

    // Session variable to store working values
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );

    // Other constants
    private static final String LUTECE_USER_INFO_CUSTOMER_ID = "customer_id";

    /**
     * Returns the form to create a ticket
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_CREATE_TICKET, defaultView = true )
    public XPage getCreateTicket( HttpServletRequest request )
    {
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );

        if ( ticket == null )
        {
            ticket = new Ticket( );
            TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
            ticket.setTicketCategory( new TicketCategory( ) );
        }

        prefillTicketWithUserInfo( request, ticket );

        _ticketFormService.saveTicketInSession( request.getSession( ), ticket );

        Map<String, Object> model = getModel( );
        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale( ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_TICKET_TYPES_LIST, TicketTypeHome.getReferenceList( ) );
        model.put( MARK_TICKET_DOMAINS_LIST, TicketDomainHome.getReferenceList( ) );
        model.put( MARK_TICKET_CATEGORIES_LIST, TicketCategoryHome.getReferenceListByDomain( 1 ) );
        model.put( MARK_TICKET_PRECISIONS_LIST, new ReferenceList( ) );

        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList( request.getLocale( ) ) );

        saveActionTypeInSession( request.getSession( ), ACTION_CREATE_TICKET );

        return getXPage( TEMPLATE_CREATE_TICKET, request.getLocale( ), model );
    }

    /**
     * Prefill User's informations
     * 
     * @param request
     *            The HTTP request
     * @param ticket
     *            The ticket
     */
    private void prefillTicketWithUserInfo( HttpServletRequest request, Ticket ticket )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        ticket.setCustomerId( StringUtils.EMPTY );

        if ( user != null )
        {
            try
            {
                ticket.setGuid( user.getName( ) );

                String strCustomerId = user.getUserInfo( LUTECE_USER_INFO_CUSTOMER_ID );
                String strIdUserTitle = user.getUserInfo( LuteceUser.GENDER );
                String strFirstname = user.getUserInfo( LuteceUser.NAME_GIVEN );
                String strLastname = user.getUserInfo( LuteceUser.NAME_FAMILY );
                String strEmail = user.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
                String strFixedPhoneNumber = user.getUserInfo( LuteceUser.HOME_INFO_TELECOM_TELEPHONE_NUMBER );
                String strMobilePhoneNumber = user.getUserInfo( LuteceUser.HOME_INFO_TELECOM_MOBILE_NUMBER );

                ticket.setCustomerId( strCustomerId );

                if ( !StringUtils.isEmpty( strIdUserTitle ) && StringUtils.isEmpty( ticket.getUserTitle( ) ) )
                {
                    try
                    {
                        UserTitle userTitle = UserTitleHome.findByPrimaryKey( Integer.valueOf( strIdUserTitle ) );

                        if ( userTitle != null )
                        {
                            ticket.setIdUserTitle( userTitle.getId( ) );
                            ticket.setUserTitle( userTitle.getLabel( ) );
                        }
                    }
                    catch( NumberFormatException e )
                    {
                        // The ticket keep the default value 0 for the user title id (undefined)
                    }
                }

                if ( !StringUtils.isEmpty( strFirstname ) && StringUtils.isEmpty( ticket.getFirstname( ) ) )
                {
                    ticket.setFirstname( strFirstname );
                }

                if ( !StringUtils.isEmpty( strLastname ) && StringUtils.isEmpty( ticket.getLastname( ) ) )
                {
                    ticket.setLastname( strLastname );
                }

                if ( !StringUtils.isEmpty( strEmail ) && StringUtils.isEmpty( ticket.getEmail( ) ) )
                {
                    ticket.setEmail( strEmail );
                }

                if ( !StringUtils.isEmpty( strFixedPhoneNumber ) && StringUtils.isEmpty( ticket.getFixedPhoneNumber( ) ) )
                {
                    ticket.setFixedPhoneNumber( strFixedPhoneNumber );
                }

                if ( !StringUtils.isEmpty( strMobilePhoneNumber ) && StringUtils.isEmpty( ticket.getMobilePhoneNumber( ) ) )
                {
                    ticket.setMobilePhoneNumber( strMobilePhoneNumber );
                }
            }
            catch( IdentityNotFoundException e )
            {
                // The customer is not in the identity store yet : nothing to do
            }
        }
    }

    /**
     * Process the data capture form of a new ticket
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     */
    @Action( ACTION_CREATE_TICKET )
    public XPage doCreateTicket( HttpServletRequest request )
    {
        try
        {
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );

            Channel channelFront = ChannelHome.findByPrimaryKey( Integer.valueOf( PluginConfigurationService.getInt(
                    PluginConfigurationService.PROPERTY_CHANNEL_ID_FRONT, TicketingConstants.PROPERTY_UNSET_INT ) ) );

            ticket.setChannel( channelFront );
            TicketHome.create( ticket );

            if ( ( ticket.getListResponse( ) != null ) && !ticket.getListResponse( ).isEmpty( ) )
            {
                for ( Response response : ticket.getListResponse( ) )
                {
                    ResponseHome.create( response );
                    TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
                }
            }

            doProcessNextWorkflowAction( ticket, request );

            // Immediate indexation of the Ticket
            immediateTicketIndexing( ticket.getId( ), request );

            TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

            addInfo( INFO_TICKET_CREATED, getLocale( request ) );
        }
        catch( Exception e )
        {
            addError( ERROR_TICKET_CREATION_ABORTED, request.getLocale( ) );
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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        List<ResponseRecap> listResponseRecap = _ticketFormService.getListResponseRecap( ticket.getListResponse( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKET_ACTION, getActionTypeFromSession( request.getSession( ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_RESPONSE_RECAP_LIST, listResponseRecap );

        removeActionTypeFromSession( request.getSession( ) );

        return getXPage( TEMPLATE_RECAP_TICKET, request.getLocale( ), model );
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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        ticket = ( ticket != null ) ? ticket : new Ticket( );
        populate( ticket, request );
        ticket.setListResponse( new ArrayList<Response>( ) );

        int nIdCategory = TicketingConstants.PROPERTY_UNSET_INT;
        if ( StringUtils.isNotBlank( request.getParameter( PARAMETER_ID_CATEGORY ) ) )
        {
            nIdCategory = Integer.valueOf( request.getParameter( PARAMETER_ID_CATEGORY ) );
        }

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( nIdCategory );
        if ( ticketCategory == null )
        {
            TicketCategory ticketCategoryEmpty = new TicketCategory( );
            ticketCategoryEmpty.setId( TicketingConstants.PROPERTY_UNSET_INT );
            ticket.setTicketCategory( ticketCategoryEmpty );
        }
        else
        {
            ticket.setTicketCategory( ticketCategory );
        }

        // Check constraints
        // Count the number of characters in the ticket comment
        int iNbCharcount = FormValidator.countCharTicketComment( ticket.getTicketComment( ) );

        bIsFormValid = validateBean( ticket );

        TicketValidator ticketValidator = TicketValidatorFactory.getInstance( ).create( request.getLocale( ) );
        List<String> listValidationErrors = ticketValidator.validate( ticket, false );

        FormValidator formValidator = new FormValidator( request );
        listValidationErrors.add( formValidator.isEmailFilled( ) );
        listValidationErrors.add( formValidator.isPhoneNumberFilled( ) );

        boolean bIsSubProbSelected = true;

        // Validate if precision has been selected if the selected category has precisions
        if ( ticket.getTicketCategory( ).getId( ) != TicketingConstants.PROPERTY_UNSET_INT )
        {
            List<TicketCategory> listTicketCategory = TicketCategoryHome.findByDomainId( ticket.getIdTicketDomain( ) );
            for ( TicketCategory ticketCategoryByDomain : listTicketCategory )
            {
                if ( ticketCategoryByDomain.getLabel( ).equals( ticket.getTicketCategory( ).getLabel( ) )
                        && StringUtils.isNotBlank( ticketCategoryByDomain.getPrecision( ) )
                        && StringUtils.isNotBlank( request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ) )
                        && request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ).equals( TicketingConstants.NO_ID_STRING ) )
                {
                    addError( TicketingConstants.MESSAGE_ERROR_TICKET_CATEGORY_PRECISION_NOT_SELECTED, getLocale( request ) );
                    bIsFormValid = false;
                    bIsSubProbSelected = false;
                    ticket.getTicketCategory( ).setPrecision( TicketingConstants.NO_ID_STRING );
                    break;
                }
            }
        }

        // The validation for the ticket comment size is made here because the validation doesn't work for this field
        if ( iNbCharcount > 5000 )
        {
            addError( MESSAGE_ERROR_COMMENT_VALIDATION, getLocale( request ) );
            bIsFormValid = false;
        }

        for ( String error : listValidationErrors )
        {
            if ( !StringUtils.isEmpty( error ) )
            {
                addError( error );
                bIsFormValid = false;
            }
        }

        // Check if a type/domain/category have been selected (made here to sort errors)
        if ( ticket.getIdTicketType( ) == TicketingConstants.PROPERTY_UNSET_INT )
        {
            addError( TicketingConstants.MESSAGE_ERROR_TICKET_TYPE_NOT_SELECTED, getLocale( request ) );
            bIsFormValid = false;
        }

        if ( ticket.getIdTicketDomain( ) == TicketingConstants.PROPERTY_UNSET_INT )
        {
            addError( TicketingConstants.MESSAGE_ERROR_TICKET_DOMAIN_NOT_SELECTED, getLocale( request ) );
            bIsFormValid = false;
        }

        if ( ticket.getTicketCategory( ) != null && ticket.getTicketCategory( ).getId( ) == TicketingConstants.PROPERTY_UNSET_INT )
        {
            addError( TicketingConstants.MESSAGE_ERROR_TICKET_CATEGORY_NOT_SELECTED, getLocale( request ) );
            bIsFormValid = false;
        }

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );

        request.setAttribute( TicketingConstants.ATTRIBUTE_IS_DISPLAY_FRONT, true );
        if ( ticket.getTicketCategory( ).getId( ) > 0 && bIsSubProbSelected )
        {
            List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), null );

            for ( Entry entry : listEntry )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), getLocale( request ), ticket ) );
            }
        }

        if ( listFormErrors.size( ) > 0 )
        {
            bIsFormValid = false;
        }

        TicketDomain ticketDomain = TicketDomainHome.findByPrimaryKey( ticket.getIdTicketDomain( ) );
        if ( ticketDomain != null )
        {
            ticket.setTicketDomain( ticketDomain.getLabel( ) );

            TicketType ticketType = TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType( ) );
            if ( ticketType != null )
            {
                ticket.setTicketType( ticketType.getLabel( ) );
            }
        }

        ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode( ) ).getCode( ) );
        ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle( ) ).getLabel( ) );
        ticket.setConfirmationMsg( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode( ) ).getConfirmationMsg( ) );

        _ticketFormService.saveTicketInSession( request.getSession( ), ticket );

        if ( !bIsFormValid && getActionTypeFromSession( request.getSession( ) ).equals( ACTION_CREATE_TICKET ) )
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
        Map<String, Object> model = getModel( );
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        String strContent = (String) request.getSession( ).getAttribute( TicketingConstants.SESSION_TICKET_CONFIRM_MESSAGE );

        if ( ticket != null )
        {
            strContent = fillTemplate( request, ticket );
            _ticketFormService.removeTicketFromSession( request.getSession( ) );
            removeActionTypeFromSession( request.getSession( ) );
        }
        model.put( MARK_MESSAGE, strContent );
        request.getSession( ).setAttribute( TicketingConstants.SESSION_TICKET_CONFIRM_MESSAGE, strContent );

        return getXPage( TEMPLATE_CONFIRM_TICKET, request.getLocale( ), model );
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
        String strRedirectUrl = RequestUtils.popParameter( request, RequestUtils.SCOPE_SESSION, TicketingConstants.ATTRIBUTE_RETURN_URL );

        if ( StringUtils.isNotEmpty( strRedirectUrl ) )
        {
            return redirect( request, strRedirectUrl );
        }

        return redirectView( request, VIEW_CREATE_TICKET );
    }

    /**
     * Returns the template with the confirmation message with freemarker labels filled
     *
     * @param request
     *            The Http request
     * @param ticket
     *            the ticket
     * @return the template with confirmation message
     */
    private String fillTemplate( HttpServletRequest request, Ticket ticket )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( MARK_MESSAGE, ticket.getConfirmationMsg( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MESSAGE_CONFIRM, request.getLocale( ), model );

        model.put( MARK_USERTITLE, ticket.getUserTitle( ) );
        model.put( MARK_FIRSTNAME, ticket.getFirstname( ) );
        model.put( MARK_LASTNAME, ticket.getLastname( ) );
        model.put( MARK_EMAIL, ticket.getEmail( ) );
        model.put( MARK_FIX_PHONE_NUMBER, ticket.getFixedPhoneNumber( ) );
        model.put( MARK_MOBILE_PHONE_NUMBER, ticket.getMobilePhoneNumber( ) );

        String strContent = AppTemplateService.getTemplateFromStringFtl( template.getHtml( ), request.getLocale( ), model ).getHtml( );

        return strContent;
    }

    /**
     * Display ticket form linked to the category used for AJAX request ie. standalone mode (no header/footer lutece)
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
        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ) );

        if ( StringUtils.isNotEmpty( strResetResponse ) && strResetResponse.equalsIgnoreCase( Boolean.TRUE.toString( ) ) )
        {
            TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

            if ( ticket != null )
            {
                ticket.setListResponse( new ArrayList<Response>( ) );
            }
        }

        Map<String, Object> model = getModel( );

        if ( !StringUtils.isEmpty( strIdCategory ) && StringUtils.isNumeric( strIdCategory ) )
        {
            int nIdCategory = Integer.parseInt( strIdCategory );
            model.put( MARK_TICKET_FORM, _ticketFormService.getHtmlFormInputs( request.getLocale( ), true, nIdCategory, null, request ) );
        }

        XPage page = getXPage( TEMPLATE_TICKET_FORM, request.getLocale( ), model );
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
