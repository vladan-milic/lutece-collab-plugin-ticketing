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
import java.util.List;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is the business class for the object Form
 */
public class Form implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int               _nId;
    @NotEmpty( message = "#i18n{ticketing.validation.form.Title.notEmpty}" )
    @Size( max = 500, message = "#i18n{ticketing.validation.form.Title.size}" )
    private String            _strTitle;
    @Size( max = 500, message = "#i18n{ticketing.validation.form.Message.size}" )
    private String            _strMessage;
    @NotEmpty( message = "#i18n{ticketing.validation.form.ButtonLabel.notEmpty}" )
    @Size( max = 500, message = "#i18n{ticketing.validation.form.ButtonLabel.size}" )
    private String            _strButtonLabel;

    private boolean           _bConnection     = true;
    private List<FormEntry>   formEntries;

    private boolean           _bSelected       = false;

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
     * Returns the Message
     * 
     * @return The Message
     */
    public String getMessage( )
    {
        return _strMessage;
    }

    /**
     * Sets the Message
     * 
     * @param strMessage
     *            The Message
     */
    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    /**
     * Returns the ButtonLabel
     * 
     * @return The ButtonLabel
     */
    public String getButtonLabel( )
    {
        return _strButtonLabel;
    }

    /**
     * Sets the ButtonLabel
     * 
     * @param strButtonLabel
     *            The ButtonLabel
     */
    public void setButtonLabel( String strButtonLabel )
    {
        _strButtonLabel = strButtonLabel;
    }

    /**
     * Check if this entry is mandatory or not
     * 
     * @return true if the question is mandatory
     */
    public boolean isConnection( )
    {
        return _bConnection;
    }

    /**
     * Sets the Connection
     * 
     * @param bConnection
     *            The Connection
     */
    public void setConnection( boolean bConnection )
    {
        _bConnection = bConnection;
    }

    public FormEntry getEntry( String entryName )
    {
        FormEntry formEntry = getFormEntries( ).stream( ).filter( entry -> entry.getIdChamp( ).equals( entryName ) ).findFirst( ).orElse( null );
        if ( formEntry == null )
        {
            formEntry = new FormEntry( );
            // By default, a field is hidden and optional
            formEntry.setHidden( true );
            formEntry.setMandatory( false );
            formEntry.setIdChamp( entryName );
        }
        return formEntry;
    }

    public List<FormEntry> getFormEntries( )
    {
        if ( this.formEntries == null || this.formEntries.isEmpty( ) )
        {
            formEntries = FormEntryHome.findByFormId( _nId );
        }

        return formEntries;
    }

    /**
     * @param formEntries
     *            the formEntries to set
     */
    public void setFormEntries( List<FormEntry> formEntries )
    {
        this.formEntries = formEntries;
    }

    public boolean isSelected( )
    {
        return _bSelected;
    }

    public void setSelected( boolean selected )
    {
        _bSelected = selected;
    }

}
