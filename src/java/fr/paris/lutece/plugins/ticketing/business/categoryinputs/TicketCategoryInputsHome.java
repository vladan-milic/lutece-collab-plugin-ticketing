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
package fr.paris.lutece.plugins.ticketing.business.categoryinputs;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for TicketCategory objects
 */

/**
 * @author a120274
 *
 */
public final class TicketCategoryInputsHome
{
    // Static variable pointed at the DAO instance
    private static ITicketCategoryInputsDAO _dao = SpringContextService.getBean( "ticketing.ticketCategoryInputsDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TicketCategoryInputsHome( )
    {
    }

    /**
     * Create a link between a category and an input
     *
     * @param nIdCategory
     *            id Category
     * @param nIdInput
     *            id Input
     * @param nPos
     *            id Input position
     */
    public static void createLinkCategoryInput( int nIdCategory, int nIdInput, int nPos )
    {
        _dao.insertLinkCategoryInput( nIdCategory, nIdInput, nPos, _plugin );
    }

    /**
     * Create a link between a category and an input
     *
     * @param nIdCategory
     *            id Category
     * @param nIdInput
     *            id Input
     */
    public static void createLinkCategoryInputNextPos( int nIdCategory, int nIdInput )
    {
        _dao.insertLinkCategoryInputNextPos( nIdCategory, nIdInput, _plugin );
    }

    /**
     * Remove a link between a category and an input
     *
     * @param nIdCategory
     *            id Category
     * @param nIdInput
     *            id Input
     */
    public static void removeLinkCategoryInput( int nIdCategory, int nIdInput )
    {
        _dao.deleteLinkCategoryInput( nIdCategory, nIdInput, _plugin );
    }

    /**
     * Remove all links for a category
     *
     * @param nIdCategory
     *            id Category
     */
    public static void removeAllLinksCategoryInput( int nIdCategory )
    {
        _dao.deleteAllLinksCategoryInput( nIdCategory, _plugin );
    }

    /**
     * Update the Position field in a link between a category and an input
     *
     * @param nIdCategory
     *            id Category
     * @param nIdInput
     *            id Input
     * @param nPosition
     *            the position value
     */
    public static void updateCategoryInputPosition( int nIdCategory, int nIdInput, int nPosition )
    {
        _dao.updateLinkCategoryInputPos( nIdCategory, nIdInput, nPosition, _plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * returns the position of an input for a given category
     *
     * @param nId
     *            id of category
     * @param nIdInput
     *            id of input
     * @return the position as an integer
     */
    public static int getCategoryInputPosition( int nId, int nIdInput )
    {
        return _dao.selectCategoryInputPosition( nId, nIdInput, _plugin );
    }

    /**
     * returns the iD of an input for a given category and position
     *
     * @param nId
     *            id of category
     * @param nPos
     *            the position
     * @return the input id
     */
    public static int getCategoryInputByPosition( int nId, int nPos )
    {
        return _dao.selectCategoryInputByPosition( nId, nPos, _plugin );
    }

    /**
     * Load the id of all inputs related to the ticketCategory id and returns them as a collection
     *
     * @param nCategoryId
     *            The Category ID
     * @return The collection which contains the id of all the ticketCategory objects
     */
    public static List<Integer> getIdInputListByCategory( int nCategoryId )
    {
        return _dao.selectIdInputListByCategory( nCategoryId, _plugin );
    }

    /**
     * Check if an input is already used in a Category form
     *
     * @param nIdResource
     *            the id_resource of the input to be checked
     * @return true if the input is linked to 1 or more Categories
     */
    public static boolean checkIfInputIsUsedInCategories( int nIdResource )
    {
        return _dao.checkIfInputIsUsedInCategories( nIdResource, _plugin );
    }

}
