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
package fr.paris.lutece.plugins.ticketing.web.user;

import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;

public class User
{
	// Variables declarations
    private int _nIdUser;
    private String _strFirstName;
    private String _strLastName;
    private String _strEmail;
    private List<TicketDomain> domain;
    private List<Unit> unit;
    
    
    /**
     * Returns the AdminUserId
     * @return The AdminUserId
     */
    public int getIdUser(  )
    {
        return _nIdUser;
    }
    
    /**
     * Sets the AdminUserId
     * @param nAdminUserId The AdminUserId
     */
    public void setIdUser( int _niduser )
    {
        this._nIdUser = _niduser;
    }
    
    /**
     * Returns the Firstname
     * @return The Firstname
     */
    public String getFirstName(  )
    {
        return _strFirstName;
    }
    
    /**
     * Sets the Firstname
     * @param strFirstname The Firstname
     */
    public void setFirstName( String _strFistname )
    {
        this._strFirstName = _strFistname;
    }
    
    /**
     * Returns the Lastname
     * @return The Lastname
     */
    public String getLastName(  )
    {
        return _strLastName;
    }
    
    /**
     * Sets the Lastname
     * @param strLastname The Lastname
     */
    public void setLastName( String _strLastname )
    {
        this._strLastName = _strLastname;
    }
    
    /**
     * Returns the Email
     * @return The Email
     */
    public String getEmail(  )
    {
        return _strEmail;
    }
    
    /**
     * Sets the Email
     * @param strEmail The Email
     */
    public void setEmail( String _strEmail )
    {
        this._strEmail = _strEmail;
    }

    public List<TicketDomain> getDomain(  )
    {
        return domain;
    }
    
    /**
     * Sets the Domain
     * @param list of Domain
     */
    public void setDomain( List<TicketDomain> domain )
    {
        this.domain = domain;
    }

    public List<Unit> getUnit(  )
    {
        return unit;
    }
    
    /**
     * Sets the Unit
     * @param list of Unit
     */
    public void setUnit( List<Unit> unit )
    {
        this.unit = unit;
    }
}
