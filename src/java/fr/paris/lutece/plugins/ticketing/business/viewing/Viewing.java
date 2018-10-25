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
package fr.paris.lutece.plugins.ticketing.business.viewing;

import java.io.Serializable;

import javax.validation.constraints.Size;

/** 
 * This is the business class for the object Viewing 
 */

/**
 * @author jamel.abassou
 * 
 */
public class Viewing implements Serializable
{

    /** 
     *  
     */
    private static final long serialVersionUID = 775123631152093005L;

    // Variables declarations
    private int               _nId;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strTitle;
    @Size( max = 5000, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strMessage;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strValidationButon;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strIsChannel;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strIsContactMode;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strIsCivility;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strIsDomain;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strIsThematic;
    @Size( max = 50, message = "#i18n{ticketing.validation.viewing.Label.size}" )
    private String            _strIsLocation;

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
     * @return the _strLabelTitle
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * @param _strLabelTitle
     *            the _strLabelTitle to set
     */
    public void setTitle( String _strLabelTitle )
    {
        this._strTitle = _strLabelTitle;
    }

    /**
     * @return the _strMessage
     */
    public String getMessage( )
    {
        return _strMessage;
    }

    /**
     * @param _strMessage
     *            the _strMessage to set
     */
    public void setMessage( String _strMessage )
    {
        this._strMessage = _strMessage;
    }

    /**
     * @return the _strValidationButon
     */
    public String getValidationButon( )
    {
        return _strValidationButon;
    }

    /**
     * @param _strValidationButon
     *            the _strValidationButon to set
     */
    public void setValidationButon( String _strValidationButon )
    {
        this._strValidationButon = _strValidationButon;
    }

    /**
     * @return the _strIsChannel
     */
    public String getIsChannel( )
    {
        return _strIsChannel;
    }

    /**
     * @param _strIsChannel
     *            the _strIsChannel to set
     */
    public void setIsChannel( String _strIsChannel )
    {
        this._strIsChannel = _strIsChannel;
    }

    /**
     * @return the _strIsContactMode
     */
    public String getIsContactMode( )
    {
        return _strIsContactMode;
    }

    /**
     * @param _strIsContactMode
     *            the _strIsContactMode to set
     */
    public void setIsContactMode( String _strIsContactMode )
    {
        this._strIsContactMode = _strIsContactMode;
    }

    /**
     * @return the _strIsCivility
     */
    public String getIsCivility( )
    {
        return _strIsCivility;
    }

    /**
     * @param _strIsCivility
     *            the _strIsCivility to set
     */
    public void setIsCivility( String _strIsCivility )
    {
        this._strIsCivility = _strIsCivility;
    }

    /**
     * @return the _strIsDomain
     */
    public String getIsDomain( )
    {
        return _strIsDomain;
    }

    /**
     * @param _strIsDomain
     *            the _strIsDomain to set
     */
    public void setIsDomain( String _strIsDomain )
    {
        this._strIsDomain = _strIsDomain;
    }

    /**
     * @return the _strIsThematic
     */
    public String getIsThematic( )
    {
        return _strIsThematic;
    }

    /**
     * @param _strIsThematic
     *            the _strIsThematic to set
     */
    public void setIsThematic( String _strIsThematic )
    {
        this._strIsThematic = _strIsThematic;
    }

    /**
     * @return the _strIsLocation
     */
    public String getIsLocation( )
    {
        return _strIsLocation;
    }

    /**
     * @param _strIsLocation
     *            the _strIsLocation to set
     */
    public void setIsLocation( String _strIsLocation )
    {
        this._strIsLocation = _strIsLocation;
    }

}
