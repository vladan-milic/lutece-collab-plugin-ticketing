/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.web.ticketfilter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.InstantResponseFilter;
import fr.paris.lutece.plugins.ticketing.business.TicketFilter;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;


/**
 * Helper class used to manage InstantMessageFilter
 *
 * @author s267533
 *
 */
public final class InstantResponseFilterHelper
{
    //parameters
    private static final String PARAMETER_FILTER_ORDER_SORT = "fltr_order_sort";
    private static final String PARAMETER_FILTER_ORDER_BY = "fltr_order_by";
    private static final String PARAMETER_FILTER_SUBMITTED_FORM = "submitted_form";

    //Marks
    private static final String MARK_INSTANTRESPONSE_FILTER = "instantresponse_filter";

    /**
     * private constructor
     */
    private InstantResponseFilterHelper(  )
    {
        super(  );
    }

    /**
     * returns a fltrFiltre instancied from a request
     *
     * @param request request
     * @return TicketFilter fltrFiltre initialised from request parameters
     * @throws ParseException
     *             if date is not well formated
     */
    public static InstantResponseFilter getFilterFromRequest( HttpServletRequest request )
    {
        InstantResponseFilter fltrFiltre = new InstantResponseFilter(  );

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ORDER_BY ) ) )
        {
            fltrFiltre.setOrderBy( request.getParameter( PARAMETER_FILTER_ORDER_BY ) );
        }

        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ORDER_SORT ) ) )
        {
            fltrFiltre.setOrderSort( request.getParameter( PARAMETER_FILTER_ORDER_SORT ) );
        }

        return fltrFiltre;
    }

    /**
     * return filter from request / session
     * @param request http servlet request
     * @return filter
     */
    public static InstantResponseFilter getFilter( HttpServletRequest request )
    {
        InstantResponseFilter filter = null;

        if ( StringUtils.isEmpty( request.getParameter( PARAMETER_FILTER_SUBMITTED_FORM ) ) )
        {
            if ( request.getSession(  ).getAttribute( TicketingConstants.SESSION_INSTANTRESPONSE_FILTER ) != null )
            {
                filter = (InstantResponseFilter) request.getSession(  )
                                                        .getAttribute( TicketingConstants.SESSION_INSTANTRESPONSE_FILTER );
            }
            else
            {
                filter = new InstantResponseFilter(  );
                //set default order by Creation
                filter.setOrderBy( TicketFilter.CONSTANT_DEFAULT_ORDER_BY );
                filter.setOrderSort( TicketFilter.OrderSortAllowed.DESC.name(  ) );
                request.getSession(  ).setAttribute( TicketingConstants.SESSION_INSTANTRESPONSE_FILTER, filter );
            }
        }
        else
        {
            filter = InstantResponseFilterHelper.getFilterFromRequest( request );
            request.getSession(  ).setAttribute( TicketingConstants.SESSION_INSTANTRESPONSE_FILTER, filter );
        }

        return filter;
    }

    /**
     * set filter param to model
     * @param mapModel model to update
     * @param fltrFilter filter attribute to set to model
     * @param request http request
     */
    public static void setModel( Map<String, Object> mapModel, InstantResponseFilter fltrFilter, HttpServletRequest request )
    {
        mapModel.put( MARK_INSTANTRESPONSE_FILTER, fltrFilter );
    }
}
