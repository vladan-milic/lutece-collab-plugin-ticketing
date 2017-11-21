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

import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.plugins.ticketing.service.tree.Tree;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryType;
import fr.paris.lutece.plugins.ticketing.business.categoryinputs.TicketCategoryInputsHome;

import java.util.List;

public class TicketCategoryTree extends Tree<TicketCategory, TicketCategoryType> 
{
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
        listCategory.stream( ).forEach( ( category ) -> 
            category.setListIdInput( TicketCategoryInputsHome.getIdInputListByCategory( category.getId( ) ) ) );
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
     * @return the JSON Object of the tree
     */
    public String getTreeJSONObject( )
    {
        JSONObject json = new JSONObject( );
        
        JSONArray jsonRootElements = new JSONArray( );

        for ( TicketCategory ticketCategory : this.getRootElements( ) )
        {
            JSONObject jsonRootElement = new JSONObject( );
            jsonRootElement.accumulate( FormatConstants.KEY_ID, ticketCategory.getId( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_LABEL, ticketCategory.getLabel( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_HELP, ticketCategory.getHelpMessage( ) );
            jsonRootElement.accumulate( FormatConstants.KEY_DEPTH, ticketCategory.getDepth( ).getDepthNumber( ) );
            addJSONArraysChildren( jsonRootElement, ticketCategory );
            jsonRootElements.add( jsonRootElement );
        }
        
        json.accumulate( FormatConstants.KEY_CATEGORIES_DEPTH + "1", jsonRootElements );

        JSONArray jsonDepths = new JSONArray( );

        for ( TicketCategoryType ticketCategoryType : this.getDepths( ) )
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
     * Add a JSON Array of the ticketCategory children
     * 
     * @param ticketCategory
     *            the current ticketCategory
     */
    public void addJSONArraysChildren( JSONObject jsonRootElement, TicketCategory ticketCategory)
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
            addJSONArraysChildren( jsonElement, children );
            jsonElements.add( jsonElement );
        }
        
        jsonRootElement.accumulate( FormatConstants.KEY_CATEGORIES_DEPTH + nDepthChildren, jsonElements );
    }
    
}
