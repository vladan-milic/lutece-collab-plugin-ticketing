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
package fr.paris.lutece.plugins.ticketing.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * class Response
 */
public class ResponseRecap implements Serializable
{
    /**
     * The resource type of responses
     */
    private static final long serialVersionUID = 1L;
    private static final String STRING_LIST_SEPARATOR = ", ";
    private String _strTitle;
    private List<String> _listValues;

    /**
     * Default constructor
     */
    public ResponseRecap( )
    {
        _listValues = new ArrayList<String>( );
    }

    /**
     * Set the response title
     *
     * @param strTitle
     *            the response title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Get the response title
     *
     * @return the response title
     */
    public String getTitle( )
    {
        return _strTitle;
    }

    /**
     * Get the list of values of this response
     *
     * @return the list of value of this response
     */
    public List<String> getListValues( )
    {
        return _listValues;
    }

    /**
     * Set the list of values of this response
     *
     * @param listValues
     *            The list of values
     */
    public void setListValues( List<String> listValues )
    {
        this._listValues = listValues;
    }

    /**
     * Add a value in the list of values of this response
     *
     * @param value
     *            value to add
     */
    public void addValue( String value )
    {
        this._listValues.add( value );
    }

    /**
     * Get the list of values of this response into a string separated by comma
     *
     * @return the list of value of this response into a string separated by comma
     */
    public String getValuesToString( )
    {
        StringBuilder bufferValues = new StringBuilder( );

        if ( ( this._listValues != null ) && ( this._listValues.size( ) > 0 ) )
        {
            int i = 0;

            while ( i < ( this._listValues.size( ) - 1 ) )
            {
                bufferValues.append( this._listValues.get( i ) ).append( STRING_LIST_SEPARATOR );
                i++;
            }

            bufferValues.append( this._listValues.get( i ) );
        }

        return bufferValues.toString( );
    }
}
