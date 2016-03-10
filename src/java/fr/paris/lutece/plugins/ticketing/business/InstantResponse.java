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
package fr.paris.lutece.plugins.ticketing.business;

import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;

import java.io.Serializable;


/**
 * This is the business class for the object InstantResponse
 */ 
public class InstantResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private int _nId;
    private int _nIdTicketCategory;
    private String _strSubject;
    private int _nIdAdminUser;
    private String _strType;
    private String _strDomain;
    private String _strCategory;
    private String _strUserFirstname;
    private String _strUserLastname;
    
    
    /**
     * Returns the Id
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * @param nId The Id
     */ 
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdTicketCategory
     * @return The IdTicketCategory
     */
    public int getIdTicketCategory( )
    {
        return _nIdTicketCategory;
    }

    /**
     * Sets the IdTicketCategory
     * @param nIdTicketCategory The IdTicketCategory
     */ 
    public void setIdTicketCategory( int nIdTicketCategory )
    {
        _nIdTicketCategory = nIdTicketCategory;
    }
    /**
     * Returns the Subject
     * @return The Subject
     */
    public String getSubject( )
    {
        return _strSubject;
    }

    /**
     * Sets the Subject
     * @param strSubject The Subject
     */ 
    public void setSubject( String strSubject )
    {
        _strSubject = strSubject;
    }
    /**
     * Returns the IdAdminUser
     * @return The IdAdminUser
     */
    public int getIdAdminUser( )
    {
        return _nIdAdminUser;
    }

    /**
     * Sets the IdAdminUser
     * @param nIdAdminUser The IdAdminUser
     */ 
    public void setIdAdminUser( int nIdAdminUser )
    {
        _nIdAdminUser = nIdAdminUser;
    }
       /**
        * Returns the Type
        * @return The Type
        */ 
    public String getType()
    {
        return _strType;
    }
    
       /**
        * Sets the Type
        * @param strType The Type
        */ 
    public void setType( String strType )
    {
        _strType = strType;
    }
    
       /**
        * Returns the Domain
        * @return The Domain
        */ 
    public String getDomain()
    {
        return _strDomain;
    }
    
       /**
        * Sets the Domain
        * @param strDomain The Domain
        */ 
    public void setDomain( String strDomain )
    {
        _strDomain = strDomain;
    }
    
       /**
        * Returns the Category
        * @return The Category
        */ 
    public String getCategory()
    {
        return _strCategory;
    }
    
       /**
        * Sets the Category
        * @param strCategory The Category
        */ 
    public void setCategory( String strCategory )
    {
        _strCategory = strCategory;
    }
       /**
        * Returns the UserFirstname
        * @return The UserFirstname
        */ 
    public String getUserFirstname()
    {
        return _strUserFirstname;
    }
    
       /**
        * Sets the UserFirstname
        * @param strUserFirstname The UserFirstname
        */ 
    public void setUserFirstname( String strUserFirstname )
    {
        _strUserFirstname = strUserFirstname;
    }
    
       /**
        * Returns the UserLastname
        * @return The UserLastname
        */ 
    public String getUserLastname()
    {
        return _strUserLastname;
    }
    
       /**
        * Sets the UserLastname
        * @param strUserLastname The UserLastname
        */ 
    public void setUserLastname( String strUserLastname )
    {
        _strUserLastname = strUserLastname;
    }
}
