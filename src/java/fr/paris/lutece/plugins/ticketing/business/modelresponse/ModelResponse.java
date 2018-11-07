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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is the business class for the object TypeResponse
 */
public class ModelResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int               _nId;
    @NotEmpty( message = "#i18n{ticketing.validation.modelresponse.Title.notEmpty}" )
    @Size( max = 500, message = "#i18n{ticketing.validation.modelresponse.Title.size}" )
    private String            _strTitle;
    @NotEmpty( message = "#i18n{ticketing.validation.modelresponse.Reponse.notEmpty}" )
    @Size( max = 60000, message = "#i18n{ticketing.validation.modelresponse.Reponse.size}" )
    private String            _strReponse;
    @Size( max = 1000, message = "#i18n{ticketing.validation.modelresponse.Keyword.size}" )
    @NotEmpty( message = "#i18n{ticketing.validation.modelresponse.Keyword.notEmpty}" )
    private String            _strKeyword;
    private String            _strDomain;
    private String            _strDomainLabel;
    private String            _strDateUpdate;
    private String            _strLastName;
    private String            _strFirstName;
    private String            _strInfos;

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
     * Returns the Title
     *
     * @return The Title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     *
     * @param strTitle
     *            The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the Reponse
     *
     * @return The Reponse
     */
    public String getReponse( )
    {
        return _strReponse;
    }

    /**
     * Sets the Reponse
     *
     * @param strReponse
     *            The Reponse
     */
    public void setReponse( String strReponse )
    {
        _strReponse = strReponse;
    }

    /**
     * Returns the Domain
     *
     * @return The Domain
     */
    public String getDomain( )
    {
        return _strDomain;
    }

    /**
     * Sets the Domain
     *
     * @param strDomain
     *            The Domain
     */
    public void setDomain( String strDomain )
    {
        _strDomain = strDomain;
    }

    public String getDomainLabel( )
    {
        return _strDomainLabel;
    }

    public void setDomainLabel( String domainLabel )
    {
        _strDomainLabel = domainLabel;
    }

    /**
     * Returns the Keyword
     *
     * @return The Keyword
     */
    public String getKeyword( )
    {
        return _strKeyword;
    }

    /**
     * Sets the Keyword
     *
     * @param strKeyword
     *            The Keyword
     */
    public void setKeyword( String strKeyword )
    {
        _strKeyword = strKeyword;
    }

    @Override
    public String toString( )
    {
        return "ModelResponse{" + "_nId=" + _nId + '}';
    }

    /**
     * @return the _strLastName
     */
    public String getLastName( )
    {
        return _strLastName;
    }

    /**
     * @param _strLastName
     *            the _strLastName to set
     */
    public void setLastName( String _strLastName )
    {
        this._strLastName = _strLastName;
    }

    /**
     * @return the _strFirstName
     */
    public String getFirstName( )
    {
        return _strFirstName;
    }

    /**
     * @param _strFirstName
     *            the _strFirstName to set
     */
    public void setFirstName( String _strFirstName )
    {
        this._strFirstName = _strFirstName;
    }

    /**
     * Gets the update date
     *
     * @return the update date
     */
    public String getDateUpdate( )
    {
        return _strDateUpdate;
    }

    /**
     * Sets the update date
     *
     * @param dDateUpdate
     *            the update date
     */
    public void setDateUpdate( String _strDateUpdate )
    {
        this._strDateUpdate = _strDateUpdate;
    }

    /**
     * @return the _strInfos
     */
    public String getInfos( )
    {

        if ( !getDateUpdate( ).isEmpty( ) && !getFirstName( ).isEmpty( ) && !getLastName( ).isEmpty( ) )
        {
            _strInfos = getDateUpdate( ) + " par " + getFirstName( ) + " " + getLastName( ).toUpperCase( );
        } else
        {
            _strInfos = "";
        }

        return _strInfos;
    }

    /**
     * @param _strInfos
     *            the _strInfos to set
     */
    public void setInfos( String _strInfos )
    {
        this._strInfos = _strInfos;
    }

    public long getDateUpdateTimestamp( ) throws ParseException
    {
        if ( ( _strDateUpdate != null ) && !_strDateUpdate.isEmpty( ) )
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
            Date date = dateFormat.parse( _strDateUpdate );
            return date.getTime( );
        }
        return 0;
    }

}
