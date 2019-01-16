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
package fr.paris.lutece.plugins.ticketing.business.form;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is the business class for the object FormEntry
 */
public class FormEntry implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    private int _nIdForm;

    @NotEmpty( message = "#i18n{ticketing.validation.formentry.IdChamp.notEmpty}" )
    @Size( max = 50, message = "#i18n{ticketing.validation.formentry.IdChamp.size}" )
    private String _strIdChamp;

    private boolean _bHidden;

    private boolean _bMandatory;

    private int _nHierarchy;

    @Size( max = 500, message = "#i18n{ticketing.validation.formentry.DefaultValue.size}" )
    private String _strDefaultValue;

    /**
     * 
     */
    public FormEntry( )
    {
        super( );
    }

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
     * Returns the IdForm
     * 
     * @return The IdForm
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * Sets the IdForm
     * 
     * @param nIdForm
     *            The IdForm
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
    }

    /**
     * Returns the IdChamp
     * 
     * @return The IdChamp
     */
    public String getIdChamp( )
    {
        return _strIdChamp;
    }

    /**
     * Sets the IdChamp
     * 
     * @param strIdChamp
     *            The IdChamp
     */
    public void setIdChamp( String strIdChamp )
    {
        _strIdChamp = strIdChamp;
    }

    /**
     * Returns the Hidden
     * 
     * @return The Hidden
     */
    public boolean isHidden( )
    {
        return _bHidden;
    }

    /**
     * Sets the Hidden
     * 
     * @param bHidden
     *            The Hidden
     */
    public void setHidden( boolean bHidden )
    {
        _bHidden = bHidden;
    }

    /**
     * Returns the Mandatory
     * 
     * @return The Mandatory
     */
    public boolean isMandatory( )
    {
        return _bMandatory;
    }

    /**
     * Sets the Mandatory
     * 
     * @param bMandatory
     *            The Mandatory
     */
    public void setMandatory( boolean bMandatory )
    {
        _bMandatory = bMandatory;
    }

    /**
     * Returns the Order
     * 
     * @return The Order
     */
    public int getHierarchy( )
    {
        return _nHierarchy;
    }

    /**
     * Sets the Order
     * 
     * @param nOrder
     *            The Order
     */
    public void setHierarchy( int nOrder )
    {
        _nHierarchy = nOrder;
    }

    public void setDefaultValue( String defaultValue )
    {
        _strDefaultValue = defaultValue;
    }

    public String getDefaultValue( )
    {
        return _strDefaultValue;
    }
}
