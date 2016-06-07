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

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.channel.ChannelHome;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactMode;
import fr.paris.lutece.plugins.ticketing.business.contactmode.ContactModeHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitle;
import fr.paris.lutece.plugins.ticketing.business.usertitle.UserTitleHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.beanvalidation.ValidationError;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * This class permits to validate a ticket
 *
 */
public class TicketValidator
{
    // Errors
    private static final String ERROR_PHONE_NUMBER_MISSING = "ticketing.error.phonenumber.missing";
    private static final String ERROR_USER_TITLE_UNKNOWN = "ticketing.error.userTitle.unknown";
    private static final String ERROR_TICKET_CATEGORY_UNKNOWN = "ticketing.error.ticketCategory.unknown";
    private static final String ERROR_CONTACT_MODE_UNKNOWN = "ticketing.error.contactMode.unknown";
    private static final String ERROR_CHANNEL_UNKNOWN = "ticketing.error.channel.unknown";

    // Attributes
    private Locale _locale;

    /**
     * Constructor
     * @param locale the locale used to select the correct validation error messages
     */
    public TicketValidator( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Validates a ticket, considered as a bean
     * @param ticket the ticket
     * @return the list of validation errors
     */
    public List<String> validateBean( Ticket ticket )
    {
        List<String> listErrors = new ArrayList<String>(  );

        List<ValidationError> listValidationErrors = BeanValidationUtil.validate( ticket, _locale,
                TicketingConstants.VALIDATION_ATTRIBUTES_PREFIX );

        for ( ValidationError error : listValidationErrors )
        {
            listErrors.add( error.getMessage(  ) );
        }

        return listErrors;
    }

    /**
     * Validates a ticket
     * @param ticket the ticket
     * @return the validation errors
     */
    public List<String> validate( Ticket ticket )
    {
        return validate( ticket, true );
    }

    /**
     * Validates a ticket
     * @param ticket the ticket
     * @param bValidateReferenceData {@code true} if the reference data must be validated, {@code false} otherwise
     * @return the validation errors
     */
    public List<String> validate( Ticket ticket, boolean bValidateReferenceData )
    {
        List<String> listErrors = new ArrayList<String>(  );

        if ( !hasPhoneNumber( ticket ) )
        {
            listErrors.add( I18nService.getLocalizedString( ERROR_PHONE_NUMBER_MISSING, _locale ) );
        }

        if ( bValidateReferenceData )
        {
            UserTitle userTitle = UserTitleHome.findByPrimaryKey( ticket.getIdUserTitle(  ) );

            if ( userTitle == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_USER_TITLE_UNKNOWN, _locale ) );
            }

            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );

            if ( ticketCategory == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_TICKET_CATEGORY_UNKNOWN, _locale ) );
            }

            ContactMode contactMode = ContactModeHome.findByPrimaryKey( ticket.getIdContactMode(  ) );

            if ( contactMode == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_CONTACT_MODE_UNKNOWN, _locale ) );
            }

            Channel channel = ChannelHome.findByPrimaryKey( ticket.getIdChannel(  ) );

            if ( channel == null )
            {
                listErrors.add( I18nService.getLocalizedString( ERROR_CHANNEL_UNKNOWN, _locale ) );
            }
        }

        return listErrors;
    }

    /**
     * Tests whether the specified ticket has a phone number or not
     *
     * @param ticket the ticket to test
     * @return {@code true} if neither the fixed phone number nor the mobile phone number are
     * filled, {@code false} otherwise
     */
    private boolean hasPhoneNumber( Ticket ticket )
    {
        return !StringUtils.isBlank( ticket.getFixedPhoneNumber(  ) ) ||
        !StringUtils.isBlank( ticket.getMobilePhoneNumber(  ) );
    }
}
