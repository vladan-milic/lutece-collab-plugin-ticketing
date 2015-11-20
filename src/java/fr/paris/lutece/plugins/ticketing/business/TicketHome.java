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
package fr.paris.lutece.plugins.ticketing.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for Ticket objects
 */
public final class TicketHome
{
    // Static variable pointed at the DAO instance
    private static ITicketDAO _dao = SpringContextService.getBean( "ticketing.ticketDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketHome(  )
    {
    }

    /**
     * Create an instance of the ticket class
     * @param ticket The instance of the Ticket which contains the informations to store
     * @return The  instance of ticket which has been created with its primary key.
     */
    public static Ticket create( Ticket ticket )
    {
        _dao.insert( ticket, _plugin );

        return ticket;
    }

    /**
     * Update of the ticket which is specified in parameter
     * @param ticket The instance of the Ticket which contains the data to store
     * @return The instance of the  ticket which has been updated
     */
    public static Ticket update( Ticket ticket )
    {
        _dao.store( ticket, _plugin );

        return ticket;
    }

    /**
     * Remove the ticket whose identifier is specified in parameter
     * @param nKey The ticket Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticket whose identifier is specified in parameter
     * @param nKey The ticket primary key
     * @return an instance of Ticket
     */
    public static Ticket findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the ticket objects and returns them in form of a collection
     * @return the collection which contains the data of all the ticket objects
     */
    public static List<Ticket> getTicketsList(  )
    {
        return _dao.selectTicketsList( _plugin );
    }

    /**
     * Load the id of all the ticket objects and returns them in form of a collection
     * @return the collection which contains the id of all the ticket objects
     */
    public static List<Integer> getIdTicketsList(  )
    {
        return _dao.selectIdTicketsList( _plugin );
    }
}
