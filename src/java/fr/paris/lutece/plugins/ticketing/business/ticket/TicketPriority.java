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
package fr.paris.lutece.plugins.ticketing.business.ticket;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This enum represents the priority of a ticket
 *
 */
public enum TicketPriority
{
    LOW, MEDIUM, HIGH;

    private static final String MESSAGE_PREFIX = "ticketing.priority.";
    private static Map<Integer, TicketPriority> _mapTicketPriority = new HashMap<Integer, TicketPriority>( );

    static
    {
        for ( TicketPriority enumTicketPriority : EnumSet.allOf( TicketPriority.class ) )
        {
            _mapTicketPriority.put( enumTicketPriority.ordinal( ), enumTicketPriority );
        }
    }

    /**
     * returns TicketPriority enum from id
     * 
     * @param nTicketPriorityId
     *            level value
     * @return TicketPriority enum
     */
    public static TicketPriority valueOf( int nTicketPriorityId )
    {
        return _mapTicketPriority.get( Integer.valueOf( nTicketPriorityId ) );
    }

    /**
     * Gives the localized message
     * 
     * @param locale
     *            the locale to use
     * @return the message
     */
    public String getLocalizedMessage( Locale locale )
    {
        return I18nService.getLocalizedString( MESSAGE_PREFIX + this.name( ).toLowerCase( ), locale );
    }

    /**
     * Builds a RefenrenceList object containing all the TicketPriority objects
     * 
     * @param locale
     *            the locale used to retrieve the localized messages
     * @return the ReferenceList object
     */
    public static ReferenceList getReferenceList( Locale locale )
    {
        ReferenceList listPriorities = new ReferenceList( );

        for ( TicketPriority ticketPriority : TicketPriority.values( ) )
        {
            listPriorities.addItem( ticketPriority.ordinal( ), ticketPriority.getLocalizedMessage( locale ) );
        }

        return listPriorities;
    }
}
