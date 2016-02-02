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

import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This is the business class for the object TicketCategory
 */
public class TicketCategory implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private int _nId;
    private int _nIdTicketDomain;
    private int _nIdTicketType;
    private String _strTicketDomain;
    private String _strTicketType;
    private int _nIdTicketForm;
    @NotEmpty( message = "#i18n{ticketing.validation.ticketcategory.label.notEmpty}" )
    @Size( max = 50, message = "#i18n{ticketing.validation.ticketcategory.label.size}" )
    private String _strLabel;
    private String _strCode;
    private int _nIdWorkflow;
    @NotNull( message = "#i18n{ticketing.validation.ticketcategory.unit.notEmpty}" )
    private AssigneeUnit _unit;

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
     * Returns the IdTicketDomain
     * @return The IdTicketDomain
     */
    public int getIdTicketDomain(  )
    {
        return _nIdTicketDomain;
    }

    /**
     * Sets the IdTicketDomain
     * @param nIdTicketDomain The IdTicketDomain
     */
    public void setIdTicketDomain( int nIdTicketDomain )
    {
        _nIdTicketDomain = nIdTicketDomain;
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
     * Returns the TicketDomain
     * @return The TicketDomain
     */
    public String getTicketDomain(  )
    {
        return _strTicketDomain;
    }

    /**
     * Sets the TicketDomain
     * @param strTicketDomain The TicketDomain
     */
    public void setTicketDomain( String strTicketDomain )
    {
        _strTicketDomain = strTicketDomain;
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
     * Returns the Label
     * @return The Label
     */
    public String getLabel(  )
    {
        return _strLabel;
    }

    /**
     * Sets the Label
     * @param strLabel The Label
     */
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }

    /**
     * Get the id of the workflow associated with this ticket category
     * @return The id of the workflow
     */
    public int getIdWorkflow(  )
    {
        return _nIdWorkflow;
    }

    /**
     * Set the id of the workflow associated with this ticket category
     * @param nIdWorkflow The id of the workflow
     */
    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }

    /**
     * @return the _nIdTicketForm
     */
    public int getIdTicketForm(  )
    {
        return _nIdTicketForm;
    }

    /**
     * @param nIdTicketForm
     *            the _nIdTicketForm to set
     */
    public void setIdTicketForm( int nIdTicketForm )
    {
        this._nIdTicketForm = nIdTicketForm;
    }

    /**
     * Returns the Code
     * @return The Code
     */
    public String getCode(  )
    {
        return _strCode;
    }

    /**
     * Sets the Code
     * @param strCode The Code
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }

    /**
     * Returns the AssigneeUnit
     * @return The AssigneeUnit
     */
    public AssigneeUnit getAssigneeUnit(  )
    {
        return _unit;
    }

    /**
     * Sets the assigneeUnit
     * @param assigneeUnit The assigneeUnit
     */
    public void setAssigneeUnit( AssigneeUnit assigneeUnit )
    {
        _unit = assigneeUnit;
    }
}
