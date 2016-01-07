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
package fr.paris.lutece.plugins.ticketing.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.TicketFilter;

/**
 * Helper class used to manage TicketFilter
 * 
 * @author s267533
 *
 */
public class TicketFilterHelper
{
    private static final String PARAMETER_FILTER_ID_TICKET = "fltr_id_ticket";
    private static final String PARAMETER_FILTER_LASTUPDATE_DATE = "fltr_lastupdate";
    private static final String PARAMETER_FILTER_START_LASTUPDATE_DATE = "fltr_start_lastupdate";
    private static final String PARAMETER_FILTER_END_LASTUPDATE_DATE = "fltr_end_lastupdate";
    private static final String PARAMETER_FILTER_CREATION_DATE = "fltr_creationdate";
    private static final String PARAMETER_FILTER_CLOSE_DATE = "fltr_closedate";
    private static final String PARAMETER_FILTER_END_CREATION_DATE = "fltr_end_creationdate";
    private static final String PARAMETER_FILTER_START_CREATION_DATE = "fltr_start_creationdate";
    private static final String PARAMETER_FILTER_ID_USER = "fltr_id_user";
    private static final String PARAMETER_FILTER_ID_CATEGORY = "fltr_id_category";
    private static final String PARAMETER_FILTER_ID_DOMAIN = "fltr_id_domain";
    private static final String PARAMETER_FILTER_ID_TYPE = "fltr_id_type";
    private static final String PARAMETER_FILTER_STATUS = "fltr_status";
    private static final String PARAMETER_FILTER_ORDER_BY = "fltr_order_by";
    private static final String PARAMETER_FILTER_EMAIL = "fltr_email";
    private static final String PARAMETER_FILTER_LASTNAME = "fltr_lastname";
    private static final String PARAMETER_FILTER_FIRSTNAME = "fltr_firstname";
    private static final String PARAMETER_FILTER_FIXED_PHONE_NUMBER = "fltr_fixed_phone_number";
    private static final String PARAMETER_FILTER_MOBILE_PHONE_NUMBER = "fltr_mobile_phone_number";
    private static final String PARAMETER_FILTER_ORDER_SORT = "fltr_order_sort";

    private static final String DATE_FILTER_PATTERN = "yyyyMMdd";

    /**
     * returns a filter instancied from a request
     * 
     * @param request
     * @return TicketFilter
     * @throws ParseException
     *             if date is not well formated
     */
    public static TicketFilter getFilterFromRequest(HttpServletRequest request)
            throws ParseException
    {
        TicketFilter filter = new TicketFilter( );
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_DOMAIN ) )
                && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_DOMAIN ) ) )
        {
            filter.setIdDomain( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_DOMAIN ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ORDER_BY ) ) )
        {
            filter.setOrderBy( request.getParameter( PARAMETER_FILTER_ORDER_BY ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ORDER_SORT ) ) )
        {
            filter.setOrderSort( request.getParameter( PARAMETER_FILTER_ORDER_SORT ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_CATEGORY ) )
                && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_CATEGORY ) ) )
        {
            filter.setIdCategory( Integer.parseInt( request
                    .getParameter( PARAMETER_FILTER_ID_CATEGORY ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_USER ) )
                && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_USER ) ) )
        {
            filter.setIdUser( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_USER ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_TYPE ) )
                && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_TYPE ) ) )
        {
            filter.setIdType( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_TYPE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_ID_TICKET ) )
                && StringUtils.isNumeric( request.getParameter( PARAMETER_FILTER_ID_TICKET ) ) )
        {
            filter.setIdTicket( Integer.parseInt( request.getParameter( PARAMETER_FILTER_ID_TICKET ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_CREATION_DATE ) ) )
        {
            filter.setCreationDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_CREATION_DATE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_END_CREATION_DATE ) ) )
        {
            filter.setCreationEndDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_END_CREATION_DATE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_START_CREATION_DATE ) ) )
        {
            filter.setCreationStartDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_START_CREATION_DATE ) ) );
        }
        if ( StringUtils
                .isNotEmpty( request.getParameter( PARAMETER_FILTER_START_LASTUPDATE_DATE ) ) )
        {
            filter.setLastUpdateStartDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_START_LASTUPDATE_DATE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_END_LASTUPDATE_DATE ) ) )
        {
            filter.setLastUpdateEndDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_END_LASTUPDATE_DATE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_LASTUPDATE_DATE ) ) )
        {
            filter.setLastUpdateDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_LASTUPDATE_DATE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_CLOSE_DATE ) ) )
        {
            filter.setCloseDate( getDateFromString( request
                    .getParameter( PARAMETER_FILTER_CLOSE_DATE ) ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_STATUS ) ) )
        {
            filter.setStatus( request.getParameter( PARAMETER_FILTER_STATUS ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_EMAIL ) ) )
        {
            filter.setEmail( request.getParameter( PARAMETER_FILTER_EMAIL ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_LASTNAME ) ) )
        {
            filter.setLastName( request.getParameter( PARAMETER_FILTER_LASTNAME ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_FIRSTNAME ) ) )
        {
            filter.setFirstName( request.getParameter( PARAMETER_FILTER_FIRSTNAME ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_FIXED_PHONE_NUMBER ) ) )
        {
            filter.setFixedPhoneNumber( request.getParameter( PARAMETER_FILTER_FIXED_PHONE_NUMBER ) );
        }
        if ( StringUtils.isNotEmpty( request.getParameter( PARAMETER_FILTER_MOBILE_PHONE_NUMBER ) ) )
        {
            filter.setMobilePhoneNumber( request
                    .getParameter( PARAMETER_FILTER_MOBILE_PHONE_NUMBER ) );
        }
        return filter;
    }

    /**
     * returns a Date object from a String date String has to respect yyyyMMdd
     * format unless a parseException is thrown
     * 
     * @param strDate
     *            yyyyMMdd date
     * @return date
     * @throws ParseException
     */
    private static Date getDateFromString(String strDate) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat( DATE_FILTER_PATTERN );
        Date date = null;
        date = sdf.parse( strDate );
        return date;
    }
}
