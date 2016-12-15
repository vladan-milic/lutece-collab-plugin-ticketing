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
package fr.paris.lutece.plugins.ticketing.business.category;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * ITicketCategoryDAO Interface
 */
public interface ITicketCategoryDAO
{
    /**
     * Insert a new record in the table.
     * @param ticketCategory instance of the TicketCategory object to insert
     * @param plugin the Plugin
     */
    void insert( TicketCategory ticketCategory, Plugin plugin );

    /**
     * Insert a new record in the table linking category with input.
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param nPos input position
     * @param plugin the Plugin
     */
    void insertLinkCategoryInput( int nIdCategory, int nIdInput, int nPos, Plugin plugin );

    /**
     * Update the record in the table
     * @param ticketCategory the reference of the TicketCategory
     * @param plugin the Plugin
     */
    void store( TicketCategory ticketCategory, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nKey The identifier of the TicketCategory to delete
     * @param plugin the Plugin
     */
    void delete( int nKey, Plugin plugin );

    /**
     * Delete a record in the table linking category with input.
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param plugin the Plugin
     */
    void deleteLinkCategoryInput( int nIdCategory, int nIdInput, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * @param nKey The identifier of the ticketCategory
     * @param plugin the Plugin
     * @return The instance of the ticketCategory
     */
    TicketCategory load( int nKey, Plugin plugin );

    /**
     * Load the data from the table
     * @param strCode The code of the category
     * @param plugin the Plugin
     * @return The instance of the ticketCategory
     */
    TicketCategory loadByCode( String strCode, Plugin plugin );
    
    /**
     * Load the data from the table
     * @param nDomainId The domain id of the category
     * @param plugin the Plugin
     * @return The instance of the ticketCategory
     */
    List<TicketCategory> loadByDomainId( int nDomainId, Plugin plugin );

    /**
     * Load the data of all the ticketCategory objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the data of all the ticketCategory objects
     */
    List<TicketCategory> selectTicketCategorysList( Plugin plugin );

    /**
     * Load the id of all the ticketCategory objects and returns them as a collection
     * @param plugin the Plugin
     * @return The collection which contains the id of all the ticketCategory objects
     */
    List<Integer> selectIdTicketCategorysList( Plugin plugin );

    /**
     * Create a reference list of categories for a given domain
     * @param nDomainId The domain ID
     * @param plugin The plugin
     * @return The reference list
     */
    ReferenceList selectReferenceListByDomain( int nDomainId, Plugin plugin );

    /**
     * Insert a new record in the table linking category with input
     * Using the next available value for pos
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param plugin the Plugin
     */
    void insertLinkCategoryInputNextPos( int nIdCategory, int nIdInput, Plugin plugin );

    /**
     * Update pos field in the table linking category with input
     * @param nIdCategory id Category
     * @param nIdInput id Input
     * @param nPos the position to update
     * @param plugin the Plugin
     */
    void updateLinkCategoryInputPos( int nIdCategory, int nIdInput, int nPos, Plugin plugin );

    /**
     * returns the position of an input for a given category
     * @param nId id of category
     * @param nIdInput id of input
     * @return the position as an integer
     */
    int selectCategoryInputPosition( int nId, int nIdInput, Plugin _plugin );

    /**
     * returns the iD of an input for a given category and position
     * @param nId id of category
     * @param nIdInput the position
     * @return the input id
     */
    int selectCategoryInputByPosition( int nId, int nPos, Plugin plugin );

    /**
     * Create a reference list of categories for a given domain for a given category name
     * @param nDomainId The domain ID
     * @param labelCategory The label category
     * @param plugin The plugin
     * @return The reference list
     */
    ReferenceList selectReferenceListByCategory( int nDomainId, String labelCategory, Plugin _plugin );

	/**
     * Load the id of all inputs related to the ticketCategory id and returns them as a collection
     * @param nCategoryId The Category ID
     * @param plugin The plugin
     * @return The collection which contains the id of all the ticketCategory objects
     */
    List<Integer> selectIdInputListByCategory( int nCategoryId, Plugin _plugin );
}