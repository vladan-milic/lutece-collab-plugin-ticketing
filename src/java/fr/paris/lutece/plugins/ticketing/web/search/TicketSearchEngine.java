/*
 * Copyright (c) 2002-2016, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.web.search;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.util.BytesRef;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.search.TicketSearchService;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexWriterUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketSearchUtil;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * TicketSearchEngine
 */
public class TicketSearchEngine implements ITicketSearchEngine
{
    // Constants
    private static final String DESC_CONSTANT = "DESC";
    private static final int TRUE_VALUE_CONSTANT = 1;
    private static final int FALSE_VALUE_CONSTANT = 0;

    // The map for the association on the filter selected by the user and the Lucene field
    private final Map<String, List<AbstractMap.SimpleEntry<String, Type>>> _mapSortField = TicketSearchUtil.initMapSortField( );

    /**
     * Convert a Document to a Ticket
     *
     * @param listSource
     *            The list of Lucene items
     * @return A list of generic search items
     */
    private Ticket createTicketFromDocument( Document document )
    {
        Ticket result = new Ticket( );
        if ( document != null )
        {
            result.setId( document.getField( TicketSearchItemConstant.FIELD_TICKET_ID ).numericValue( ).intValue( ) );
            result.setDateCreate( new Timestamp( document.getField( TicketSearchItemConstant.FIELD_DATE_CREATION ).numericValue( ).longValue( ) ) );
            result.setUrl( document.get( TicketSearchItemConstant.FIELD_URL ) );
            result.setIdTicketType( document.getField( TicketSearchItemConstant.FIELD_TICKET_TYPE_ID ).numericValue( ).intValue( ) );
            result.setTicketType( document.get( TicketSearchItemConstant.FIELD_TICKET_TYPE ) );
            result.setTicketComment( document.get( TicketSearchItemConstant.FIELD_COMMENT ) );
            result.setCriticality( document.getField( TicketSearchItemConstant.FIELD_CRITICALITY ).numericValue( ).intValue( ) );
            result.setPriority( document.getField( TicketSearchItemConstant.FIELD_PRIORITY ).numericValue( ).intValue( ) );
            result.setReference( document.get( TicketSearchItemConstant.FIELD_REFERENCE ) );
            result.setTicketStatus( Integer.parseInt( document.get( TicketSearchItemConstant.FIELD_STATUS ) ) );
            result.setIdTicketDomain( Integer.parseInt( document.get( TicketSearchItemConstant.FIELD_DOMAIN_ID ) ) );
            result.setTicketDomain( document.get( TicketSearchItemConstant.FIELD_DOMAIN ) );
            result.setUserTitle( document.get( TicketSearchItemConstant.FIELD_USER_TITLE ) );
            result.setFirstname( document.get( TicketSearchItemConstant.FIELD_FIRSTNAME ) );
            result.setLastname( document.get( TicketSearchItemConstant.FIELD_LASTNAME ) );
            result.setEmail( document.get( TicketSearchItemConstant.FIELD_EMAIL ) );
            result.setMobilePhoneNumber( document.get( TicketSearchItemConstant.FIELD_MOBILE_PHONE_NUMBER ) );
            result.setFixedPhoneNumber( document.get( TicketSearchItemConstant.FIELD_FIXED_PHONE_NUMBER ) );
            result.setNomenclature( document.get( TicketSearchItemConstant.FIELD_TICKET_NOMENCLATURE ) );
            result.setRead( BooleanUtils.toBoolean( Integer.parseInt( document.get( TicketSearchItemConstant.FIELD_TICKET_READ ) ), TRUE_VALUE_CONSTANT,
                    FALSE_VALUE_CONSTANT ) );

            // TicketCategory
            TicketCategory ticketCategory = new TicketCategory( );
            ticketCategory.setLabel( document.get( TicketSearchItemConstant.FIELD_CATEGORY ) );
            ticketCategory.setPrecision( document.get( TicketSearchItemConstant.FIELD_PRECISION ) );
            result.setTicketCategory( ticketCategory );

            // State
            State state = new State( );
            state.setId( document.getField( TicketSearchItemConstant.FIELD_STATE_ID ).numericValue( ).intValue( ) );
            state.setName( document.get( TicketSearchItemConstant.FIELD_STATE ) );
            result.setState( state );

            // Channel
            Channel channel = new Channel( );
            channel.setIconFont( document.get( TicketSearchItemConstant.FIELD_CHANNEL_ICONFONT ) );
            channel.setLabel( document.get( TicketSearchItemConstant.FIELD_CHANNEL_LABEL ) );
            result.setChannel( channel );

            // AssigneeUnit
            AssigneeUnit assigneeUnit = new AssigneeUnit( );
            assigneeUnit.setUnitId( document.getField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID ).numericValue( ).intValue( ) );
            assigneeUnit.setName( document.get( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME ) );
            result.setAssigneeUnit( assigneeUnit );

            // AssigneeUser
            AssigneeUser assigneeUser = new AssigneeUser( );
            assigneeUser.setAdminUserId( document.getField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID ).numericValue( ).intValue( ) );
            assigneeUser.setFirstname( document.get( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_FIRSTNAME ) );
            assigneeUser.setLastname( document.get( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME ) );
            result.setAssigneeUser( assigneeUser );

            // AssignerUnit
            AssigneeUnit assignerUnit = new AssigneeUnit( );
            assignerUnit.setUnitId( document.getField( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID ).numericValue( ).intValue( ) );
            result.setAssignerUnit( assignerUnit );

            // AssignerUser
            AssigneeUser assignerUser = new AssigneeUser( );
            assignerUser.setAdminUserId( document.getField( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID ).numericValue( ).intValue( ) );
            result.setAssignerUser( assignerUser );
        }
        return result;
    }

    /**
     * process search
     *
     * @param query
     *            lucene query
     * @return list of ticket matching query
     */
    private List<Ticket> search( Query query, TicketFilter filter )
    {
        List<Ticket> listResults = new ArrayList<Ticket>( );

        try
        {
            IndexSearcher searcher = TicketSearchService.getInstance( ).getSearcher( );

            if ( searcher != null )
            {
                // Get results documents
                TopDocs topDocs = searcher.search( query, addQueryFilterTabClause( filter ), LuceneSearchEngine.MAX_RESPONSES, getSortQuery( filter ) );
                ScoreDoc [ ] hits = topDocs.scoreDocs;

                for ( int i = 0; i < hits.length; i++ )
                {
                    int docId = hits [i].doc;
                    Document document = searcher.doc( docId );
                    listResults.add( createTicketFromDocument( document ) );
                }
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return listResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> searchTickets( String strQuery, List<TicketDomain> listTicketDomain, TicketFilter filter ) throws ParseException
    {
        return search( createMainSearchQuery( strQuery, listTicketDomain ), filter );
    }

    /**
     * Return the number of result documents
     *
     * @param query
     *            lucene query
     * @return the number of result
     */
    private int searchCount( Query query, TicketFilter filter )
    {
        int nNbResult = 0;
        try
        {
            IndexSearcher searcher = TicketSearchService.getInstance( ).getSearcher( );

            if ( searcher != null )
            {
                // Get the number of results documents
                TotalHitCountCollector totaltHitcountCollector = new TotalHitCountCollector( );
                searcher.search( query, addQueryFilterTabClause( filter ), totaltHitcountCollector );
                return totaltHitcountCollector.getTotalHits( );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return nNbResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchCountTickets( String strQuery, List<TicketDomain> listTicketDomain, TicketFilter filter ) throws ParseException
    {
        return searchCount( createMainSearchQuery( strQuery, listTicketDomain ), filter );
    }

    /**
     * Method which create the main query use for the ticket search
     * 
     * @param strQuery
     *            the query typed by the user
     * @param listTicketDomain
     *            the list of the domain use for the search
     * @return the main query constructed for the search
     * @throws ParseException
     */
    private BooleanQuery createMainSearchQuery( String strQuery, List<TicketDomain> listTicketDomain ) throws ParseException
    {
        BooleanQuery mainQuery = new BooleanQuery( );
        if ( StringUtils.isNotBlank( strQuery ) )
        {
            PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper( TicketSearchService.getInstance( ).getAnalyzer( ),
                    TicketIndexWriterUtil.getPerFieldAnalyzerMap( ) );
            Query queryTicket = new QueryParser( IndexationService.LUCENE_INDEX_VERSION, TicketSearchItemConstant.FIELD_CONTENTS, perFieldAnalyzerWrapper )
                    .parse( strQuery );
            mainQuery.add( queryTicket, BooleanClause.Occur.MUST );
        }
        addQueryDomainClause( mainQuery, listTicketDomain );

        return mainQuery;
    }

    /**
     * add ticket domain clause
     * 
     * @param booleanQuery
     *            input query
     * @param listUserDomain
     *            list domains authorized for admin user
     * @throws ParseException
     *             exception while parsing document type
     */
    private void addQueryDomainClause( BooleanQuery booleanQuery, List<TicketDomain> listUserDomain ) throws ParseException
    {
        BooleanQuery domainsQuery = new BooleanQuery( );

        for ( TicketDomain domain : listUserDomain )
        {
            TermQuery domQuery = new TermQuery( new Term( TicketSearchItemConstant.FIELD_DOMAIN_ID, Integer.toString( domain.getId( ) ) ) );
            domainsQuery.add( new BooleanClause( domQuery, BooleanClause.Occur.SHOULD ) );
        }

        booleanQuery.add( new BooleanClause( domainsQuery, BooleanClause.Occur.MUST ) );
    }

    /**
     * Return the Sort object to use for the search according to the given filter
     * 
     * @param filter
     *            the filter to use for sorting the search results
     * @return the Sort object
     */
    private Sort getSortQuery( TicketFilter filter )
    {
        Sort defaultSort = new Sort( new SortField( TicketSearchItemConstant.FIELD_DATE_CREATION, SortField.Type.LONG, true ) );
        if ( filter != null )
        {
            boolean order = StringUtils.equalsIgnoreCase( filter.getOrderSort( ), DESC_CONSTANT ) ? true : false;
            if ( _mapSortField.containsKey( filter.getOrderBy( ) ) )
            {
                List<SortField> listSortField = new LinkedList<>( );
                for ( AbstractMap.SimpleEntry<String, Type> entryField : _mapSortField.get( filter.getOrderBy( ) ) )
                {
                    listSortField.add( new SortField( entryField.getKey( ), entryField.getValue( ), order ) );
                }
                if ( !listSortField.isEmpty( ) )
                {
                    return new Sort( listSortField.toArray( new SortField [ listSortField.size( )] ) );
                }
            }
            else
            {
                return defaultSort;
            }
        }

        // The default filter based on the date creation ascending
        return defaultSort;
    }

    /**
     * Add ticket filter clause on the selected tab
     * 
     * @param booleanQuery
     * @param filter
     */
    private BooleanFilter addQueryFilterTabClause( TicketFilter filter )
    {
        if ( filter != null )
        {
            BooleanFilter booleanFilterGlobal = new BooleanFilter( );

            BytesRef bytesRefIdAssigneAdminUser = TicketSearchUtil.getBytesRef( filter.getFilterIdAdminUser( ) );
            TermFilter termFilterIdAdminUser = new TermFilter( new Term( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID, bytesRefIdAssigneAdminUser ) );
            TermFilter termFilterIdAssignerUser = new TermFilter( new Term( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID, bytesRefIdAssigneAdminUser ) );

            // Create a list of filter terms for the id of assignee unit
            TermsFilter termsFilterIdAssigneeUnit = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID,
                    filter.getFilterIdAssigneeUnit( ) );

            // Create a list of filter terms for the id of assigner unit
            TermsFilter termsFilterIdAssignerUnit = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID,
                    filter.getFilterIdAssignerUnit( ) );

            BooleanFilter booleanFilterIdUser = new BooleanFilter( );
            BooleanFilter booleanFilterIdUnit = new BooleanFilter( );

            switch( filter.getFilterView( ) )
            {
                case AGENT:
                    booleanFilterIdUser = new BooleanFilter( );
                    booleanFilterIdUser.add( termFilterIdAdminUser, Occur.SHOULD );
                    booleanFilterIdUser.add( termFilterIdAssignerUser, Occur.SHOULD );

                    booleanFilterGlobal.add( booleanFilterIdUser, Occur.MUST );
                    break;
                case GROUP:
                    booleanFilterGlobal.add( termFilterIdAdminUser, Occur.MUST_NOT );
                    booleanFilterGlobal.add( termFilterIdAssignerUser, Occur.MUST_NOT );

                    booleanFilterIdUnit = new BooleanFilter( );
                    booleanFilterIdUnit.add( termsFilterIdAssigneeUnit, Occur.SHOULD );
                    booleanFilterIdUnit.add( termsFilterIdAssignerUnit, Occur.SHOULD );

                    booleanFilterGlobal.add( booleanFilterIdUnit, Occur.MUST );
                    break;
                case DOMAIN:
                    booleanFilterGlobal.add( termFilterIdAdminUser, Occur.MUST_NOT );
                    booleanFilterGlobal.add( termFilterIdAssignerUser, Occur.MUST_NOT );

                    booleanFilterGlobal.add( termsFilterIdAssigneeUnit, Occur.MUST_NOT );
                    booleanFilterGlobal.add( termsFilterIdAssignerUnit, Occur.MUST_NOT );
                    break;
                case ALL:
                default:
                    break;
            }

            // Return the global filter
            return addSelectedFilter( booleanFilterGlobal, filter );
        }

        return null;
    }

    /**
     * Add the filters selected by the user
     * 
     * @param booleanFilterGlobal
     *            the global filter of the request
     * @param filter
     *            to apply to the global filter
     * @return
     */
    private BooleanFilter addSelectedFilter( BooleanFilter booleanFilterGlobal, TicketFilter filter )
    {
        if ( filter != null )
        {
            // Filter on the ticket urgency
            if ( filter.getUrgency( ) != -1 )
            {
                addUrgencyFilter( booleanFilterGlobal, filter.getUrgency( ) );
            }

            // Filter on the ticket type
            if ( filter.getIdType( ) != -1 )
            {
                addTicketTypeIdFilter( booleanFilterGlobal, filter.getIdType( ) );
            }

            // Filter on the creation date
            if ( filter.getCreationStartDate( ) != null )
            {
                addCreationDateFilter( booleanFilterGlobal, filter.getCreationStartDate( ).getTime( ) );
            }

            // Filter on the selected state
            if ( filter.getListIdWorkflowState( ) != null && !filter.getListIdWorkflowState( ).isEmpty( ) )
            {
                addIdWorkflowStateFilter( booleanFilterGlobal, filter.getListIdWorkflowState( ) );
            }
        }
        return booleanFilterGlobal;
    }

    /**
     * Add a filter for the urgency value
     * 
     * @param booleanFilter
     *            the filter to add the urgency filter
     * @param urgencyValue
     *            the value to filter
     */
    private void addUrgencyFilter( BooleanFilter booleanFilter, int urgencyValue )
    {
        BytesRef bytesRefZero = TicketSearchUtil.getBytesRef( 0 );
        BytesRef bytesRefUrgency = TicketSearchUtil.getBytesRef( urgencyValue );

        TermRangeFilter termRangeFilterPriority = new TermRangeFilter( TicketSearchItemConstant.FIELD_PRIORITY, bytesRefZero, bytesRefUrgency, true, true );
        TermFilter termFilterSamePriority = new TermFilter( new Term( TicketSearchItemConstant.FIELD_PRIORITY, bytesRefUrgency ) );

        TermRangeFilter termRangeFilterCritilicatity = new TermRangeFilter( TicketSearchItemConstant.FIELD_CRITICALITY, bytesRefZero, bytesRefUrgency, true,
                true );
        TermFilter termFilterSameCritilicatity = new TermFilter( new Term( TicketSearchItemConstant.FIELD_CRITICALITY, bytesRefUrgency ) );

        // Same Criticality value and Priority in range [0, urgency]
        BooleanFilter booleanFilterRangePrioritySameCriticality = new BooleanFilter( );
        booleanFilterRangePrioritySameCriticality.add( termRangeFilterPriority, Occur.MUST );
        booleanFilterRangePrioritySameCriticality.add( termFilterSameCritilicatity, Occur.MUST );

        // Same Priority value and Criticality in range [0, urgency]
        BooleanFilter booleanFilterRangeCriticalitySamePriority = new BooleanFilter( );
        booleanFilterRangeCriticalitySamePriority.add( termRangeFilterCritilicatity, Occur.MUST );
        booleanFilterRangeCriticalitySamePriority.add( termFilterSamePriority, Occur.MUST );

        // Create the urgency filter
        BooleanFilter booleanfilterUrgency = new BooleanFilter( );
        booleanfilterUrgency.add( booleanFilterRangePrioritySameCriticality, Occur.SHOULD );
        booleanfilterUrgency.add( booleanFilterRangeCriticalitySamePriority, Occur.SHOULD );

        // Add the urgency filter to the global filter
        booleanFilter.add( booleanfilterUrgency, Occur.MUST );
    }

    /**
     * Add a filter for the ticket type id value
     * 
     * @param booleanFilter
     *            the filter to add the ticket type id filter
     * @param ticketTypeIdValue
     *            the value to filter
     */
    private void addTicketTypeIdFilter( BooleanFilter booleanFilter, int ticketTypeIdValue )
    {
        BytesRef bytesRefIdType = TicketSearchUtil.getBytesRef( ticketTypeIdValue );

        TermFilter termRangeFilterIdType = new TermFilter( new Term( TicketSearchItemConstant.FIELD_TICKET_TYPE_ID, bytesRefIdType ) );

        // Add the ticket type id filter to the global filter
        booleanFilter.add( termRangeFilterIdType, Occur.MUST );
    }

    /**
     * Add a filter for the ticket creation date
     * 
     * @param booleanFilter
     *            the filter to add the creation date filter
     * @param creationDate
     *            the creation date limit
     */
    private void addCreationDateFilter( BooleanFilter booleanFilter, long creationDate )
    {
        BytesRef bytesRefCreationDateFilter = TicketSearchUtil.getBytesRef( creationDate );
        BytesRef bytesRefActualDateFilter = TicketSearchUtil.getBytesRef( new Date( ).getTime( ) );

        TermRangeFilter termRangeFilterCreationDate = new TermRangeFilter( TicketSearchItemConstant.FIELD_DATE_CREATION, bytesRefCreationDateFilter,
                bytesRefActualDateFilter, true, true );

        // Add the creation date filter to the global filter
        booleanFilter.add( termRangeFilterCreationDate, Occur.MUST );
    }

    /**
     * Add the workflow state id list to filter
     * 
     * @param booleanFilter
     *            the filter to add the workflow state id filter
     * @param listIdWorkflowState
     *            the list of workflow state id to filter
     */
    private void addIdWorkflowStateFilter( BooleanFilter booleanFilter, List<Integer> listIdWorkflowState )
    {
        // Create a list of filter terms for the id of workflow state
        TermsFilter termsFilterIdWorkflowState = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_STATE_ID, listIdWorkflowState );

        // Add the workflow state id list to filter to the global filter
        booleanFilter.add( termsFilterIdWorkflowState, Occur.MUST );
    }
}
