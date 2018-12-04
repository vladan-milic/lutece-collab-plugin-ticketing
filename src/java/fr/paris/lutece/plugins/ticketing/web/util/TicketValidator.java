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
package fr.paris.lutece.plugins.ticketing.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactMode;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.form.Form;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntryType;
import fr.paris.lutece.plugins.ticketing.business.form.FormHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.beanvalidation.ValidationError;

/**
 * This class permits to validate a ticket
 *
 */
public class TicketValidator
{
    // Errors
    private static final String ERROR_USER_TITLE_UNKNOWN   = "ticketing.error.userTitle.unknown";
    private static final String ERROR_CONTACT_MODE_UNKNOWN = "ticketing.error.contactMode.unknown";
    private static final String ERROR_CHANNEL_UNKNOWN      = "ticketing.error.channel.unknown";

    // Attributes
    private Locale              _locale;

    /**
     * Constructor
     *
     * @param locale
     *            the locale used to select the correct validation error messages
     */
    public TicketValidator( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Validates a ticket, considered as a bean
     *
     * @param ticket
     *            the ticket
     * @return the list of validation errors
     */
    public List<String> validateBean( Ticket ticket )
    {
        List<String> listErrors = new ArrayList<String>( );

        List<ValidationError> listValidationErrors = BeanValidationUtil.validate( ticket, _locale, TicketingConstants.VALIDATION_ATTRIBUTES_PREFIX );

        if ( ( ticket != null ) && ( ticket.getTicketAddress( ) != null ) )
        {
            listValidationErrors.addAll( BeanValidationUtil.validate( ticket.getTicketAddress( ), _locale, TicketingConstants.VALIDATION_ATTRIBUTES_PREFIX ) );
        }

        for ( ValidationError error : listValidationErrors )
        {
            listErrors.add( error.getMessage( ) );
        }

        return listErrors;
    }

    /**
     * Validates a ticket
     *
     * @param ticket
     *            the ticket
     * @return the validation errors
     */
    public List<String> validate( Ticket ticket )
    {
        return validate( ticket, true );
    }

    /**
     * Validates a ticket
     *
     * @param ticket
     *            the ticket
     * @param bValidateReferenceData
     *            {@code true} if the reference data must be validated, {@code false} otherwise
     * @return the validation errors
     */
    public List<String> validate( Ticket ticket, boolean bValidateReferenceData )
    {
        List<String> listErrors = new ArrayList<String>( );

        if ( bValidateReferenceData )
        {
            UserTitle userTitle = UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle( ) );

            if ( userTitle == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_USER_TITLE_UNKNOWN, _locale ) );
            }

            ContactMode contactMode = ContactModeHome.findByPrimaryKey( ticket.getIdContactMode( ) );

            if ( contactMode == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_CONTACT_MODE_UNKNOWN, _locale ) );
            }

            Channel channel = ChannelHome.findByPrimaryKey( ticket.getChannel( ).getId( ) );

            if ( channel == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_CHANNEL_UNKNOWN, _locale ) );
            }
        }

        return listErrors;
    }

    /**
     * Validate fields and return errors
     *
     * @param request
     *            request
     * @return errors
     */
    public List<String> validateDynamicFields( HttpServletRequest request )
    {
        List<String> listValidationErrors = new ArrayList<>( );

        FormValidator formValidator = new FormValidator( request );
        Form form = FormHome.getFormFromRequest( request );

        FormEntryType formEntryType = new FormEntryType( );
        if ( defaultOptional( form, formEntryType.getUserTitle( ) ) )
        {
            listValidationErrors.add( formValidator.isUserTitleFilled( ) );
        }

        if ( defaultRequired( form, formEntryType.getFirstName( ) ) )
        {
            listValidationErrors.add( formValidator.isFirstNameFilled( ) );
        }

        if ( defaultRequired( form, formEntryType.getLastName( ) ) )
        {
            listValidationErrors.add( formValidator.isLastNameFilled( ) );
        }

        if ( defaultRequired( form, formEntryType.getEmail( ) ) )
        {
            listValidationErrors.add( formValidator.isEmailFilled( ) );
        }

        if ( defaultRequired( form, formEntryType.getPhoneNumbers( ) ) )
        {
            listValidationErrors.add( formValidator.isPhoneNumberFilled( ) );
        }

        if ( defaultOptional( form, formEntryType.getContactMode( ) ) )
        {
            listValidationErrors.add( formValidator.isContactModeFilled( ) );
        }

        if ( defaultRequired( form, formEntryType.getComment( ) ) )
        {
            listValidationErrors.add( formValidator.isCommentFilled( ) );
        }

        return listValidationErrors;
    }

    private boolean defaultRequired( Form form, String entryType )
    {
        return ( form == null ) || isMandatoryEntry( form, entryType );
    }

    private boolean defaultOptional( Form form, String entryType )
    {
        return isMandatoryEntry( form, entryType );
    }

    private boolean isMandatoryEntry( Form form, String entry )
    {
        return ( form != null ) && form.getEntry( entry ).isMandatory( );
    }
}
