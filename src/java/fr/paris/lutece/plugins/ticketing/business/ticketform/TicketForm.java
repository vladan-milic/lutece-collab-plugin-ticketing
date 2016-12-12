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
package fr.paris.lutece.plugins.ticketing.business.ticketform;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

import javax.validation.constraints.Size;


/**
 * This is the business class for the object TicketForm
 */
public class TicketForm implements RBACResource, Cloneable, Serializable
{
    /**
     * Name of the resource type of Ticket Forms
     */
    public static final String RESOURCE_TYPE = "TICKET_FORM";
    private static final long serialVersionUID = 1L;

    // Variables declarations 
    private int _nIdForm;
    @NotBlank( message = "#i18n{ticketing.validation.ticketform.Title.notEmpty}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.ticketform.Title.size}" )
    private String _strTitle;
    @NotBlank( message = "#i18n{ticketing.validation.ticketform.Description.notEmpty}" )
    private String _strDescription;
    private TicketCategory _ticketCategory;

    /**
     * Returns the IdForm
     * @return The IdForm
     */
    public int getIdForm(  )
    {
        return _nIdForm;
    }

    /**
     * Sets the IdForm
     * @param nIdForm The IdForm
     */
    public void setIdForm( int nIdForm )
    {
        _nIdForm = nIdForm;
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
     * Get the description of the ticket form
     *
     * @return The description of the ticket form
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Set the description of the ticket form
     *
     * @param strDescription
     *            The description of the ticket form
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     * Get the TicketCategory of the ticket form
     *
     * @return The TicketCategory of the ticket form
     */
    public TicketCategory getTicketCategory(  )
    {
        return _ticketCategory;
    }

    /**
     * Set the TicketCategory of the ticket form
     *
     * @param TicketCategory
     *            The TicketCategory of the ticket form
     */
    public void setTicketCategory( TicketCategory ticketCategory )
    {
        _ticketCategory = ticketCategory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getResourceId(  )
    {
        return Integer.toString( getIdForm(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone(  )
    {
        try
        {
            return super.clone(  );
        }
        catch ( CloneNotSupportedException e )
        {
            AppLogService.error( e.getMessage(  ), e );

            return null;
        }
    }
}
