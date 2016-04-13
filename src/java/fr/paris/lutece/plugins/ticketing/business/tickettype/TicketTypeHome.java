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
package fr.paris.lutece.plugins.ticketing.business.tickettype;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for TicketType objects
 */
public final class TicketTypeHome
{
    // Static variable pointed at the DAO instance
    private static ITicketTypeDAO _dao = SpringContextService.getBean( "ticketing.ticketTypeDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketTypeHome(  )
    {
    }

    /**
     * Create an instance of the ticketType class
     * @param ticketType The instance of the TicketType which contains the informations to store
     */
    public static void create( TicketType ticketType )
    {
        _dao.insert( ticketType, _plugin );
    }

    /**
     * Update of the ticketType which is specified in parameter
     * @param ticketType The instance of the TicketType which contains the data to store
     */
    public static void update( TicketType ticketType )
    {
        _dao.store( ticketType, _plugin );
    }

    /**
     * Remove the ticketType whose identifier is specified in parameter
     * @param nKey The ticketType Id
     */
    public static void remove( int nKey )
    {
        if ( canRemove( nKey ) )
        {
            _dao.delete( nKey, _plugin );
        }
        else
        {
            throw new AppException( "TicketType cannot be removed for ID :" + nKey );
        }
    }

    /**
     * return true if type can be removed false otherwise
     * @param nKey The ticketType Id
     * @return true if type can be removed false otherwise
     */
    public static boolean canRemove( int nKey )
    {
        return _dao.canRemoveType( nKey, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticketType whose identifier is specified in parameter
     * @param nKey The ticketType primary key
     * @return an instance of TicketType
     */
    public static TicketType findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the ticketType objects and returns them in form of a collection
     * @return the collection which contains the data of all the ticketType objects
     */
    public static List<TicketType> getTicketTypesList(  )
    {
        return _dao.selectTicketTypesList( _plugin );
    }

    /**
     * Load the id of all the ticketType objects and returns them in form of a collection
     * @return the collection which contains the id of all the ticketType objects
     */
    public static List<Integer> getIdTicketTypesList(  )
    {
        return _dao.selectIdTicketTypesList( _plugin );
    }

    /**
     * returns referenceList
     * @return ReferenceList
     */
    public static ReferenceList getReferenceList(  )
    {
        return _dao.selectReferenceList( _plugin );
    }
}
