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

import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.DocValuesTermsQuery;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;

import fr.paris.lutece.plugins.ticketing.web.search.TicketSearchItemConstant;

/**
 * Class util for the ticket search with Lucene
 */
public class TicketSearchUtil
{
    // CONSTANTS
    private static final int OFFSET = 0;

    /**
     * Create a TermsFilter object which contains all the values to filter on a specific field
     *
     * @param strFieldForTerm
     *            the field to filter
     * @param collectionInteger
     * @return the TermsFilter object
     */
    public static DocValuesTermsQuery createTermsFilter( String strFieldForTerm, Collection<Integer> collectionInteger )
    {
        List<BytesRef> listBytesRefIdAssignUnit = new ArrayList<BytesRef>( );
        if ( collectionInteger != null )
        {
            for ( Integer currentId : collectionInteger )
            {
                listBytesRefIdAssignUnit.add( getBytesRef( BigInteger.valueOf( currentId ) ) );
            }

            return new DocValuesTermsQuery( strFieldForTerm, listBytesRefIdAssignUnit );
        }

        return null;
    }

    /**
     * Return a BytesRef object associated to the given BigInteger parameter
     *
     * @param value
     *            the BigInteger value to convert
     * @return the BytesRef associated to the value
     */
    public static BytesRef getBytesRef( BigInteger value )
    {
        byte[] byteArrayValue = value.toByteArray( );
        return new BytesRef( byteArrayValue, OFFSET, byteArrayValue.length );
    }

    /**
     * Create the map containing the association of sort name and index field name
     *
     * @return the map with each sort is associate to a field
     */
    public static Map<String, List<AbstractMap.SimpleEntry<String, Type>>> initMapSortField( )
    {
        Map<String, List<AbstractMap.SimpleEntry<String, Type>>> mapSortField = new LinkedHashMap<String, List<AbstractMap.SimpleEntry<String, Type>>>( );

        // Sort on reference field and on ticket id
        mapSortField.put( "reference", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_REFERENCE, SortField.Type.STRING ),
                new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_TICKET_ID, SortField.Type.INT ) ) );

        // Sort on the creation date
        mapSortField.put( "date_create", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_DATE_CREATION, SortField.Type.LONG ) ) );

        // Sort on category label
        mapSortField.put( "category_label", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_CATEGORY, SortField.Type.STRING ) ) );

        // Sort on lastname and on firtsname and at last on email
        mapSortField.put( "lastname",
                Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_LASTNAME, SortField.Type.STRING ),
                        new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_FIRSTNAME, SortField.Type.STRING ),
                        new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_EMAIL, SortField.Type.STRING ) ) );

        // Sort on state label
        mapSortField.put( "state", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_STATE, SortField.Type.STRING ) ) );

        // Sort on nomenclature
        mapSortField.put( "nomenclature", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_TICKET_NOMENCLATURE, SortField.Type.STRING ) ) );

        // Sort on channel label
        mapSortField.put( "channel", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_CHANNEL_LABEL, SortField.Type.STRING ) ) );

        // Sort on assignee unit label and on the lastname of the assignee user
        mapSortField.put( "assignee", Arrays.asList( new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME, SortField.Type.STRING ),
                new AbstractMap.SimpleEntry<String, Type>( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME, SortField.Type.STRING ) ) );

        return mapSortField;
    }

}
