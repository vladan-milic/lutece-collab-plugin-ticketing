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
package fr.paris.lutece.plugins.ticketing.business.supportentity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * SupportEntity
 */
public class SupportEntity implements RBACResource
{
    // RBAC management
    public static final String RESOURCE_TYPE = "SUPPORT_ENTITY";

    // Variables declarations
    private int _nId;
    private AssigneeUser _user;
    @NotNull( message = "#i18n{ticketing.validation.supportentity.unit.notNull}" )
    private AssigneeUnit _unit;

    // Can actually be any category
    private TicketCategory _ticketDomain;
    @NotEmpty( message = "#i18n{ticketing.validation.supportentity.name.notEmpty}" )
    @Size( max = 50, message = "#i18n{ticketing.validation.supportentity.name.size}" )
    private String _strName;
    private SupportLevel _supportLevel;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * @return the _user
     */
    public AssigneeUser getUser( )
    {
        return _user;
    }

    /**
     * @param user
     *            the _user to set
     */
    public void setUser( AssigneeUser user )
    {
        this._user = user;
    }

    /**
     * @return the _unit
     */
    public AssigneeUnit getUnit( )
    {
        return _unit;
    }

    /**
     * @param unit
     *            the unit to set
     */
    public void setUnit( AssigneeUnit unit )
    {
        this._unit = unit;
    }

    /**
     * @return the ticketDomain
     */
    public TicketCategory getTicketDomain( )
    {
        return _ticketDomain;
    }

    /**
     * @param ticketDomain
     *            the _ticketDomain to set
     */
    public void setTicketDomain( TicketCategory ticketDomain )
    {
        _ticketDomain = ticketDomain;
    }

    /**
     * @return the supportLevel
     */
    public SupportLevel getSupportLevel( )
    {
        return _supportLevel;
    }

    /**
     * @param supportLevel
     *            the _supportLevel to set
     */
    public void setSupportLevel( SupportLevel supportLevel )
    {
        this._supportLevel = supportLevel;
    }

    /**
     * Returns the Name
     * 
     * @return The Name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Sets the Name
     * 
     * @param strName
     *            The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    @Override
    public String getResourceId( )
    {
        return String.valueOf( _nId );
    }

    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    public void setTicketCategory( TicketCategory ticketCategory )
    {
        this._ticketDomain = ticketCategory;
    }

    /**
     * Get a JSON Object of the branch
     * 
     * @return the JSON Object of the branch
     */
    public String getBranchJSONObject( )
    {
        JSONArray jsonBranchCategories = new JSONArray( );

        for ( TicketCategory ticketCategory : TicketCategoryService.getInstance( ).getCategoriesTree( ).getBranch( _ticketDomain ) )
        {
            JSONObject jsonTicketCategory = new JSONObject( );
            jsonTicketCategory.accumulate( FormatConstants.KEY_ID, ticketCategory.getId( ) );
            jsonTicketCategory.accumulate( FormatConstants.KEY_DEPTH_NUMBER, ticketCategory.getDepth( ).getDepthNumber( ) );
            jsonBranchCategories.add( jsonTicketCategory );
        }

        return jsonBranchCategories.toString( );
    }

}
