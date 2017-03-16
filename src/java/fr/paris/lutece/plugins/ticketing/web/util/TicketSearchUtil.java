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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;

import fr.paris.lutece.plugins.ticketing.web.search.TicketSearchItem;

/**
 * Class util for the ticket search with Lucene
 */
public class TicketSearchUtil
{
    // CONSTANTS
    private static final int CAPACITY = NumericUtils.BUF_SIZE_INT;
    private static final int SHIFT = 0;
    
    /**
     * Create a TermsFilter object which contains all the values to filter on a specific field
     * 
     * @param strFieldForTerm the field to filter
     * @param setInteger the set which contains all values to filter
     * @return the TermsFilter object
     */
    public static TermsFilter createTermsFilter( String strFieldForTerm, Collection<Integer> setInteger )
    {
        List<Term> listTermIdAssignUnit = new ArrayList<Term>( );
        if ( setInteger != null && !setInteger.isEmpty( ) )
        {
            for ( Integer currentId : setInteger )
            {
                BytesRef bytesRefId = getBytesRef( currentId );
                listTermIdAssignUnit.add( new Term( strFieldForTerm, bytesRefId ) );
            }                
        }   
        return listTermIdAssignUnit.isEmpty( ) ? null : new TermsFilter( listTermIdAssignUnit );
    }
    
    /**
     * Return a BytesRef object associated to the given int parameter
     * 
     * @param value the int value to convert
     * @return the BytesRef associated to the value
     */
    public static BytesRef getBytesRef( int value )
    {
        BytesRef bytesRef = new BytesRef( CAPACITY );
        NumericUtils.intToPrefixCoded( value, SHIFT, bytesRef );
        
        return bytesRef;
    }
    
    /**
     * Return a BytesRef object associated to the given long parameter
     * 
     * @param value the long value to convert
     * @return the BytesRef associated to the value
     */
    public static BytesRef getBytesRef( long value )
    {
        BytesRef bytesRef = new BytesRef( CAPACITY );
        NumericUtils.longToPrefixCoded( value, SHIFT, bytesRef );
        
        return bytesRef;
    }
    
    /**
     * Create the map containing the association of sort name and index field name
     * 
     * @return the map with each sort is associate to a field
     */
    public static Map<String, AbstractMap.SimpleEntry<String, Type>> initMapSortField( )
    {
        Map<String, AbstractMap.SimpleEntry<String, Type>> mapSortField = new LinkedHashMap<String, AbstractMap.SimpleEntry<String, Type>>( );
        mapSortField.put( "reference", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_REFERENCE, SortField.Type.STRING ) );
        mapSortField.put( "date_create", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_DATE_CREATION, SortField.Type.LONG) );
        mapSortField.put( "category_label", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_CATEGORY, SortField.Type.STRING ) );
        mapSortField.put( "lastname", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_LASTNAME, SortField.Type.STRING ) );
        mapSortField.put( "state", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_STATE, SortField.Type.STRING ) );
        mapSortField.put( "nomenclature", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_TICKET_NOMENCLATURE, SortField.Type.STRING ) );
        mapSortField.put( "channel", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_CHANNEL_LABEL, SortField.Type.STRING ) );
        mapSortField.put( "assignee", new AbstractMap.SimpleEntry<String, Type>( TicketSearchItem.FIELD_ASSIGNEE_UNIT_NAME, SortField.Type.STRING ) );
        return mapSortField;
    }
    
}
