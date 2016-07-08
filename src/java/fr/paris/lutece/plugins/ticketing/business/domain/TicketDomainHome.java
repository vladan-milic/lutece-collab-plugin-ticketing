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
package fr.paris.lutece.plugins.ticketing.business.domain;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for TicketDomain objects
 */
public final class TicketDomainHome
{
    // Static variable pointed at the DAO instance
    private static ITicketDomainDAO _dao = SpringContextService.getBean( "ticketing.ticketDomainDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketDomainHome(  )
    {
    }

    /**
     * Create an instance of the ticketDomain class
     * @param ticketDomain The instance of the TicketDomain which contains the informations to store
     */
    public static void create( TicketDomain ticketDomain )
    {
        _dao.insert( ticketDomain, _plugin );
    }

    /**
     * Update of the ticketDomain which is specified in parameter
     * @param ticketDomain The instance of the TicketDomain which contains the data to store
     */
    public static void update( TicketDomain ticketDomain )
    {
        _dao.store( ticketDomain, _plugin );
    }

    /**
     * Remove the ticketDomain whose identifier is specified in parameter
     * @param nKey The ticketDomain Id
     */
    public static void remove( int nKey )
    {
        if ( canRemove( nKey ) )
        {
            _dao.delete( nKey, _plugin );
        }
        else
        {
            throw new AppException( "TicketDomain cannot be removed for ID :" + nKey );
        }
    }

    /**
     * return true if domain can be removed false otherwise
     * @param nKey The ticketDomain Id
     * @return true if type can be removed false otherwise
     */
    public static boolean canRemove( int nKey )
    {
        return _dao.canRemoveDomain( nKey, _plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ticketDomain whose identifier is specified in parameter
     * @param nKey The ticketDomain primary key
     * @return an instance of TicketDomain
     */
    public static TicketDomain findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the ticketDomain objects and returns them in form of a collection
     * @return the collection which contains the data of all the ticketDomain objects
     */
    public static List<TicketDomain> getTicketDomainsList(  )
    {
        return _dao.selectTicketDomainsList( _plugin );
    }

    /**
     * Load the id of all the ticketDomain objects and returns them in form of a collection
     * @return the collection which contains the id of all the ticketDomain objects
     */
    public static List<Integer> getIdTicketDomainsList(  )
    {
        return _dao.selectIdTicketDomainsList( _plugin );
    }

    /**
     * returns referenceList by type of input typeId
     * @param nTicketTypeId id of type
     * @return ReferenceList of typeId
     */
    public static ReferenceList getReferenceListByType( int nTicketTypeId )
    {
        return _dao.selectReferenceListByType( nTicketTypeId, _plugin );
    }

    /**
     * returns referenceList
     * @return ReferenceList
     */
    public static ReferenceList getReferenceList(  )
    {
        return _dao.selectReferenceList( _plugin );
    }

    /**
    * returns referenceList
    * @return ReferenceList
    */
    public static ReferenceList getReferenceListSimple(  )
    {
        return _dao.selectReferenceListSimple( _plugin );
    }
}
