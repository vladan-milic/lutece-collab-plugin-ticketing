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

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.search.TicketSearchService;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexWriterUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketSearchUtil;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.util.AppLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.SortField.Type;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

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

            // Ticket marking
            IndexableField ticketMarkingId = document.getField( TicketSearchItemConstant.FIELD_TICKET_MARKING_ID );
            int nMarkingId = ticketMarkingId != null ? ticketMarkingId.numericValue( ).intValue( ) : TicketingConstants.PROPERTY_UNSET_INT;
            result.setIdTicketMarking( nMarkingId );
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
                TopDocs topDocs = searcher.search( query, LuceneSearchEngine.MAX_RESPONSES, getSortQuery( filter ) );
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
        return search( createMainSearchQuery( strQuery, listTicketDomain, filter ), filter );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> searchTicketsByIds( List<Integer> listIdsTickets, TicketFilter filter ) throws ParseException
    {
        if ( listIdsTickets != null && !listIdsTickets.isEmpty( ) )
        {
            Builder idTicketsQueryBuilder = new Builder( );
            for ( int idTicket : listIdsTickets )
            {
                Query idTicketQuery = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_TICKET_ID, idTicket );
                idTicketsQueryBuilder.add( new BooleanClause( idTicketQuery, BooleanClause.Occur.SHOULD ) );
            }

            // Create the main builder
            Builder mainIdTicketsQuery = new Builder( );
            mainIdTicketsQuery.add( new BooleanClause( idTicketsQueryBuilder.build( ), BooleanClause.Occur.MUST ) );

            return search( mainIdTicketsQuery.build( ), filter );
        }
        return null;
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
                searcher.search( query, totaltHitcountCollector );
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
        return searchCount( createMainSearchQuery( strQuery, listTicketDomain, filter ), filter );
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
    private BooleanQuery createMainSearchQuery( String strQuery, List<TicketDomain> listTicketDomain, TicketFilter filter ) throws ParseException
    {
        Builder mainQuery = new Builder( );

        if ( StringUtils.isNotBlank( strQuery ) )
        {
            PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper( TicketSearchService.getInstance( ).getAnalyzer( ),
                    TicketIndexWriterUtil.getPerFieldAnalyzerMap( ) );
            Query queryTicket = new QueryParser( TicketSearchItemConstant.FIELD_CONTENTS, perFieldAnalyzerWrapper ).parse( strQuery );
            mainQuery.add( queryTicket, BooleanClause.Occur.MUST );
        }

        addQueryDomainClause( mainQuery, listTicketDomain );

        // Construct the final query with the selected filter
        Builder finalGlobalQueryBuilder = new Builder( );
        finalGlobalQueryBuilder.add( mainQuery.build( ), Occur.MUST );
        finalGlobalQueryBuilder.add( addQueryFilterTabClause( filter ).build( ), Occur.FILTER );

        return finalGlobalQueryBuilder.build( );
    }

    /**
     * add ticket domain clause
     * 
     * @param booleanQueryBuilder
     *            input query
     * @param listUserDomain
     *            list domains authorized for admin user
     * @throws ParseException
     *             exception while parsing document type
     */
    private void addQueryDomainClause( Builder booleanQueryBuilder, List<TicketDomain> listUserDomain ) throws ParseException
    {
        Builder domainsQueryBuilder = new Builder( );

        for ( TicketDomain domain : listUserDomain )
        {
            TermQuery domQuery = new TermQuery( new Term( TicketSearchItemConstant.FIELD_DOMAIN_ID, Integer.toString( domain.getId( ) ) ) );
            domainsQueryBuilder.add( new BooleanClause( domQuery, BooleanClause.Occur.SHOULD ) );
        }

        booleanQueryBuilder.add( new BooleanClause( domainsQueryBuilder.build( ), BooleanClause.Occur.MUST ) );
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
    private Builder addQueryFilterTabClause( TicketFilter filter )
    {
        if ( filter != null )
        {
            // Create the global query builder
            Builder booleanQueryBuilderGlobal = new Builder( );

            // Create the query on the id admin user and id assigner user field
            int nIdAdminUser = filter.getFilterIdAdminUser( );
            Query queryIdAdminUser = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID, nIdAdminUser );
            Query queryIdAssignerUser = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID, nIdAdminUser );

            // Create a list of filter terms for the id of assignee unit
            DocValuesTermsQuery docValuesTermsQueryIdAssigneeUnit = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID,
                    filter.getFilterIdAssigneeUnit( ) );

            // Create a list of filter terms for the id of assigner unit
            DocValuesTermsQuery docValuesTermsQueryIdAssignerUnit = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID,
                    filter.getFilterIdAssignerUnit( ) );

            switch( filter.getFilterView( ) )
            {
                case AGENT:

                    Builder booleanQueryBuilderIdUser = new Builder( );
                    booleanQueryBuilderIdUser.add( queryIdAdminUser, Occur.SHOULD );
                    booleanQueryBuilderIdUser.add( queryIdAssignerUser, Occur.SHOULD );

                    booleanQueryBuilderGlobal.add( booleanQueryBuilderIdUser.build( ), Occur.MUST );
                    break;
                case GROUP:
                    if ( docValuesTermsQueryIdAssigneeUnit != null && docValuesTermsQueryIdAssignerUnit != null )
                    {
                        booleanQueryBuilderGlobal.add( queryIdAdminUser, Occur.MUST_NOT );
                        booleanQueryBuilderGlobal.add( queryIdAssignerUser, Occur.MUST_NOT );

                        Builder booleanQueryBuilderIdUnit = new Builder( );
                        booleanQueryBuilderIdUnit.add( docValuesTermsQueryIdAssigneeUnit, Occur.SHOULD );
                        booleanQueryBuilderIdUnit.add( docValuesTermsQueryIdAssignerUnit, Occur.SHOULD );

                        booleanQueryBuilderGlobal.add( booleanQueryBuilderIdUnit.build( ), Occur.MUST );
                    }
                    break;
                case DOMAIN:
                    if ( docValuesTermsQueryIdAssigneeUnit != null && docValuesTermsQueryIdAssignerUnit != null )
                    {
                        booleanQueryBuilderGlobal.add( queryIdAdminUser, Occur.MUST_NOT );
                        booleanQueryBuilderGlobal.add( queryIdAssignerUser, Occur.MUST_NOT );

                        booleanQueryBuilderGlobal.add( docValuesTermsQueryIdAssigneeUnit, Occur.MUST_NOT );
                        booleanQueryBuilderGlobal.add( docValuesTermsQueryIdAssignerUnit, Occur.MUST_NOT );
                    }
                    break;
                case ALL:
                default:
                    break;
            }

            // Return the global filter
            return addSelectedFilter( booleanQueryBuilderGlobal, filter );
        }

        return null;
    }

    /**
     * Add the filters selected by the user
     * 
     * @param booleanQueryBuilderGlobal
     *            The global query builder of the query
     * @param filter
     *            The filter to apply to the global builder
     * @return The builder with all clause
     */
    private Builder addSelectedFilter( Builder booleanQueryBuilderGlobal, TicketFilter filter )
    {
        if ( filter != null )
        {
            // Filter on the ticket urgency
            if ( filter.getUrgency( ) != -1 )
            {
                addUrgencyFilter( booleanQueryBuilderGlobal, filter.getUrgency( ) );
            }

            // Filter on the ticket type
            if ( filter.getIdType( ) != -1 )
            {
                addTicketTypeIdFilter( booleanQueryBuilderGlobal, filter.getIdType( ) );
            }

            // Filter on the creation start and end date
            if ( filter.getCreationStartDate( ) != null || filter.getCreationEndDate( ) != null )
            {
                Long startDate = filter.getCreationStartDate( ) != null ? filter.getCreationStartDate( ).getTime( ) : TicketingConstants.CONSTANT_ZERO;
                Long endDate = filter.getCreationEndDate( ) != null ? filter.getCreationEndDate( ).getTime( ) : new Date( ).getTime( );
                addCreationDateFilter( booleanQueryBuilderGlobal, startDate, endDate );
            }

            // Filter on the selected state
            if ( filter.getListIdWorkflowState( ) != null && !filter.getListIdWorkflowState( ).isEmpty( ) )
            {
                addIdWorkflowStateFilter( booleanQueryBuilderGlobal, filter.getListIdWorkflowState( ) );
            }
        }
        return booleanQueryBuilderGlobal;
    }

    /**
     * Add a filter for the urgency value
     * 
     * @param queryBuilder
     *            The query builder to add the new BooleanClause
     * @param urgencyValue
     *            The value to filter
     */
    private void addUrgencyFilter( Builder queryBuilder, int urgencyValue )
    {
        // Create the Query on the Priority field
        Query queryRangePriority = IntPoint.newRangeQuery( TicketSearchItemConstant.FIELD_PRIORITY, TicketingConstants.CONSTANT_ZERO, urgencyValue );
        Query querySamePriority = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_PRIORITY, urgencyValue );

        // Create the query on the Criticality field
        Query queryRangeCritilicatity = IntPoint.newRangeQuery( TicketSearchItemConstant.FIELD_CRITICALITY, TicketingConstants.CONSTANT_ZERO, urgencyValue );
        Query querySameCritilicatity = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_CRITICALITY, urgencyValue );

        // Same Criticality value and Priority in range [0, urgency]
        Builder booleanQueryBuilderRangePrioritySameCriticality = new Builder( );
        booleanQueryBuilderRangePrioritySameCriticality.add( queryRangePriority, Occur.MUST );
        booleanQueryBuilderRangePrioritySameCriticality.add( querySameCritilicatity, Occur.MUST );

        // Same Priority value and Criticality in range [0, urgency]
        Builder booleanQueryBuilderRangeCriticalitySamePriority = new Builder( );
        booleanQueryBuilderRangeCriticalitySamePriority.add( queryRangeCritilicatity, Occur.MUST );
        booleanQueryBuilderRangeCriticalitySamePriority.add( querySamePriority, Occur.MUST );

        // Create the urgency filter
        Builder booleanQueryBuilderUrgency = new Builder( );
        booleanQueryBuilderUrgency.add( booleanQueryBuilderRangePrioritySameCriticality.build( ), Occur.SHOULD );
        booleanQueryBuilderUrgency.add( booleanQueryBuilderRangeCriticalitySamePriority.build( ), Occur.SHOULD );

        // Add the urgency filter to the global query builder
        queryBuilder.add( booleanQueryBuilderUrgency.build( ), Occur.MUST );
    }

    /**
     * Add a filter for the ticket type id value
     * 
     * @param queryBuilder
     *            The query builder to add the new BooleanClause
     * @param ticketTypeIdValue
     *            The value to filter
     */
    private void addTicketTypeIdFilter( Builder queryBuilder, int ticketTypeIdValue )
    {
        // Create the Query on th Type Id
        Query queryTypeId = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_TICKET_TYPE_ID, ticketTypeIdValue );

        // Add the ticket type id filter to the global filter
        queryBuilder.add( new BooleanClause( queryTypeId, Occur.MUST ) );
    }

    /**
     * Add the Boolean clause on the creation date on the query builder
     *
     * @param queryBuilder
     *         The query builder to add the new BooleanClause
     * @param creationDateStart
     *         The creation date start limit
     * @param creationDateEnd
     *         The creation end date limit
     */
    private void addCreationDateFilter( Builder queryBuilder, long creationDateStart, long creationDateEnd )
    {
        // Create the range query of the creation date
        Query queryCreationDate = LongPoint.newRangeQuery( TicketSearchItemConstant.FIELD_DATE_CREATION, creationDateStart, creationDateEnd );

        // Return the Boolean clause on the creation date
        queryBuilder.add( new BooleanClause( queryCreationDate, Occur.MUST ) );
    }

    /**
     * Add the boolean clause of the list id workflow state on the query builder
     * 
     * @param queryBuilder
     *            The query builder to add the new BooleanClause
     * @param listIdWorkflowState
     *            The list of workflow state id to filter
     */
    private void addIdWorkflowStateFilter( Builder queryBuilder, List<Integer> listIdWorkflowState )
    {
        if ( listIdWorkflowState != null && !listIdWorkflowState.isEmpty( ) )
        {
            // Create a list of filter terms for the id of workflow state
            DocValuesTermsQuery termsFilterIdWorkflowState = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_STATE_ID, listIdWorkflowState );

            // Return the Boolean clause on the id workflow state list
            queryBuilder.add( new BooleanClause( termsFilterIdWorkflowState, Occur.MUST ) );
        }
    }
}
