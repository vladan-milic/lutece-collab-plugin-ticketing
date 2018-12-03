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

package fr.paris.lutece.plugins.ticketing.business.category;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;

/**
 * ICategoryDAO Interface
 */
public interface ITicketCategoryDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param category
     *            instance of the Category object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( TicketCategory category, Plugin plugin );

    /**
     * Update the record in the table
     *
     * @param category
     *            the reference of the Category
     * @param plugin
     *            the Plugin
     */
    void store( TicketCategory category, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nKey
     *            The identifier of the Category to delete
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
     *            The identifier of the category
     * @param plugin
     *            the Plugin
     * @return The instance of the category
     */
    TicketCategory load( int nKey, Plugin plugin );

    /**
     * Load the category for given code
     *
     * @param strCode
     * @param plugin
     * @return the category
     */
    TicketCategory loadByCode( String strCode, Plugin plugin );

    /**
     * Load the data of all the category objects and returns them as a list
     *
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the category objects
     */
    List<TicketCategory> selectCategorysList( Plugin plugin );

    /**
     * Load the list of categories (full with java objects filled)
     *
     * @param plugin
     * @param withInactives
     * @return the full categories list
     */
    List<TicketCategory> selectFullCategorysList( Plugin plugin, boolean withInactives );

    /**
     * Load the id of all the category objects and returns them as a list
     *
     * @param plugin
     *            the Plugin
     * @return The list which contains the id of all the category objects
     */
    List<Integer> selectIdCategorysList( Plugin plugin );

    /**
     * Load the data of all the category objects and returns them as a referenceList
     *
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the category objects
     */
    ReferenceList selectCategorysReferenceList( Plugin plugin );

    /**
     * Update order of a category
     *
     * @param nId
     *            id parent to move
     * @param nNewPosition
     *            the order value to update
     * @param _plugin
     * @param plugin
     *            the Plugin
     */
    void updateCategoryOrder( int nId, int nNewPosition, Plugin _plugin );

    /**
     * returns the Id of a category for a given position and parent
     *
     * @param nOrder
     *            Position of the category
     * @param nIdParent
     *            Id of the parent
     * @param _plugin
     * @return the id of category as an integer
     */
    int selectCategoryIdByOrder( int nOrder, int nIdParent, Plugin _plugin );

    /**
     * Rebuild the order sequence of active TicketCategory for a parent, by substracting 1 to all orders larger than a given value
     * 
     * @param nFromOrder
     *
     * @param nfromOrder
     *            the order to rebuild sequence from
     * @param nIdParent
     *            the parent id
     * @param _plugin
     */
    void rebuildCategoryOrders( int nFromOrder, int nIdParent, Plugin _plugin );

    /**
     * Update the record in the table. The field n_order will be replaced by the next available value for the category parent.
     *
     * @param category
     *            the reference of the TicketCategory
     * @param _plugin
     * @param plugin
     *            the Plugin
     */
    void storeWithLastOrder( TicketCategory category, Plugin _plugin );

    /**
     * check if category can be removed
     *
     * @param nKey
     *            The identifier of the category
     * @param plugin
     *            the Plugin
     * @return true if category can be removed, false otherwise
     */
    boolean canRemoveCategory( int nKey, Plugin plugin );
}
