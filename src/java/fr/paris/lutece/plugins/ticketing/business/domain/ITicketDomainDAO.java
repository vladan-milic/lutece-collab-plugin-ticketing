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
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * ITicketDomainDAO Interface
 */
public interface ITicketDomainDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param ticketDomain
     *            instance of the TicketDomain object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( TicketDomain ticketDomain, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param ticketDomain
     *            the reference of the TicketDomain
     * @param plugin
     *            the Plugin
     */
    void store( TicketDomain ticketDomain, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the TicketDomain to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nKey, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the ticketDomain
     * @param plugin
     *            the Plugin
     * @return The instance of the ticketDomain
     */
    TicketDomain load( int nKey, Plugin plugin );

    /**
     * Load the data of all the ticketDomain objects and returns them as a collection
     * 
     * @param plugin
     *            the Plugin
     * @return The collection which contains the data of all the ticketDomain objects
     */
    List<TicketDomain> selectTicketDomainsList( Plugin plugin );

    /**
     * Load the id of all the ticketDomain objects and returns them as a collection
     * 
     * @param plugin
     *            the Plugin
     * @return The collection which contains the id of all the ticketDomain objects
     */
    List<Integer> selectIdTicketDomainsList( Plugin plugin );

    /**
     * Get a reference list of domains for a given ticket type
     * 
     * @param nTicketTypeId
     *            The type id
     * @param plugin
     *            The plugin
     * @return The reference list
     */
    ReferenceList selectReferenceListByType( int nTicketTypeId, Plugin plugin );

    /**
     * Get a reference list of domains for a given domain label
     * 
     * @param sDomainLabel
     *            The domain label
     * @param plugin
     *            The plugin
     * @return The reference list
     */
    List<TicketDomain> selectDomainsByLabel( String sDomainLabel, Plugin plugin );

    /**
     * Get a reference list of domains
     * 
     * @param plugin
     *            The plugin
     * @return The reference list
     */
    ReferenceList selectReferenceList( Plugin plugin );

    /**
     * Get a simple reference list of domains
     * 
     * @param plugin
     *            The plugin
     * @return The reference list
     */
    ReferenceList selectReferenceListSimple( Plugin plugin );

    /**
     * check if domain can be removed
     * 
     * @param nKey
     *            The identifier of the ticketDomain
     * @param plugin
     *            the Plugin
     * @return true if domain can be removed, false otherwise
     */
    boolean canRemoveDomain( int nKey, Plugin plugin );

    /**
     * Get a reference list of all domains with the name composed of the concatenation of the type name and the domain name
     * 
     * @param _plugin
     * @return the reference list
     */
    ReferenceList selectReferenceListModelResponse( Plugin _plugin );

    /**
     * Get a list of domains for a given Type id
     * 
     * @param nTicketTypeId
     *            The Type id
     * @param plugin
     *            The plugin
     * @return The reference list
     */
    public List<TicketDomain> selectDomainListByTypeId( int nTicketTypeId, Plugin plugin );

    /**
     * Update order of a Domain
     * 
     * @param nId
     *            id Type to move
     * @param nNewPosition
     *            the order value to update
     * @param plugin
     *            the Plugin
     */
    void updateDomainOrder( int nId, int nNewPosition, Plugin _plugin );

    /**
     * returns the Id of a Domain for a given position and TicketType
     * 
     * @param nOrder
     *            Position of the Domain
     * @param nIdType
     *            Id of the Type
     * @return the id of Domain as an integer
     */
    int selectDomainIdByOrder( int nOrder, int nIdType, Plugin _plugin );

    /**
     * Rebuild the order sequence of active TicketDomain for a TicketType, by substracting 1 to all orders larger than a given value
     * 
     * @param nfromOrder
     *            the order to rebuild sequence from
     * @param nIdTicketType
     *            the Type id
     */
    void rebuildDomainOrdersByType( int nfromOrder, int nIdTicketType, Plugin _plugin );

    /**
     * Update the record in the table. The field domain_order will be replaced by the next available value for the TicketType.
     * 
     * @param ticketDomain
     *            the reference of the TicketDomain
     * @param plugin
     *            the Plugin
     */
    void storeWithLastOrder( TicketDomain ticketDomain, Plugin _plugin );

}
