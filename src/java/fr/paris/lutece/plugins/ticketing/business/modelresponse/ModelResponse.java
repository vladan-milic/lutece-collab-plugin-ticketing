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
package fr.paris.lutece.plugins.ticketing.business.modelresponse;

import org.hibernate.validator.constraints.*;

import java.io.Serializable;

import javax.validation.constraints.*;


/**
 * This is the business class for the object TypeResponse
 */
public class ModelResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private int _nId;
    private int _nIdTicketCategory;
    private int _nIdTicketType;
    private int _nIdDomain;

    @NotEmpty( message = "#i18n{ticketing.validation.modelresponse.Title.notEmpty}" )    
    @Size( max = 500, message = "#i18n{ticketing.validation.modelresponse.Title.size}" )
    private String _strTitle;
    
    @NotEmpty( message = "#i18n{ticketing.validation.modelresponse.Reponse.notEmpty}" )
    @Size( max = 1000, message = "#i18n{ticketing.validation.modelresponse.Reponse.size}" )
    private String _strReponse;
    
     @Size( max = 1000, message = "#i18n{ticketing.validation.modelresponse.Keyword.size}" )
    @NotEmpty( message = "#i18n{ticketing.validation.modelresponse.Keyword.notEmpty}" )
    private String _strKeyword;
     
    private String _strCategory;
    private String _strTicketType;
    private String _strDomain;

    /**
     * Returns the Id
     * @return The Id
     */
    public int getId(  )
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
    public int getIdTicketCategory(  )
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
     * Returns the Title
     * @return The Title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     * @param strTitle The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the Reponse
     * @return The Reponse
     */
    public String getReponse(  )
    {
        return _strReponse;
    }

    /**
     * Sets the Reponse
     * @param strReponse The Reponse
     */
    public void setReponse( String strReponse )
    {
        _strReponse = strReponse;
    }

    /**
     * Returns the Category
     * @return The Category
     */
    public String getCategory(  )
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
     * Returns the TicketType
     * @return The TicketType
     */
    public String getTicketType(  )
    {
        return _strTicketType;
    }

    /**
     * Sets the TicketType
     * @param strTicketType The TicketType
     */
    public void setTicketType( String strTicketType )
    {
        _strTicketType = strTicketType;
    }

    /**
     * Returns the Domain
     * @return The Domain
     */
    public String getDomain(  )
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
     * Returns the IdTicketType
     * @return The IdTicketType
     */
    public int getIdTicketType(  )
    {
        return _nIdTicketType;
    }

    /**
     * Sets the IdTicketType
     * @param nIdTicketType The IdTicketType
     */
    public void setIdTicketType( int nIdTicketType )
    {
        _nIdTicketType = nIdTicketType;
    }

    /**
     * Returns the IdDomain
     * @return The IdDomain
     */
    public int getIdDomain(  )
    {
        return _nIdDomain;
    }

    /**
     * Sets the IdDomain
     * @param nIdDomain The IdDomain
     */
    public void setIdDomain( int nIdDomain )
    {
        _nIdDomain = nIdDomain;
    }

    /**
    * Returns the Keyword
    * @return The Keyword
    */
    public String getKeyword(  )
    {
        return _strKeyword;
    }

    /**
     * Sets the Keyword
     * @param strKeyword The Keyword
     */
    public void setKeyword( String strKeyword )
    {
        _strKeyword = strKeyword;
    }

    @Override
    public String toString() {
        return "ModelResponse{" + "_nId=" + _nId + '}';
    }
    
    
}
