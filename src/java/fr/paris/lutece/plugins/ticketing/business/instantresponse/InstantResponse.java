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
package fr.paris.lutece.plugins.ticketing.business.instantresponse;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.format.FormatConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * This is the business class for the object InstantResponse
 */
public class InstantResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int               _nId;
    private String            _strSubject;
    private TicketCategory    _ticketCategory;
    private Timestamp         _dDateCreate;
    private int               _nIdAdminUser;
    private String            _strUserFirstname;
    private String            _strUserLastname;
    private int               _nIdUnit;
    private String            _strUnit;
    private int               _nIdChannel;

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
     * Returns the Ticket Category
     * 
     * @return The TicketCategory
     */
    public TicketCategory getTicketCategory( )
    {
        return _ticketCategory;
    }

    /**
     * Returns the TicketType
     * 
     * @return The TicketType
     */
    public TicketCategory getTicketType( )
    {
        return TicketCategoryService.getInstance( ).getType( _ticketCategory );
    }

    /**
     * Returns the TicketDomain
     * 
     * @return The TicketDomain
     */
    public TicketCategory getTicketDomain( )
    {
        return TicketCategoryService.getInstance( ).getDomain( _ticketCategory );
    }

    /**
     * Returns the Ticket Thematic
     * 
     * @return The TicketCategory
     */
    public TicketCategory getTicketThematic( )
    {
        return TicketCategoryService.getInstance( ).getThematic( _ticketCategory );
    }

    /**
     * Returns the Ticket Category precision
     * 
     * @return The TicketCategory
     */
    public TicketCategory getTicketPrecision( )
    {
        return TicketCategoryService.getInstance( ).getPrecision( _ticketCategory );
    }

    /**
     * Sets the TicketCategory
     * 
     * @param ticketCategory
     *            The TicketCategory
     */
    public void setTicketCategory( TicketCategory ticketCategory )
    {
        _ticketCategory = ticketCategory;
    }

    /**
     * Get a JSON Object of the branch
     * 
     * @return the JSON Object of the branch
     */
    public String getBranchJSONObject( )
    {
        JSONArray jsonBranchCategories = new JSONArray( );

        for ( TicketCategory ticketCategory : TicketCategoryService.getInstance( ).getCategoriesTree( ).getBranch( _ticketCategory ) )
        {
            JSONObject jsonTicketCategory = new JSONObject( );
            jsonTicketCategory.accumulate( FormatConstants.KEY_ID, ticketCategory.getId( ) );
            jsonTicketCategory.accumulate( FormatConstants.KEY_DEPTH_NUMBER, ticketCategory.getDepth( ).getDepthNumber( ) );
            jsonBranchCategories.add( jsonTicketCategory );
        }

        return jsonBranchCategories.toString( );
    }

    /**
     * Get the branch of the ticketCategory
     * 
     * @return the TicketCategory list corresponding to the branch
     */
    public List<TicketCategory> getBranch( )
    {
        return TicketCategoryService.getInstance( ).getCategoriesTree( ).getBranch( _ticketCategory );
    }

    /**
     * Returns the Subject
     * 
     * @return The Subject
     */
    public String getSubject( )
    {
        return _strSubject;
    }

    /**
     * Sets the Subject
     * 
     * @param strSubject
     *            The Subject
     */
    public void setSubject( String strSubject )
    {
        _strSubject = strSubject;
    }

    /**
     * Returns the IdAdminUser
     * 
     * @return The IdAdminUser
     */
    public int getIdAdminUser( )
    {
        return _nIdAdminUser;
    }

    /**
     * Sets the IdAdminUser
     * 
     * @param nIdAdminUser
     *            The IdAdminUser
     */
    public void setIdAdminUser( int nIdAdminUser )
    {
        _nIdAdminUser = nIdAdminUser;
    }

    /**
     * Returns the UserFirstname
     * 
     * @return The UserFirstname
     */
    public String getUserFirstname( )
    {
        return _strUserFirstname;
    }

    /**
     * Sets the UserFirstname
     * 
     * @param strUserFirstname
     *            The UserFirstname
     */
    public void setUserFirstname( String strUserFirstname )
    {
        _strUserFirstname = strUserFirstname;
    }

    /**
     * Returns the UserLastname
     * 
     * @return The UserLastname
     */
    public String getUserLastname( )
    {
        return _strUserLastname;
    }

    /**
     * Sets the UserLastname
     * 
     * @param strUserLastname
     *            The UserLastname
     */
    public void setUserLastname( String strUserLastname )
    {
        _strUserLastname = strUserLastname;
    }

    /**
     * Returns the IdUnit
     * 
     * @return The IdUnit
     */
    public int getIdUnit( )
    {
        return _nIdUnit;
    }

    /**
     * Sets the IdUnit
     * 
     * @param nIdUnit
     *            The IdUnit
     */
    public void setIdUnit( int nIdUnit )
    {
        _nIdUnit = nIdUnit;
    }

    /**
     * Returns the Unit
     * 
     * @return The Unit
     */
    public String getUnit( )
    {
        return _strUnit;
    }

    /**
     * Sets the Unit
     * 
     * @param strUnit
     *            The Unit
     */
    public void setUnit( String strUnit )
    {
        _strUnit = strUnit;
    }

    /**
     * Gets the create date
     * 
     * @return the create date
     */
    public Timestamp getDateCreate( )
    {
        return _dDateCreate;
    }

    /**
     * Sets the create date
     * 
     * @param dDateCreate
     *            the create date
     */
    public void setDateCreate( Timestamp dDateCreate )
    {
        _dDateCreate = dDateCreate;
    }

    /**
     * Returns the IdChannel
     * 
     * @return The IdChannel
     */
    public int getIdChannel( )
    {
        return _nIdChannel;
    }

    /**
     * Sets the IdChannel
     * 
     * @param nIdChannel
     *            The IdChannel
     */
    public void setIdChannel( int nIdChannel )
    {
        _nIdChannel = nIdChannel;
    }
}
