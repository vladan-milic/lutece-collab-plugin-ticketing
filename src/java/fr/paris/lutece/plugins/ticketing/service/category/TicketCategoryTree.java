/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.service.category;

import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.ticketing.business.categoryinputs.TicketCategoryInputsHome;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.tree.Tree;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TicketCategoryTree extends Tree<TicketCategory, TicketCategoryType>
{
    private List<Integer> _restrictedCategoriesId = null;

    /**
     * Constructor for category tree
     *
     * @param listCategory
     *            the list of Categories
     * @param listCategoryType
     *            the list of Category types
     */
    public TicketCategoryTree( List<TicketCategory> listCategory, List<TicketCategoryType> listCategoryType )
    {
        super( listCategory, listCategoryType );
        listCategory.stream( ).forEach( ( category ) -> category.setListIdInput( TicketCategoryInputsHome.getIdInputListByCategory( category.getId( ) ) ) );
    }

    /**
     * Constructor for category tree
     *
     * @param treeSource
     *            Tree Source
     * @param restrictedCategoriesId
     *            the restricted Categories Id list
     *
     */
    public TicketCategoryTree( TicketCategoryTree treeSource, List<Integer> restrictedCategoriesId )
    {
        super( treeSource );
        _restrictedCategoriesId = restrictedCategoriesId;
        setRootElements( _rootNodes.stream( ).filter( root -> ( _restrictedCategoriesId == null ) || _restrictedCategoriesId.contains( root.getId( ) ) )
                .collect( Collectors.toList( ) ) );
    }

    /**
     * Find a category in the tree by code
     *
     * @param strCode
     *            the code
     * @return the category found with the given code
     */
    public TicketCategory findByCode( String strCode )
    {
        for ( TicketCategory category : _nodes )
        {
            if ( category.getCode( ).equals( strCode ) )
            {
                return category;
            }
        }
        return null;
    }

    /**
     * Get a JSON Object of the tree
     *
     * @param selectedRootCategory
     *            The selected Root Category
     * @param selectedChildCategory
     *            The selected Child Category
     *
     * @return the JSON Object of the tree
     */
    public String getTreeJSONObject( int selectedRootCategory, int selectedChildCategory )
    {
        JSONObject json = new JSONObject( );

        JSONArray jsonRootElements = new JSONArray( );

        for ( TicketCategory ticketCategory : getRootElements( ) )
        {
            JSONObject jsonRootElement = new JSONObject( );
            jsonRootElement.accumulate( FormatConstants.KEY_ID, ticketCategory.getId( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_LABEL, ticketCategory.getLabel( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_HELP, ticketCategory.getHelpMessage( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_DEPTH, ticketCategory.getDepth( ).getDepthNumber( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_INACTIVE, ticketCategory.isInactive( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_ICON, ticketCategory.getIconFont( ) );
            if ( ( selectedRootCategory > 0 ) && ( ticketCategory.getId( ) == selectedRootCategory ) )
            {
                jsonRootElement.accumulate( FormatConstants.KEY_SELECTED, true );
            }
            addJSONArraysChildren( jsonRootElement, ticketCategory, selectedChildCategory );
            jsonRootElements.add( jsonRootElement );
        }

        json.accumulate( FormatConstants.KEY_CATEGORIES_DEPTH + "1", jsonRootElements );

        JSONArray jsonDepths = new JSONArray( );

        for ( TicketCategoryType ticketCategoryType : TicketCategoryTypeHome.getCategoryTypesList( ) )
        {
            JSONObject jsonDepth = new JSONObject( );
            jsonDepth.accumulate( FormatConstants.KEY_DEPTH_NUMBER, ticketCategoryType.getDepthNumber( ) );
            jsonDepth.accumulate( FormatConstants.KEY_LABEL, ticketCategoryType.getLabel( ) );
            jsonDepths.add( jsonDepth );
        }

        json.accumulate( FormatConstants.KEY_CATEGORIES_DEPTHS, jsonDepths );

        return json.toString( );
    }

    /**
     * Get a JSON Object of the tree
     *
     * @return the JSON Object of the tree
     */
    public String getTreeJSONObject( )
    {
        return getTreeJSONObject( 0, 0 );
    }

    /**
     * Add a JSON Array of the ticketCategory children
     *
     * @param jsonRootElement
     *            json RootElement
     *
     * @param ticketCategory
     *            the current ticketCategory
     * @param selectedChildCategory
     *            the selected Child Category
     */
    public void addJSONArraysChildren( JSONObject jsonRootElement, TicketCategory ticketCategory, int selectedChildCategory )
    {
        JSONArray jsonElements = new JSONArray( );
        int nDepthChildren = ticketCategory.getDepth( ).getDepthNumber( ) + 1;

        for ( TicketCategory children : ticketCategory.getChildren( ) )
        {
            JSONObject jsonElement = new JSONObject( );
            jsonElement.accumulate( FormatConstants.KEY_ID, children.getId( ) );
            jsonElement.accumulate( FormatConstants.KEY_LABEL, children.getLabel( ) );
            jsonElement.accumulate( FormatConstants.KEY_DEPTH, children.getDepth( ).getDepthNumber( ) );
            jsonElement.accumulate( FormatConstants.KEY_HELP, children.getHelpMessage( ) );
            jsonElement.accumulate( FormatConstants.KEY_INACTIVE, children.isInactive( ) );
            if ( ( selectedChildCategory > 0 ) && ( children.getId( ) == selectedChildCategory ) )
            {
                jsonElement.accumulate( FormatConstants.KEY_SELECTED, true );
            }
            addJSONArraysChildren( jsonElement, children, selectedChildCategory );
            jsonElements.add( jsonElement );
        }

        jsonRootElement.accumulate( FormatConstants.KEY_CATEGORIES_DEPTH + nDepthChildren, jsonElements );
    }

}
