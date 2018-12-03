/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.web.util;

import java.util.List;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;

/**
 * Result representation of a TicketCategory validation
 */
public class TicketCategoryValidatorResult
{
    private TicketCategory _ticketCategory;
    private TicketCategory _ticketCategoryParent;
    private boolean        _ticketCategoryValid;
    private List<String>   _listValidationErrors;

    /**
     * Constructor
     *
     * @param ticketCategory
     *            the ticketCategory to set
     * @param ticketCategoryParent
     * @param ticketCategoryValid
     *            the ticketCategoryValid to set
     * @param listValidationErrors
     */
    public TicketCategoryValidatorResult( TicketCategory ticketCategory, TicketCategory ticketCategoryParent, boolean ticketCategoryValid, List<String> listValidationErrors )
    {
        _ticketCategory = ticketCategory;
        _ticketCategoryParent = ticketCategoryParent;
        _ticketCategoryValid = ticketCategoryValid;
        _listValidationErrors = listValidationErrors;
    }

    /**
     * Return the ticketCategory
     *
     * @return the _ticketCategory
     */
    public TicketCategory getTicketCategory( )
    {
        return _ticketCategory;
    }

    /**
     * Set the ticketCategory
     *
     * @param ticketCategory
     *            the ticketCategory to set
     */
    public void setTicketCategory( TicketCategory ticketCategory )
    {
        _ticketCategory = ticketCategory;
    }

    /**
     * Return the ticketCategoryParent
     *
     * @return the _ticketCategoryParent
     */
    public TicketCategory getTicketCategoryParent( )
    {
        return _ticketCategoryParent;
    }

    /**
     * Set the ticketCategoryParent
     *
     * @param ticketCategoryParent
     *            the ticketCategoryParent to set
     */
    public void setTicketCategoryParent( TicketCategory ticketCategoryParent )
    {
        _ticketCategoryParent = ticketCategoryParent;
    }

    /**
     * Return the _ticketCategoryValid
     *
     * @return the _ticketCategoryValid
     */
    public boolean isTicketCategoryValid( )
    {
        return _ticketCategoryValid;
    }

    /**
     * Set the ticketCategoryValid
     *
     * @param ticketCategoryValid
     *            the ticketCategoryValid to set
     */
    public void setTicketCategoryValid( boolean ticketCategoryValid )
    {
        _ticketCategoryValid = ticketCategoryValid;
    }

    /**
     * Return the listValidationErrors
     *
     * @return the _listValidationErrors
     */
    public List<String> getListValidationErrors( )
    {
        return _listValidationErrors;
    }

    /**
     * Set the _listValidationErrors
     *
     * @param listValidationErrors
     *            the listValidationErrors to set
     */
    public void setListValidationErrors( List<String> listValidationErrors )
    {
        _listValidationErrors = listValidationErrors;
    }

}
