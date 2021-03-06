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
package fr.paris.lutece.plugins.ticketing.business.contactmode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * This is the business class for the object ContactMode
 */

/**
 * @author s235706
 *
 */
public class ContactMode implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final char COMMA = ',';

    // Variables declarations
    private int _nId;
    @NotEmpty( message = "#i18n{ticketing.validation.contactmode.code.notEmpty}" )
    @Size( max = 50, message = "#i18n{ticketing.validation.contactmode.code.size}" )
    private String _strCode;
    @Size( max = 150, message = "#i18n{ticketing.validation.contactmode.required_inputs.size}" )
    private String _strRequiredInputs;
    private String _strConfirmationMsg;

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
     * Returns the code
     * 
     * @return The code
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * Sets the code
     * 
     * @param strCode
     *            The code
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }

    /**
     * Returns the required inputs
     * 
     * @return The required inputs
     */
    public String getRequiredInputs( )
    {
        return _strRequiredInputs;
    }

    /**
     * Sets the required inputs
     * 
     * @param strRequiredInputs
     *            The RequiredInputs
     */
    public void setRequiredInputs( String strRequiredInputs )
    {
        _strRequiredInputs = strRequiredInputs;
    }

    /**
     * Returns the confirmation message
     *
     * @return The confirmation message
     */
    public String getConfirmationMsg( )
    {
        return _strConfirmationMsg;
    }

    /**
     * Sets the confirmation message
     *
     * @param strConfirmationMsg
     *            The confirmation message
     */
    public void setConfirmationMsg( String strConfirmationMsg )
    {
        _strConfirmationMsg = strConfirmationMsg;
    }

    /**
     * Returns the required inputs list
     * 
     * @return The required inputs list
     */
    public List<String> getRequiredInputsList( )
    {
        return new ArrayList<String>( Arrays.asList( StringUtils.split( _strRequiredInputs, COMMA ) ) );
    }

}
