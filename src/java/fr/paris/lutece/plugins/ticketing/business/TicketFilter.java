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
package fr.paris.lutece.plugins.ticketing.business;

import org.apache.commons.lang.StringUtils;

import java.util.Date;


/**
 *
 * class TicketFilter
 *
 */
public class TicketFilter
{
    /**
     * Value for boolean filters to represent Boolean.FALSE
     */
    public static final int FILTER_FALSE = 0;

    /**
     * Value for boolean filters to represent Boolean.TRUE
     */
    public static final int FILTER_TRUE = 1;
    public static final int CONSTANT_ID_NULL = -1;

    /**
     * Default order by
     */
    public static final String CONSTANT_DEFAULT_ORDER_BY = "id_ticket";
    private static final String[] LIST_ORDER_BY = 
        {
            CONSTANT_DEFAULT_ORDER_BY, "ticket_status", "date_create", "date_update", "id_user", "id_ticket_domain",
            "id_ticket_category", "email", "lastname", "firstname", "fixed_phone_number", "mobile_phone_number",
            "date_close",
        };
    private int _nIdTicket = CONSTANT_ID_NULL;
    private Date _dateLastUpdateDate;
    private Date _dateLastUpdateStartDate;
    private Date _dateLastUpdateEndDate;
    private Date _dateCreationDate;
    private Date _dateCreationStartDate;
    private Date _dateCreationEndDate;
    private Date _dateCloseDate;
    private int _nIdUser = CONSTANT_ID_NULL;
    private int _nIdCategory = CONSTANT_ID_NULL;
    private int _nIdDomain = CONSTANT_ID_NULL;
    private int _nIdType = CONSTANT_ID_NULL;
    private String _strStatus;
    private String _strEmail;
    private String _strLastName;
    private String _strFirstName;
    private String _strFixedPhoneNumber;
    private String _strMobilePhoneNumber;
    private String _strOrderBy;
    private String _strOrderSort;

    /**
     * Check if this filter contains a idUser
     *
     * @return true if the filter contain an id of ticket
     */
    public boolean containsIdUser(  )
    {
        return ( _nIdUser != CONSTANT_ID_NULL );
    }

    /**
     * @return the _nIdUser
     */
    public int getIdUser(  )
    {
        return _nIdUser;
    }

    /**
     * @param nIdUser
     *            the nIdUser to set
     */
    public void setIdUser( int nIdUser )
    {
        this._nIdUser = nIdUser;
    }

    /**
     * @return the _nIdTicket
     */
    public int getIdTicket(  )
    {
        return _nIdTicket;
    }

    /**
     * @param nIdTicket
     *            the nIdTicket to set
     */
    public void setIdTicket( int nIdTicket )
    {
        this._nIdTicket = nIdTicket;
    }

    /**
     * Check if this filter contains a idTicket
     *
     * @return true if the filter contain an id of ticket
     */
    public boolean containsIdTicket(  )
    {
        return ( _nIdTicket != CONSTANT_ID_NULL );
    }

    /**
     * @return the _dateLastUpdateDate
     */
    public Date getLastUpdateDate(  )
    {
        return _dateLastUpdateDate;
    }

    /**
     * @param strLastUpdateDate
     *            the strLastUpdateDate to set
     */
    public void setLastUpdateDate( Date strLastUpdateDate )
    {
        this._dateLastUpdateDate = strLastUpdateDate;
    }

    /**
     * Check if this filter contains a _dateLastUpdateDate
     *
     * @return true if the filter contain _dateLastUpdateDate
     */
    public boolean containsLastUpdateDate(  )
    {
        return _dateLastUpdateDate != null;
    }

    /**
     * @return the _dateLastUpdateStartDate
     */
    public Date getLastUpdateStartDate(  )
    {
        return _dateLastUpdateStartDate;
    }

    /**
     * @param strLastUpdateStartDate
     *            the strLastUpdateStartDate to set
     */
    public void setLastUpdateStartDate( Date strLastUpdateStartDate )
    {
        this._dateLastUpdateStartDate = strLastUpdateStartDate;
    }

    /**
     * Check if this filter contains a _dateLastUpdateStartDate
     *
     * @return true if the filter contains _dateLastUpdateStartDate
     */
    public boolean containsLastUpdateStartDate(  )
    {
        return _dateLastUpdateStartDate != null;
    }

    /**
     * @return the _dateLastUpdateEndDate
     */
    public Date getLastUpdateEndDate(  )
    {
        return _dateLastUpdateEndDate;
    }

    /**
     * @param strLastUpdateEndDate
     *            the strLastUpdateEndDate to set
     */
    public void setLastUpdateEndDate( Date strLastUpdateEndDate )
    {
        this._dateLastUpdateEndDate = strLastUpdateEndDate;
    }

    /**
     * Check if this filter contains a _dateLastUpdateEndDate
     *
     * @return true if the filter contains _dateLastUpdateEndDate
     */
    public boolean containsLastUpdateEndDate(  )
    {
        return _dateLastUpdateEndDate != null;
    }

    /**
     * @return the _strCloseDate
     */
    public Date getCloseDate(  )
    {
        return _dateCloseDate;
    }

    /**
     * @param strCloseDate
     *            the strCloseDate to set
     */
    public void setCloseDate( Date strCloseDate )
    {
        this._dateCloseDate = strCloseDate;
    }

    /**
     * Check if this filter contains a _dateCloseDate
     *
     * @return true if the filter contain _dateCloseDate
     */
    public boolean containsCloseDate(  )
    {
        return _dateCloseDate != null;
    }

    /**
     * @return the _strCreationDate
     */
    public Date getCreationDate(  )
    {
        return _dateCreationDate;
    }

    /**
     * @param strCreationDate
     *            the strCreationDate to set
     */
    public void setCreationDate( Date strCreationDate )
    {
        this._dateCreationDate = strCreationDate;
    }

    /**
     * Check if this filter contains a _dateCreationDate
     *
     * @return true if the filter contain _dateCreationDate
     */
    public boolean containsCreationDate(  )
    {
        return _dateCreationDate != null;
    }

    /**
     * @return the _dateCreationStartDate
     */
    public Date getCreationStartDate(  )
    {
        return _dateCreationStartDate;
    }

    /**
     * @param strCreationStartDate
     *            the strCreationStartDate to set
     */
    public void setCreationStartDate( Date strCreationStartDate )
    {
        this._dateCreationStartDate = strCreationStartDate;
    }

    /**
     * Check if this filter contains a _dateCreationStartDate
     *
     * @return true if the filter contains _dateCreationStartDate
     */
    public boolean containsCreationStartDate(  )
    {
        return _dateCreationStartDate != null;
    }

    /**
     * @return the _dateCreationEndDate
     */
    public Date getCreationEndDate(  )
    {
        return _dateCreationEndDate;
    }

    /**
     * @param strCreationEndDate
     *            the strCreationEndDate to set
     */
    public void setCreationEndDate( Date strCreationEndDate )
    {
        this._dateCreationEndDate = strCreationEndDate;
    }

    /**
     * Check if this filter contains a _dateCreationEndDate
     *
     * @return true if the filter contains _dateCreationEndDate
     */
    public boolean containsCreationEndDate(  )
    {
        return _dateCreationEndDate != null;
    }

    /**
     * @return the _nIdCategory
     */
    public int getIdCategory(  )
    {
        return _nIdCategory;
    }

    /**
     * @param _nIdCategory
     *            the _nIdCategory to set
     */
    public void setIdCategory( int nIdCategory )
    {
        this._nIdCategory = nIdCategory;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of category
     */
    public boolean containsIdCategory(  )
    {
        return ( _nIdCategory != CONSTANT_ID_NULL );
    }

    /**
     * @return the _nIdDomain
     */
    public int getIdDomain(  )
    {
        return _nIdDomain;
    }

    /**
     * @param nIdDomain
     *            the nIdDomain to set
     */
    public void setIdDomain( int nIdDomain )
    {
        this._nIdDomain = nIdDomain;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of domain
     */
    public boolean containsIdDomain(  )
    {
        return ( _nIdDomain != CONSTANT_ID_NULL );
    }

    /**
     * @return the _nIdType
     */
    public int getIdType(  )
    {
        return _nIdType;
    }

    /**
     * @param nIdType
     *            the nIdType to set
     */
    public void setIdType( int nIdType )
    {
        this._nIdType = nIdType;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of domain
     */
    public boolean containsIdType(  )
    {
        return ( _nIdType != CONSTANT_ID_NULL );
    }

    /**
     * @return the _strStatus
     */
    public String getStatus(  )
    {
        return _strStatus;
    }

    /**
     * @param strStatus
     *            the strStatus to set
     */
    public void setStatus( String strStatus )
    {
        this._strStatus = strStatus;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of status
     */
    public boolean containsStatus(  )
    {
        return StringUtils.isNotEmpty( _strStatus );
    }

    /**
     * @return the _strEmail
     */
    public String getEmail(  )
    {
        return _strEmail;
    }

    /**
     * @param strEmail
     *            the strEmail to set
     */
    public void setEmail( String strEmail )
    {
        this._strEmail = strEmail;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of Email
     */
    public boolean containsEmail(  )
    {
        return StringUtils.isNotEmpty( _strEmail );
    }

    /**
     * @return the _strLastName
     */
    public String getLastName(  )
    {
        return _strLastName;
    }

    /**
     * @param strLastName
     *            the strLastName to set
     */
    public void setLastName( String strLastName )
    {
        this._strLastName = strLastName;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of LastName
     */
    public boolean containsLastName(  )
    {
        return StringUtils.isNotEmpty( _strLastName );
    }

    /**
     * @return the _strFirstName
     */
    public String getFirstName(  )
    {
        return _strFirstName;
    }

    /**
     * @param strFirstName
     *            the strFirstName to set
     */
    public void setFirstName( String strFirstName )
    {
        this._strFirstName = strFirstName;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of FirstName
     */
    public boolean containsFirstName(  )
    {
        return StringUtils.isNotEmpty( _strFirstName );
    }

    /**
     * @return the _strFixedPhoneNumber
     */
    public String getFixedPhoneNumber(  )
    {
        return _strFixedPhoneNumber;
    }

    /**
     * @param strFixedPhoneNumber
     *            the strFixedPhoneNumber to set
     */
    public void setFixedPhoneNumber( String strFixedPhoneNumber )
    {
        this._strFixedPhoneNumber = strFixedPhoneNumber;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of FixedPhoneNumber
     */
    public boolean containsFixedPhoneNumber(  )
    {
        return StringUtils.isNotEmpty( _strFixedPhoneNumber );
    }

    /**
     * @return the _strMobilePhoneNumber
     */
    public String getMobilePhoneNumber(  )
    {
        return _strMobilePhoneNumber;
    }

    /**
     * @param strMobilePhoneNumber
     *            the strMobilePhoneNumber to set
     */
    public void setMobilePhoneNumber( String strMobilePhoneNumber )
    {
        this._strMobilePhoneNumber = strMobilePhoneNumber;
    }

    /**
     * Check if this filter contains a resource id
     *
     * @return true if the filter contain an id of MobilePhoneNumber
     */
    public boolean containsMobilePhoneNumber(  )
    {
        return StringUtils.isNotEmpty( _strMobilePhoneNumber );
    }

    /**
     * Set the order by attribute of this filter.
     *
     * @param strOrderBy
     *            The order by attribute of this filter. If the specified order
     *            does not match with column names of the ticket table of the
     *            database, then the order by is reinitialized.
     */
    public void setOrderBy( String strOrderBy )
    {
        boolean bValidOrderBy = false;

        for ( String strOrder : LIST_ORDER_BY )
        {
            if ( StringUtils.equals( strOrder, strOrderBy ) )
            {
                bValidOrderBy = true;

                break;
            }
        }

        if ( bValidOrderBy )
        {
            this._strOrderBy = strOrderBy;
        }
        else
        {
            _strOrderBy = LIST_ORDER_BY[0];
        }
    }

    /**
     * @return the _strOrderBy
     */
    public String getOrderBy(  )
    {
        return _strOrderBy;
    }

    /**
     * Check if this filter contains a order by clause
     *
     * @return the _strOrderBy
     */
    public boolean containsOrderBy(  )
    {
        return StringUtils.isNotEmpty( _strOrderBy );
    }

    /**
     * @return the _bOrderASC
     */
    public boolean isOrderASC(  )
    {
        return OrderSortAllowed.ASC.name(  ).equalsIgnoreCase( _strOrderSort );
    }

    /**
     * Check if this filter contains a valid ordersort
     *
     * @return true if filter contains a valid ordersort
     */
    public boolean containsOrderSort(  )
    {
        boolean bResult = false;

        if ( StringUtils.isNotEmpty( _strOrderSort ) )
        {
            for ( OrderSortAllowed osa : OrderSortAllowed.values(  ) )
            {
                if ( osa.name(  ).equalsIgnoreCase( _strOrderSort ) )
                {
                    bResult = true;

                    break;
                }
            }
        }

        return bResult;
    }

    /**
     * @param strOrderSort
     *            the strOrderSort to set
     */
    public void setOrderSort( String strOrderSort )
    {
        _strOrderSort = strOrderSort;
    }

    /**
     * Sort order allowed
     */
    private enum OrderSortAllowed
    {ASC,
        DESC;
    }
}