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

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;

/**
 * This class provides utility methods to validate a TicketCategory
 */
public class TicketCategoryValidator
{
    // Constants
    private static final String LOG_MESSAGE_TYPE_ID_MISSING = "Parameter domain id is missing !";

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
    public TicketCategoryValidatorResult validateTicketCategoryAndPrecision( )
    {
        // Retrieve the TicketCategory
        TicketCategory ticketCategory = retrieveTicketCategoryFromRequest( );

        // Validate if precision has been selected if the selected category has precisions
        String strIdDomain = _request.getParameter( TicketingConstants.PARAMETER_TICKET_DOMAIN_ID );
        boolean isValid = isTicketCategoryPrecisionValid( ticketCategory, TicketUtils.manageIntegerParsingFromString( strIdDomain, LOG_MESSAGE_TYPE_ID_MISSING ) );

        return new TicketCategoryValidatorResult( ticketCategory, isValid );

    }

    /**
     * Return the TicketCategory object associated to the id from the request or an "empty" ticket category object if parameter is missing
     * 
     * @return the TicketCategory object associated to the id from the request or an "empty" ticket category object if parameter is missing
     */
    public TicketCategory retrieveTicketCategoryFromRequest( )
    {
        int nIdCategory = TicketingConstants.PROPERTY_UNSET_INT;
        if ( StringUtils.isNotBlank( _request.getParameter( TicketingConstants.PARAMETER_TICKET_CATEGORY_ID ) ) )
        {
            nIdCategory = Integer.valueOf( _request.getParameter( TicketingConstants.PARAMETER_TICKET_CATEGORY_ID ) );
        }

        TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( nIdCategory );
        if ( ticketCategory == null )
        {
            ticketCategory = new TicketCategory( );
            ticketCategory.setId( TicketingConstants.PROPERTY_UNSET_INT );
            ticketCategory.setIdTicketDomain( TicketingConstants.PROPERTY_UNSET_INT );
        }
        return ticketCategory;
    }

    /**
     * Return true if precision has been selected if the selected category has precisions otherwise return false
     * 
     * @return true if precision has been selected if the selected category has precisions false otherwise
     */
    public boolean isTicketCategoryPrecisionValid( TicketCategory ticketCategory, Integer nIdDomain )
    {
        if ( ticketCategory != null && ticketCategory.getId( ) != TicketingConstants.PROPERTY_UNSET_INT )
        {
            List<TicketCategory> listTicketCategory = TicketCategoryHome.findByDomainId( nIdDomain );
            for ( TicketCategory ticketCategoryByDomain : listTicketCategory )
            {
                if ( ticketCategoryByDomain.getLabel( ).equals( ticketCategory.getLabel( ) ) && StringUtils.isNotBlank( ticketCategoryByDomain.getPrecision( ) )
                        && StringUtils.isNotBlank( _request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ) )
                        && _request.getParameter( TicketingConstants.PARAMETER_TICKET_PRECISION_ID ).equals( TicketingConstants.NO_ID_STRING ) )
                {
                    return false;
                }
            }
        }
        return true;
    }

}
