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
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.genericattributes.business.GenericAttributeError;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.business.ResponseHome;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityNotFoundException;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactMode;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.form.Form;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntryType;
import fr.paris.lutece.plugins.ticketing.business.form.FormHome;
import fr.paris.lutece.plugins.ticketing.business.formcategory.FormCategory;
import fr.paris.lutece.plugins.ticketing.business.formcategory.FormCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.TicketFormService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.upload.TicketAsynchronousUploadHandler;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.util.FormValidator;
import fr.paris.lutece.plugins.ticketing.web.util.RequestUtils;
import fr.paris.lutece.plugins.ticketing.web.util.ResponseRecap;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketCategoryValidatorResult;
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
import fr.paris.lutece.portal.util.mvc.utils.MVCUtils;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;
import freemarker.template.TemplateModelException;

/**
 * This class provides the user interface to manage Ticket xpages ( manage, create, modify, remove )
 */
@Controller( xpageName = TicketXPage.TICKET_XPAGE_NAME, pageTitleI18nKey = "ticketing.xpage.ticket.pageTitle", pagePathI18nKey = "ticketing.xpage.ticket.pagePathLabel" )
public class TicketXPage extends WorkflowCapableXPage
{
	public static final String TICKET_XPAGE_NAME = "ticket";

    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_TICKET_FORM = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "ticket_form.html";
    private static final String TEMPLATE_RECAP_TICKET = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "recap_ticket.html";
    private static final String TEMPLATE_CONFIRM_TICKET = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "confirm_ticket.html";
    private static final String TEMPLATE_MESSAGE_CONFIRM = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "message_confirm_ticket.html";
    private static final String TEMPLATE_CREATE_TICKET_DYNAMIC_FORM = TicketingConstants.TEMPLATE_FRONT_TICKET_FEATURE_PATH + "create_ticket_dynamic_form.html";

    // Marks
    private static final String MARK_USER_TITLES_LIST = "user_titles_list";
    private static final String MARK_TICKET_FORM = "ticket_form";
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
    private static final String MARK_FORM = "form";
    private static final String MARK_FORMENTRYTYPE = "formEntryType";

    // Parameters
    private static final String PARAMETER_ID_CATEGORY = "id_ticket_category";
    public static final String PARAMETER_ID_FORM = "form";
    private static final String PARAMETER_RESET_RESPONSE = "reset_response";
    private static final String PARAMETER_ID_TICKET = "ticket_id";

    // Views
    private static final String VIEW_CREATE_TICKET = "createTicket";
    private static final String VIEW_RECAP_TICKET = "recapTicket";
    private static final String VIEW_CONFIRM_TICKET = "confirmTicket";
    private static final String VIEW_REDIRECT_AFTER_CREATE_ACTION = "redirectAfterCreateAction";
    private static final String VIEW_TICKET_FORM = "ticketForm";
    private static final String VIEW_CREATE_TICKET_DYNAMIC_FORM = "create";

    // Actions
    private static final String ACTION_CREATE_TICKET = "createTicket";
    private static final String ACTION_RECAP_TICKET = "recapTicket";

    // Infos
    private static final String INFO_TICKET_CREATED = "ticketing.info.ticket.created";

    // Errors
    private static final String ERROR_TICKET_CREATION_ABORTED = "ticketing.error.ticket.creation.aborted.frontoffice";
    private static final String MESSAGE_ERROR_COMMENT_VALIDATION = "ticketing.validation.ticket.TicketComment.size";
    private static final String ERROR_NO_FORM_EXISTS = "ticketing.error.no.form";

    // Session keys
    private static final String SESSION_ACTION_TYPE = "ticketing.session.actionType";

    // Session variable to store working values
    private final TicketFormService _ticketFormService = SpringContextService.getBean( TicketFormService.BEAN_NAME );

    // Other constants
    private static final String LUTECE_USER_INFO_CUSTOMER_ID = "user.id.customer";
    private static final String URL_PORTAL = "Portal.jsp";

    /**
     * Returns the form to create a ticket
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     * @throws TemplateModelException
     */
    @View( value = VIEW_CREATE_TICKET_DYNAMIC_FORM, defaultView = true )
    public XPage getCreateTicketDynamicForm( HttpServletRequest request )
    {
        Map<String, Object> model = getModel( );

        Form form = FormHome.getFormFromRequest( request );
        List<Integer> restrictedCategoriesId = null;

        if ( form == null || form.getId( ) == 0 )
        {
            addError( ERROR_NO_FORM_EXISTS, request.getLocale( ) );
        }
        else
        {
            // Get ticket from session or create one
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ), form );

            if ( ticket == null )
            {
                ticket = new Ticket( );
                TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );

                prefillTicketWithDefaultForm( request, ticket, form );
                prefillTicketWithUserInfo( request, ticket );
            }

            model.put( TicketingConstants.MARK_TICKET, ticket );

            // remove ticket from session
            // -> on refresh, ticket will be reset
            // -> we pass the ticket via the form
            _ticketFormService.removeTicketFromSession( request.getSession( ), form );

            List<FormCategory> restrictedCategories = FormCategoryHome.findByForm( form.getId( ) );
            restrictedCategoriesId = restrictedCategories.stream( ).map( category -> category.getIdCategory( ) ).collect( Collectors.toList( ) );
        }

        model.put( MARK_FORM, form );
        model.put( MARK_FORMENTRYTYPE, new FormEntryType( ) );

        model.put( MARK_USER_TITLES_LIST, UserTitleHome.getReferenceList( request.getLocale( ) ) );
        model.put( MARK_CONTACT_MODES_LIST, ContactModeHome.getReferenceList( request.getLocale( ) ) );

        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_TREE, TicketCategoryService.getInstance( ).getCategoriesTree( restrictedCategoriesId ).getTreeJSONObject( ) );
        model.put( TicketingConstants.MARK_TICKET_CATEGORIES_DEPTHS, TicketCategoryService.getInstance( ).getCategoriesTree( restrictedCategoriesId ).getDepths( ) );

        saveActionTypeInSession( request.getSession( ), ACTION_CREATE_TICKET );


        return getXPage( TEMPLATE_CREATE_TICKET_DYNAMIC_FORM, request.getLocale( ), model );
    }


    /**
     * Returns the form to create a ticket
     *
     * @param request
     *            The Http request
     * @return the html code of the ticket form
     */
    @View( value = VIEW_CREATE_TICKET )
    public XPage getCreateTicket( HttpServletRequest request )
    {
        return getCreateTicketDynamicForm( request );
    }

    /**
     * Prefill ticket with form default values
     * 
     * @param request
     *            the http request
     * @param form
     *            the form
     */
    private void prefillTicketWithDefaultForm( HttpServletRequest request, Ticket ticket, Form form )
    {
        FormEntryType formEntryType = new FormEntryType();

        UserTitle userTitle = null;
        try
        {
            String userTitleId = form.getEntry( formEntryType.getUserTitle( ) ).getDefaultValue( );
            userTitle = UserTitleHome.findByPrimaryKey( Integer.parseInt( userTitleId ) );
        }
        catch ( NumberFormatException e )
        {
            // do nothing, default value can be empty or not valid
        }

        if ( userTitle != null )
        {
            ticket.setIdUserTitle( userTitle.getId( ) );
            ticket.setUserTitle( userTitle.getLabel( ) );
        }

        ticket.setLastname( form.getEntry( formEntryType.getLastName( ) ).getDefaultValue( ) );
        ticket.setFirstname( form.getEntry( formEntryType.getFirstName( ) ).getDefaultValue( ) );
        ticket.setEmail( form.getEntry( formEntryType.getEmail( ) ).getDefaultValue( ) );
        ticket.setFixedPhoneNumber( form.getEntry( formEntryType.getPhoneNumbers( ) ).getDefaultValue( ) );

        ContactMode contactMode = null;
        try
        {
            String contactModeId = form.getEntry( formEntryType.getContactMode( ) ).getDefaultValue( );
            contactMode = ContactModeHome.findByPrimaryKey( Integer.parseInt( contactModeId ) );
        }
        catch ( NumberFormatException e )
        {
            // do nothing, default value can be empty or not valid
        }

        if ( contactMode != null )
        {
            ticket.setIdContactMode( contactMode.getId( ) );
            ticket.setContactMode( contactMode.getCode( ) );
            ticket.setConfirmationMsg( contactMode.getConfirmationMsg( ) );
        }

        for ( TicketCategoryType depth : TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ) )
        {
            TicketCategory category = null;
            try
            {
                String categoryId = form.getEntry( formEntryType.getCategory( ) + depth.getDepthNumber( ) ).getDefaultValue( );
                category = TicketCategoryService.getInstance( ).findCategoryById( Integer.parseInt( categoryId ) );
            }
            catch ( NumberFormatException e )
            {
                // do nothing, default value can be empty or not valid
            }

            if ( category != null )
            {
                ticket.setTicketCategory( category );
            }
        }

        ticket.setTicketComment( form.getEntry( formEntryType.getComment( ) ).getDefaultValue( ) );

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
                String strBirthname = user.getUserInfo( LuteceUser.NAME_MIDDLE );
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

                if ( StringUtils.isEmpty( strLastname ) && !StringUtils.isEmpty( strBirthname ) && StringUtils.isEmpty( ticket.getLastname( ) ) )
                {
                    ticket.setLastname( strBirthname );
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
        Form form = FormHome.getFormFromRequest( request );
        HashMap<String, String> additionalParameters = new HashMap<String, String>( );
        try
        {
            Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ), form );
            
            
            if ( ticket != null )
            {
	            Channel channelFront = ChannelHome.findByPrimaryKey( Integer.valueOf( PluginConfigurationService.getInt(
	                    PluginConfigurationService.PROPERTY_CHANNEL_ID_FRONT, TicketingConstants.PROPERTY_UNSET_INT ) ) );
	
	            ticket.setChannel( channelFront );
	            TicketHome.create( ticket );
	            
	            _ticketFormService.removeTicketFromSession( request.getSession( ), form );
	            
	            if ( ( ticket.getListResponse( ) != null ) && !ticket.getListResponse( ).isEmpty( ) )
	            {
	                for ( Response response : ticket.getListResponse( ) )
	                {
	                    ResponseHome.create( response );
	                    TicketHome.insertTicketResponse( ticket.getId( ), response.getIdResponse( ) );
	                }
	            }
	
	            request.setAttribute( TicketingConstants.ATTRIBUTE_BYPASS_ASSSIGN_TO_ME, true );
	
	            doProcessNextWorkflowAction( ticket, request );
	
	            // Immediate indexation of the Ticket
	            immediateTicketIndexing( ticket.getId( ), request );
	            
	            TicketAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
	
	            addInfo( INFO_TICKET_CREATED, getLocale( request ) );
	            
	            additionalParameters.put( PARAMETER_ID_TICKET, String.valueOf( ticket.getId( ) ));
            }
        }
        catch( Exception e )
        {
            addError( ERROR_TICKET_CREATION_ABORTED, request.getLocale( ) );
            AppLogService.error( e );

            return redirectView( request, VIEW_CREATE_TICKET_DYNAMIC_FORM, form );
        }

		return redirectView( request, VIEW_CONFIRM_TICKET, form, additionalParameters );
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
        Form form = FormHome.getFormFromRequest( request );
        if ( form == null )
        {
            form = new Form( );
        }

        Ticket ticket = _ticketFormService.getTicketFromSession( request.getSession( ), form );
        List<ResponseRecap> listResponseRecap = _ticketFormService.getListResponseRecap( ticket.getListResponse( ) );

        Map<String, Object> model = getModel( );
        model.put( MARK_TICKET_ACTION, getActionTypeFromSession( request.getSession( ) ) );
        model.put( TicketingConstants.MARK_TICKET, ticket );
        model.put( MARK_RESPONSE_RECAP_LIST, listResponseRecap );
        model.put( MARK_FORM, form );
        model.put( MARK_FORMENTRYTYPE, new FormEntryType( ) );

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
        Ticket ticket = new Ticket( );
        populate( ticket, request );
        ticket.setListResponse( new ArrayList<Response>( ) );

        Form form = FormHome.getFormFromRequest( request );

        // Validate the TicketCategory
        TicketCategoryValidatorResult categoryValidatorResult = new TicketCategoryValidator( request, request.getLocale( ) ).validateTicketCategory( form );
        if ( !categoryValidatorResult.isTicketCategoryValid( ) )
        {
            categoryValidatorResult.getListValidationErrors( ).stream( ).forEach( ( error ) -> addError( error ) );
            bIsFormValid = false;
        }

        if ( categoryValidatorResult.getTicketCategory( ) != null )
        {
            ticket.setTicketCategory( categoryValidatorResult.getTicketCategory( ) );
        }

        // Check constraints
        // Count the number of characters in the ticket comment
        int iNbCharcount = FormValidator.countCharTicketComment( ticket.getTicketComment( ) );

        bIsFormValid &= validateBean( ticket );

        TicketValidator ticketValidator = TicketValidatorFactory.getInstance( ).create( request.getLocale( ) );
        List<String> listValidationErrors = ticketValidator.validate( ticket, false );

        listValidationErrors.addAll( ticketValidator.validateDynamicFields( request ) );

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

        List<GenericAttributeError> listFormErrors = new ArrayList<GenericAttributeError>( );

        request.setAttribute( TicketingConstants.ATTRIBUTE_IS_DISPLAY_FRONT, true );
        if ( categoryValidatorResult.isTicketCategoryValid( ) && ticket.getTicketCategory( ) != null )
        {
            ticket.setListResponse( null );

            List<Entry> listEntry = TicketFormService.getFilterInputs( ticket.getTicketCategory( ).getId( ), null );

            for ( Entry entry : listEntry )
            {
                listFormErrors.addAll( _ticketFormService.getResponseEntry( request, entry.getIdEntry( ), getLocale( request ), ticket ) );
            }
        }

        if ( listFormErrors.size( ) > 0 )
        {
            for ( GenericAttributeError error : listFormErrors )
            {
                addError( error.getMessage( ) );
            }

            bIsFormValid = false;
        }

        ticket.setContactMode( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode( ) ).getCode( ) );
        ticket.setUserTitle( UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle( ) ).getLabel( ) );
        ticket.setConfirmationMsg( ContactModeHome.findByPrimaryKey( ticket.getIdContactMode( ) ).getConfirmationMsg( ) );

        _ticketFormService.saveTicketInSession( request.getSession( ), ticket, form );

        if ( !bIsFormValid && ACTION_CREATE_TICKET.equals( getActionTypeFromSession( request.getSession( ) ) ) )
        {
            return redirectView( request, VIEW_CREATE_TICKET_DYNAMIC_FORM, form );
        }
        else
        {
            return redirectView( request, VIEW_RECAP_TICKET, form );
        }
    }


    /**
     * Redirect to requested view
     *
     * @param request
     *            the http request
     * @param strView
     *            the targeted view
     * @return the page requested
     */
    protected XPage redirectView( HttpServletRequest request, String strView, Form form )
    {
        return redirect( request, getViewUrl( strView, form ) );
    }
    
    
    /**
     * Redirect to requested view
     *
     * @param request
     *            the http request
     * @param strView
     *            the targeted view
     * @return the page requested
     */
    protected XPage redirectView( HttpServletRequest request, String strView, Form form, Map<String, String> additionalParameters )
    {
        return redirect( request, getViewUrl( strView, form, additionalParameters ) );
    }
    

    /**
     * Get a View URL
     * 
     * @param strView
     *            The view name
     * @return The URL
     */
    protected String getViewUrl( String strView, Form form, Map<String, String> additionalParameters )
    {
        UrlItem url = new UrlItem( URL_PORTAL );
        url.addParameter( MVCUtils.PARAMETER_PAGE, getXPageName( ) );
        url.addParameter( MVCUtils.PARAMETER_VIEW, strView );
        if ( form != null )
        {
            url.addParameter( PARAMETER_ID_FORM, form.getId( ) );
        }
        
        if ( additionalParameters != null && !additionalParameters.isEmpty() )
        {
        	for ( java.util.Map.Entry<String, String> parameters : additionalParameters.entrySet() )
        	{
        		url.addParameter( parameters.getKey(), parameters.getValue() );
        	}
        }

        return url.getUrl( );
    }
    
    /**
     * Get a View URL
     * 
     * @param strView
     *            The view name
     * @return The URL
     */
    protected String getViewUrl( String strView, Form form )
    {
        return getViewUrl(strView, form, null);
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
        Form form = FormHome.getFormFromRequest( request );
        String strContent = StringUtils.EMPTY;
        String idTicketParam = request.getParameter( PARAMETER_ID_TICKET );
        if ( idTicketParam != null ) 
        {
			int ticketId = Integer.parseInt( idTicketParam );
	        Ticket ticket = TicketHome.findByPrimaryKey( ticketId );
	
	        model.put( TicketingConstants.MARK_TICKET, ticket );
	        strContent = (String) request.getSession( ).getAttribute( TicketingConstants.SESSION_TICKET_CONFIRM_MESSAGE );
	
	        if ( ticket != null )
	        {
	            strContent = fillTemplate( request, ticket );
	            removeActionTypeFromSession( request.getSession( ) );
	        }
	        model.put( MARK_MESSAGE, strContent );
        
        }
        model.put( MARK_FORM, form );
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

        Form form = FormHome.getFormFromRequest( request );
        return redirectView( request, VIEW_CREATE_TICKET_DYNAMIC_FORM, form );
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

        @SuppressWarnings( "deprecation" )
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
        Form form = FormHome.getFormFromRequest( request );
        return redirectView( request, VIEW_CREATE_TICKET_DYNAMIC_FORM, form );
    }

    @Override
    protected XPage redirectWorkflowActionCancelled( HttpServletRequest request )
    {
        Form form = FormHome.getFormFromRequest( request );
        return redirectView( request, VIEW_CREATE_TICKET_DYNAMIC_FORM, form );
    }

    @Override
    protected XPage defaultRedirectWorkflowAction( HttpServletRequest request )
    {
        Form form = FormHome.getFormFromRequest( request );
        return redirectView( request, VIEW_CREATE_TICKET_DYNAMIC_FORM, form );
    }
}
