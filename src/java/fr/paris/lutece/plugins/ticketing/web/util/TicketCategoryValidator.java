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


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryTypeHome;
import fr.paris.lutece.plugins.ticketing.business.form.Form;
import fr.paris.lutece.plugins.ticketing.business.form.FormEntryType;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;

/**
 * This class provides utility methods to validate a TicketCategory
 */
public class TicketCategoryValidator
{
    // Variables
    private HttpServletRequest _request;

    /**
     * Constructor
     * 
     * @param request
     *            the request containing the parameter to validate
     */
    public TicketCategoryValidator( HttpServletRequest request )
    {
        this._request = request;
    }

    /**
     * Global method to validate a TicketCategory with the managing of the precision
     * 
     * @param nIdDomain
     *            The id of the domain
     * @return the TicketCategoryValidatorResult which represent the result of the validation
     */
    public TicketCategoryValidatorResult validateTicketCategory( )
    {
        return validateTicketCategory( null );
    }

    public TicketCategoryValidatorResult validateTicketCategory( Form form )
    {
        FormEntryType formEntryType = new FormEntryType( );
        List<String> listValidationErrors = new ArrayList<String>( );
        boolean isValid = true;

        int i = 1;
        int nId = -1;
        TicketCategory ticketCategoryParent = null;
        TicketCategory lastValidTicketCategory = null;
        int lastValidI = 1;

        while ( i <= TicketCategoryTypeHome.getCategoryTypesList( ).size( ) && isValid )
        {
            try
            {
                nId = Integer.valueOf( _request.getParameter( TicketingConstants.PARAMETER_CATEGORY_ID + i ) );
            }
            catch ( NumberFormatException e )
            {
                nId = -1;
            }

            TicketCategory ticketCategory = TicketCategoryService.getInstance( ).findCategoryById( nId );

            boolean ticketCategoryIsProvided = ticketCategory != null;
            boolean ticketCategoryWithoutFormIsRequired = form == null;
            boolean ticketCategoryWithFormIsRequired = form != null && form.getEntry( formEntryType.getCategory( ) + i ).isMandatory( );
            boolean ticketCategoryIsRequired = ticketCategoryWithoutFormIsRequired || ticketCategoryWithFormIsRequired;

            if ( ticketCategoryIsProvided )
            {
                lastValidTicketCategory = ticketCategory;
                lastValidI = i;
            }
            else if ( ticketCategoryIsRequired || i <= TicketingConstants.CATEGORY_DEPTH_MIN )
            {
                listValidationErrors.add( I18nService.getLocalizedString( TicketingConstants.ERROR_CATEGORY_NOT_SELECTED,
                        new String[] { TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ).get( i - 1 ).getLabel( ) }, this._request.getLocale( ) ) );
                isValid = false;
            }

            boolean ticketCategoryIsLeaf = form == null && ticketCategory != null && ticketCategory.getLeaf( );
            if ( !isValid || ticketCategoryIsLeaf )
            {
                break;
            }

            if ( ticketCategoryIsProvided )
            {
                ticketCategoryParent = ticketCategory;
            }

            i++;
        }

        if ( isValid && lastValidI < TicketingConstants.CATEGORY_DEPTH_MIN )
        {
            listValidationErrors.add( I18nService.getLocalizedString( TicketingConstants.ERROR_CATEGORY_NOT_SELECTED, new String [ ] {
                    TicketCategoryService.getInstance( ).getCategoriesTree( ).getDepths( ).get( lastValidI + 1 ).getLabel( )
            }, this._request.getLocale( ) ) );
            isValid = false;
        }

        if ( isValid && lastValidTicketCategory == null && ticketCategoryParent != null )
        {
            lastValidTicketCategory = ticketCategoryParent;
            ticketCategoryParent = lastValidTicketCategory.getParent( );
        }

        return new TicketCategoryValidatorResult( lastValidTicketCategory, ticketCategoryParent, isValid, listValidationErrors );
    }
}
