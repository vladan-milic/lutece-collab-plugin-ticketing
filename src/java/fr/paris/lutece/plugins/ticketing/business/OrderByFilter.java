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

import java.util.Map;


/**
 *
 * abstract class OrderByFilter
 *
 */
public abstract class OrderByFilter
{
    /**
     * Default order by
     */
    public static final String CONSTANT_DEFAULT_ORDER_BY = "date_create";
    public static final String CONSTANT_DEFAULT_ORDER_SORT = OrderSortAllowed.DESC.name(  );

    /**
     * map containing functional field name as key
     * target sql column name as value
     */
    protected static Map<String, String> _mapOrderNameToColumnName;
    private String _strOrderBy;
    private String _strOrderSort;

    /**
     * constructor
     */
    public OrderByFilter(  )
    {
        super(  );
        initOrderNameToColumnNameMap(  );
    }

    /**
     * init _mapOrderNameToColumnName map
     *
     */
    protected abstract void initOrderNameToColumnNameMap(  );

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
        this._strOrderBy = strOrderBy;
    }

    /**
     * return order by column matching order by filter name
     * @return the _strOrderBy
     */
    public String getOrderBySqlColumn(  )
    {
        String strComputedOrderby;

        if ( _mapOrderNameToColumnName.containsKey( _strOrderBy ) )
        {
            strComputedOrderby = _mapOrderNameToColumnName.get( _strOrderBy );
        }
        else
        {
            strComputedOrderby = _mapOrderNameToColumnName.get( CONSTANT_DEFAULT_ORDER_BY );
        }

        return strComputedOrderby;
    }

    /**
     * returns default order by column
     * @return default order by column
     */
    public static String getDefaultOrderBySqlColumn(  )
    {
        return _mapOrderNameToColumnName.get( CONSTANT_DEFAULT_ORDER_BY );
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
     * @return  the strOrderSort to set
     */
    public String getOrderSort(  )
    {
        return _strOrderSort;
    }

    /**
     * @return  the strOrderSort to set
     */
    public static String getDefaultOrderSort(  )
    {
        return CONSTANT_DEFAULT_ORDER_SORT;
    }

    /**
     * returns true if filter is empty
     * @return true if filter is empty
     */
    public boolean isEmpty(  )
    {
        return StringUtils.isEmpty( _strOrderBy ) && StringUtils.isEmpty( _strOrderSort );
    }

    /**
     * Sort order allowed
     */
    public enum OrderSortAllowed
    {ASC,
        DESC;
    }
}
