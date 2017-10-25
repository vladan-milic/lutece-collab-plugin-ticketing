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
package fr.paris.lutece.plugins.ticketing.web.rs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.search.IndexerActionHome;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexer;
import fr.paris.lutece.plugins.ticketing.business.search.TicketIndexerException;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.service.format.IFormatterFactory;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexerActionUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidator;
import fr.paris.lutece.plugins.ticketing.web.util.TicketValidatorFactory;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * REST service for ticket resource
 */
@Path( RestConstants.BASE_PATH + Constants.PLUGIN_PATH + Constants.TICKET_PATH )
public class TicketRest extends TicketingRest
{
    // Parameters
    private static final String PARAMETER_TICKET_CATEGORY_CODE = "ticket_category_code";
    private static final String PARAMETER_TICKET_CONTACT_MODE_ID = "ticket_contact_mode_id";
    private static final String PARAMETER_TICKET_COMMENT = "ticket_comment";
    private static final String PARAMETER_USER_TITLE_ID = "user_title_id";
    private static final String PARAMETER_USER_FIRST_NAME = "user_first_name";
    private static final String PARAMETER_USER_LAST_NAME = "user_last_name";
    private static final String PARAMETER_USER_EMAIL = "user_email";
    private static final String PARAMETER_USER_FIXED_PHONE_NUMBER = "user_fixed_phone_number";
    private static final String PARAMETER_USER_MOBILE_PHONE_NUMBER = "user_mobile_phone_number";
    private static final String PARAMETER_TICKET_CHANNEL_ID = "ticket_channel_id";
    private static final String PARAMETER_TICKET_NOMENCLATURE = "ticket_nomenclature";

    // Services
    private static WorkflowService _workflowService;

    /**
     * Default constructor
     */
    public TicketRest( )
    {
        super( );
    }

    /**
     * Creates a ticket
     * 
     * @param strIdUserTitle
     *            the user title id
     * @param strFirstname
     *            the user first name
     * @param strLastname
     *            the user last name
     * @param strEmail
     *            the email
     * @param strFixedPhoneNumber
     *            the user fixed phone number
     * @param strMobilePhoneNumber
     *            the user mobile phone number
     * @param strCategoryCode
     *            the category code
     * @param strIdContactMode
     *            the contact mode
     * @param strIdChannel
     *            the channel id
     * @param strComment
     *            the comment
     * @param strGuid
     *            the guid
     * @param strIdCustomer
     *            the customer id
     * @param accept
     *            the accepted format
     * @param format
     *            the format
     * @param request
     *            the request
     * @return the reference of the created ticket
     */
    @PUT
    public Response createTicket( @FormParam( PARAMETER_USER_TITLE_ID ) String strIdUserTitle, @FormParam( PARAMETER_USER_FIRST_NAME ) String strFirstname,
            @FormParam( PARAMETER_USER_LAST_NAME ) String strLastname, @FormParam( PARAMETER_USER_EMAIL ) String strEmail,
            @FormParam( PARAMETER_USER_FIXED_PHONE_NUMBER ) String strFixedPhoneNumber,
            @FormParam( PARAMETER_USER_MOBILE_PHONE_NUMBER ) String strMobilePhoneNumber, @FormParam( PARAMETER_TICKET_CATEGORY_CODE ) String strCategoryCode,
            @FormParam( PARAMETER_TICKET_CONTACT_MODE_ID ) String strIdContactMode, @FormParam( PARAMETER_TICKET_CHANNEL_ID ) String strIdChannel,
            @FormParam( PARAMETER_TICKET_COMMENT ) String strComment, @FormParam( PARAMETER_TICKET_NOMENCLATURE ) String strNomenclature,
            @FormParam( TicketingConstants.PARAMETER_GUID ) String strGuid, @FormParam( TicketingConstants.PARAMETER_CUSTOMER_ID ) String strIdCustomer,
            @HeaderParam( HttpHeaders.ACCEPT ) String accept, @QueryParam( Constants.FORMAT_QUERY ) String format, @Context HttpServletRequest request )
    {
        String strMediaType = getMediaType( accept, format );

        Ticket ticket = new Ticket( );

        String strModifiedIdUserTitle = strIdUserTitle;

        if ( StringUtils.isEmpty( strIdUserTitle ) )
        {
            List<UserTitle> listUserTitle = UserTitleHome.getUserTitlesList( );
            Iterator<UserTitle> iterator = listUserTitle.iterator( );

            if ( iterator.hasNext( ) )
            {
                strModifiedIdUserTitle = String.valueOf( iterator.next( ).getId( ) );
            }
        }

        String strModifiedIdContactMode = strIdContactMode;

        if ( StringUtils.isEmpty( strIdContactMode ) )
        {
            List<Integer> listIdContactMode = ContactModeHome.getIdContactModesList( );
            Iterator<Integer> iterator = listIdContactMode.iterator( );

            if ( iterator.hasNext( ) )
            {
                strModifiedIdContactMode = iterator.next( ).toString( );
            }
        }

        String strModifiedIdChannel = strIdChannel;

        if ( StringUtils.isEmpty( strIdChannel ) )
        {
            List<Integer> listIdChannel = ChannelHome.getIdChannelList( );
            Iterator<Integer> iterator = listIdChannel.iterator( );

            if ( iterator.hasNext( ) )
            {
                strModifiedIdChannel = iterator.next( ).toString( );
            }
        }

        ticket.enrich( strModifiedIdUserTitle, strFirstname, strLastname, strFixedPhoneNumber, strMobilePhoneNumber, strEmail, strCategoryCode,
                strModifiedIdContactMode, strModifiedIdChannel, strComment, strGuid, strIdCustomer, strNomenclature );

        IFormatterFactory formatterFactory = _formatterFactories.get( strMediaType );

        TicketValidator validator = TicketValidatorFactory.getInstance( ).create( request.getLocale( ) );
        List<String> listValidationErrors = validator.validateBean( ticket );
        listValidationErrors.addAll( validator.validate( ticket ) );

        if ( !listValidationErrors.isEmpty( ) )
        {
            String strValidationErrorsMessage = formatterFactory.createRestFormatter( ).formatErrors( listValidationErrors );

            return Response.ok( strValidationErrorsMessage, strMediaType ).build( );
        }

        List<String> creationErrors = create( ticket );

        if ( !creationErrors.isEmpty( ) )
        {
            String strCreationErrorsMessage = formatterFactory.createRestFormatter( ).formatErrors( creationErrors );

            return Response.ok( strCreationErrorsMessage, strMediaType ).build( );
        }

        // Reloads the ticket to have the data updated by the workflow
        ticket = TicketHome.findByPrimaryKey( ticket.getId( ) );

        String strResponse = formatterFactory.createFormatter( Ticket.class ).formatResponse( ticket );

        return Response.ok( strResponse, strMediaType ).build( );
    }

    /**
     * Creates the specified ticket. The ticket is saved and injected in the workflow.
     * 
     * @param ticket
     *            the ticket to create
     * @return the list of errors which occurred during the creation
     */
    private List<String> create( Ticket ticket )
    {
        List<String> errors = new ArrayList<String>( );
        TicketCategory ticketCategory = ticket.getTicketCategory( );
        int nIdWorkflow = ticketCategory.getIdWorkflow( );

        if ( _workflowService == null )
        {
            _workflowService = WorkflowService.getInstance( );
        }

        TicketHome.create( ticket );

        if ( ( nIdWorkflow > 0 ) && _workflowService.isAvailable( ) )
        {
            try
            {
                _workflowService.getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, null );
                _workflowService.executeActionAutomatic( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, null );

                // Immediate indexation of the Ticket
                immediateTicketIndexing( ticket.getId( ), errors );
            }
            catch( Exception e )
            {
                _workflowService.doRemoveWorkFlowResource( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE );
                TicketHome.remove( ticket.getId( ) );

                errors.add( I18nService.getLocalizedString( TicketingConstants.ERROR_TICKET_CREATION_ABORTED, Locale.FRENCH ) );
            }
        }

        return errors;
    }

    protected void immediateTicketIndexing( int idTicket, List<String> errors )
    {
        Ticket ticket = TicketHome.findByPrimaryKey( idTicket );
        if ( ticket != null )
        {
            try
            {
                TicketIndexer ticketIndexer = new TicketIndexer( );
                ticketIndexer.indexTicket( ticket );
            }
            catch( TicketIndexerException ticketIndexerException )
            {
                errors.add( I18nService.getLocalizedString( TicketingConstants.ERROR_INDEX_TICKET_FAILED_BACK, Locale.FRENCH ) );

                // The indexation of the Ticket fail, we will store the Ticket in the table for the daemon
                IndexerActionHome.create( TicketIndexerActionUtil.createIndexerActionFromTicket( ticket ) );
            }
        }
    }
}
