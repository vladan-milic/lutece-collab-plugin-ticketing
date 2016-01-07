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

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import java.util.Locale;

/**
 * TicketingUtils
 */
public class TicketingUtils 
{
    // Priorities
    private static final int PRIORITY_LOW = 0;
    private static final int PRIORITY_MEDIUM = 1;
    private static final int PRIORITY_HIGH = 2;
    private static final String PROPERTY_PRIORITY_LOW = "ticketing.priority.low";
    private static final String PROPERTY_PRIORITY_MEDIUM = "ticketing.priority.medium";
    private static final String PROPERTY_PRIORITY_HIGH = "ticketing.priority.high";
    
    // Criticalities
    private static final int CRITICALITY_LOW = 0;
    private static final int CRITICALITY_MEDIUM = 1;
    private static final int CRITICALITY_HIGH = 2;
    private static final String PROPERTY_CRITICALITY_LOW = "ticketing.criticality.low";
    private static final String PROPERTY_CRITICALITY_MEDIUM = "ticketing.criticality.medium";
    private static final String PROPERTY_CRITICALITY_HIGH = "ticketing.criticality.high";
    
    /**
     * Provides the priority list
     * @param locale The locale
     * @return The list
     */
    public static ReferenceList getPriorityList( Locale locale )
    {
        ReferenceList list = new ReferenceList();
        list.addItem( PRIORITY_LOW , I18nService.getLocalizedString( PROPERTY_PRIORITY_LOW, locale));
        list.addItem( PRIORITY_MEDIUM , I18nService.getLocalizedString( PROPERTY_PRIORITY_MEDIUM, locale));
        list.addItem( PRIORITY_HIGH , I18nService.getLocalizedString( PROPERTY_PRIORITY_HIGH, locale));
        return list;
    }
    
    /**
     * Provides the criticality list
     * @param locale The locale
     * @return The list
     */
    public static ReferenceList getCriticalityList( Locale locale )
    {
        ReferenceList list = new ReferenceList();
        list.addItem( CRITICALITY_LOW , I18nService.getLocalizedString( PROPERTY_CRITICALITY_LOW, locale));
        list.addItem( CRITICALITY_MEDIUM , I18nService.getLocalizedString( PROPERTY_CRITICALITY_MEDIUM, locale));
        list.addItem( CRITICALITY_HIGH , I18nService.getLocalizedString( PROPERTY_CRITICALITY_HIGH, locale));
        return list;
    }

    /**
     * Get the priority label of a ticket for a given locale
     * @param ticket The ticket
     * @param locale The locale
     * @return The label
     */
    public static String getPriority( Ticket ticket , Locale locale )
    {
        for( ReferenceItem item : getPriorityList( locale ))
        {
            if( Integer.parseInt( item.getCode()) == ticket.getPriority() )
            {
                return item.getName();
            }
        }
        throw new AppException( "TicketingUtils - Invalid Priority value : " + ticket.getPriority());
    }

    /**
     * Get the criticality label of a ticket for a given locale
     * @param ticket The ticket
     * @param locale The locale
     * @return The label
     */
    public static String getCriticality( Ticket ticket , Locale locale )
    {
        for( ReferenceItem item : getPriorityList( locale ))
        {
            if( Integer.parseInt( item.getCode()) == ticket.getCriticality() )
            {
                return item.getName();
            }
        }
        throw new AppException( "TicketingUtils - Invalid Criticality value : " + ticket.getCriticality() );
    }

    
}
