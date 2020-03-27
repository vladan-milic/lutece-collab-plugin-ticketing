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
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.DocValuesTermsQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.business.search.TicketSearchService;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryTree;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexWriterUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketSearchUtil;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

/**
 * TicketSearchEngine
 */
public class TicketSearchEngine implements ITicketSearchEngine
{
    private static final String                                            PROPERTY_TICKETING_LUCENE_BOOLEANQUERY_MAXCLAUSECOUNT = "ticketing.internalIndexer.lucene.booleanquery.maxclausecount";

    // Constants
    private static final String                                            DESC_CONSTANT                                         = "DESC";

    // The map for the association on the filter selected by the user and the Lucene field
    private final Map<String, List<AbstractMap.SimpleEntry<String, Type>>> _mapSortField                                         = TicketSearchUtil.initMapSortField( );

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
            result.setDateUpdate( new Timestamp( document.getField( TicketSearchItemConstant.FIELD_DATE_UPDATE ).numericValue( ).longValue( ) ) );
            result.setGuid( document.get( TicketSearchItemConstant.FIELD_USER_GUID ) );
            result.setUrl( document.get( TicketSearchItemConstant.FIELD_URL ) );
            result.setTicketComment( document.get( TicketSearchItemConstant.FIELD_COMMENT ) );
            result.setCriticality( document.getField( TicketSearchItemConstant.FIELD_CRITICALITY ).numericValue( ).intValue( ) );
            result.setPriority( document.getField( TicketSearchItemConstant.FIELD_PRIORITY ).numericValue( ).intValue( ) );
            result.setReference( document.get( TicketSearchItemConstant.FIELD_REFERENCE ) );
            result.setTicketStatus( Integer.parseInt( document.get( TicketSearchItemConstant.FIELD_STATUS ) ) );
            result.setUserTitle( document.get( TicketSearchItemConstant.FIELD_USER_TITLE ) );
            result.setFirstname( document.get( TicketSearchItemConstant.FIELD_FIRSTNAME ) );
            result.setLastname( document.get( TicketSearchItemConstant.FIELD_LASTNAME ) );
            result.setEmail( document.get( TicketSearchItemConstant.FIELD_EMAIL ) );
            result.setMobilePhoneNumber( document.get( TicketSearchItemConstant.FIELD_MOBILE_PHONE_NUMBER ) );
            result.setFixedPhoneNumber( document.get( TicketSearchItemConstant.FIELD_FIXED_PHONE_NUMBER ) );
            result.setNomenclature( document.get( TicketSearchItemConstant.FIELD_TICKET_NOMENCLATURE ) );
            result.setDateClose( new Timestamp( document.getField( TicketSearchItemConstant.FIELD_DATE_CLOSE ).numericValue( ).longValue( ) ) );
            result.setFacilFamilleNumber( document.get( TicketSearchItemConstant.FIELD_FACIL_FAMILLE ) );
            result.setUserMessage( document.get( TicketSearchItemConstant.FIELD_USER_MESSAGE ) );

            // TicketCategory
            TicketCategoryService ticketCategoryInstance = TicketCategoryService.getInstance( true );

            TicketCategoryTree categoriesTree = ticketCategoryInstance.getCategoriesTree( );
            categoriesTree.getDepths( ).removeIf( c -> c.getDepthNumber( ) > TicketingConstants.CATEGORY_DEPTH_MAX );

            int maxDepthNumber = categoriesTree.getMaxDepthNumber( );
            int i = maxDepthNumber;
            while ( i >= 1 )
            {
                String strCategoryId = document.get( TicketSearchItemConstant.FIELD_CATEGORY_ID_DEPTHNUMBER + i );
                if ( strCategoryId != null )
                {
                    TicketCategory ticketCategory = ticketCategoryInstance.findCategoryById( Integer.valueOf( strCategoryId ) );
                    result.setTicketCategory( ticketCategory );
                    break;
                }
                i--;
            }

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
        List<Ticket> listResults = new ArrayList<>( );

        try
        {
            IndexSearcher searcher = TicketSearchService.getInstance( ).getSearcher( );

            if ( searcher != null )
            {
                // Get results documents
                TopDocs topDocs = searcher.search( query, LuceneSearchEngine.MAX_RESPONSES, getSortQuery( filter ) );
                ScoreDoc[] hits = topDocs.scoreDocs;

                for ( int i = 0; i < hits.length; i++ )
                {
                    int docId = hits[i].doc;
                    Document document = searcher.doc( docId );
                    listResults.add( createTicketFromDocument( document ) );
                }
                searcher.getIndexReader( ).close( );
            }
        } catch ( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return listResults;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> searchTickets( String strQuery, AdminUser user, TicketFilter filter ) throws ParseException
    {
        return search( createMainSearchQuery( strQuery, user, filter ), filter );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> searchTicketsByIds( List<Integer> listIdsTickets, TicketFilter filter ) throws ParseException
    {
        if ( ( listIdsTickets != null ) && !listIdsTickets.isEmpty( ) )
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
    private int searchCount( Query query )
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
                int nTotalHits = totaltHitcountCollector.getTotalHits( );
                searcher.getIndexReader( ).close( );
                return nTotalHits;
            }
        } catch ( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return nNbResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int searchCountTickets( String strQuery, AdminUser user, TicketFilter filter ) throws ParseException
    {
        return searchCount( createMainSearchQuery( strQuery, user, filter ) );
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
    private BooleanQuery createMainSearchQuery( String strQuery, AdminUser user, TicketFilter filter ) throws ParseException
    {
        Builder mainQuery = new Builder( );

        if ( StringUtils.isNotBlank( strQuery ) )
        {
            PerFieldAnalyzerWrapper perFieldAnalyzerWrapper = new PerFieldAnalyzerWrapper( TicketSearchService.getInstance( ).getAnalyzer( ), TicketIndexWriterUtil.getPerFieldAnalyzerMap( ) );
            Query queryTicket = new QueryParser( TicketSearchItemConstant.FIELD_CONTENTS, perFieldAnalyzerWrapper ).parse( strQuery );
            mainQuery.add( queryTicket, BooleanClause.Occur.MUST );
        }

        addQueryCategoriesClause( mainQuery, user );

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
    private void addQueryCategoriesClause( Builder booleanQueryBuilder, AdminUser user ) throws ParseException
    {
        Builder categoriesQueryBuilder = new Builder( );

        List<TicketCategory> domaines = TicketCategoryService.getInstance( true ).getDomainList( );
        List<TicketCategory> types = TicketCategoryService.getInstance( true ).getTypeList( );

        List<TicketCategory> categories = new ArrayList<>( domaines );
        categories.addAll( types );

        BooleanQuery.setMaxClauseCount( AppPropertiesService.getPropertyInt( PROPERTY_TICKETING_LUCENE_BOOLEANQUERY_MAXCLAUSECOUNT, BooleanQuery.getMaxClauseCount( ) ) );

        for ( TicketCategory category : categories )
        {
            TermQuery domQuery = new TermQuery( new Term( TicketSearchItemConstant.FIELD_CATEGORY_ID_DEPTHNUMBER + category.getDepth( ).getDepthNumber( ), Integer.toString( category.getId( ) ) ) );

            boolean isAuthorized = TicketCategoryService.isAuthorizedCategory( category, user, TicketCategory.PERMISSION_VIEW_LIST );
            categoriesQueryBuilder.add( new BooleanClause( domQuery, isAuthorized ? BooleanClause.Occur.SHOULD : BooleanClause.Occur.MUST_NOT ) );
        }

        booleanQueryBuilder.add( new BooleanClause( categoriesQueryBuilder.build( ), BooleanClause.Occur.MUST ) );
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
            boolean order = StringUtils.equalsIgnoreCase( filter.getOrderSort( ), DESC_CONSTANT );
            if ( _mapSortField.containsKey( filter.getOrderBy( ) ) )
            {
                List<SortField> listSortField = new LinkedList<>( );
                for ( AbstractMap.SimpleEntry<String, Type> entryField : _mapSortField.get( filter.getOrderBy( ) ) )
                {
                    listSortField.add( new SortField( entryField.getKey( ), entryField.getValue( ), order ) );
                }
                if ( !listSortField.isEmpty( ) )
                {
                    return new Sort( listSortField.toArray( new SortField[listSortField.size( )] ) );
                }
            } else
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

            List<Integer> markingsIds = filter.getMarkingsId( ).stream( ).map( Integer::parseInt ).collect( Collectors.toList( ) );

            if ( !markingsIds.isEmpty( ) )
            {
                Query markingQuery = IntPoint.newSetQuery( TicketSearchItemConstant.FIELD_TICKET_MARKING_ID, markingsIds );
                booleanQueryBuilderGlobal.add( markingQuery, Occur.MUST );
            }

            // Create a list of filter terms for the id of assignee unit
            DocValuesTermsQuery docValuesTermsQueryIdAssigneeUnit = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, filter.getFilterIdAssigneeUnit( ) );

            // Create a list of filter terms for the id of assigner unit
            DocValuesTermsQuery docValuesTermsQueryIdAssignerUnit = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, filter.getFilterIdAssignerUnit( ) );

            switch ( filter.getFilterView( ) )
            {
                case AGENT:

                    Builder booleanQueryBuilderIdUser = new Builder( );
                    booleanQueryBuilderIdUser.add( queryIdAdminUser, Occur.SHOULD );
                    booleanQueryBuilderIdUser.add( queryIdAssignerUser, Occur.SHOULD );

                    booleanQueryBuilderGlobal.add( booleanQueryBuilderIdUser.build( ), Occur.MUST );
                    break;
                case GROUP:
                    booleanQueryBuilderGlobal.add( queryIdAdminUser, Occur.MUST_NOT );
                    booleanQueryBuilderGlobal.add( queryIdAssignerUser, Occur.MUST_NOT );

                    Builder booleanQueryBuilderIdUnit = new Builder( );
                    booleanQueryBuilderIdUnit.add( docValuesTermsQueryIdAssigneeUnit, Occur.SHOULD );
                    booleanQueryBuilderIdUnit.add( docValuesTermsQueryIdAssignerUnit, Occur.SHOULD );

                    booleanQueryBuilderGlobal.add( booleanQueryBuilderIdUnit.build( ), Occur.MUST );

                    break;
                case DOMAIN:
                    booleanQueryBuilderGlobal.add( queryIdAdminUser, Occur.MUST_NOT );
                    booleanQueryBuilderGlobal.add( queryIdAssignerUser, Occur.MUST_NOT );

                    booleanQueryBuilderGlobal.add( docValuesTermsQueryIdAssigneeUnit, Occur.MUST_NOT );
                    booleanQueryBuilderGlobal.add( docValuesTermsQueryIdAssignerUnit, Occur.MUST_NOT );
                    break;
                case WARNING:
                    Builder booleanQueryManageable = new Builder( );
                    booleanQueryManageable.add( queryIdAdminUser, Occur.SHOULD );
                    booleanQueryManageable.add( queryIdAssignerUser, Occur.SHOULD );
                    if ( docValuesTermsQueryIdAssigneeUnit != null )
                    {
                        booleanQueryManageable.add( docValuesTermsQueryIdAssigneeUnit, Occur.SHOULD );
                    }
                    if ( docValuesTermsQueryIdAssignerUnit != null )
                    {
                        booleanQueryManageable.add( docValuesTermsQueryIdAssignerUnit, Occur.SHOULD );
                    }

                    booleanQueryBuilderGlobal.add( booleanQueryManageable.build( ), Occur.MUST );
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

            // Filter on the ticket category
            if ( filter.getMapCategoryId( ) != null )
            {
                for ( int i = 1; i <= filter.getMapCategoryId( ).size( ); i++ )
                {
                    if ( filter.getMapCategoryId( ).get( i ) != -1 )
                    {
                        addTicketCategoryIdFilter( booleanQueryBuilderGlobal, filter.getMapCategoryId( ).get( i ), i );
                    }

                }
            }

            // Filter on the creation start and end date
            if ( ( filter.getCreationStartDate( ) != null ) || ( filter.getCreationEndDate( ) != null ) )
            {
                Long startDate = filter.getCreationStartDate( ) != null ? filter.getCreationStartDate( ).getTime( ) : TicketingConstants.CONSTANT_ZERO;
                Long endDate = filter.getCreationEndDate( ) != null ? filter.getCreationEndDate( ).getTime( ) : new Date( ).getTime( );
                addCreationDateFilter( booleanQueryBuilderGlobal, startDate, endDate );
            }

            // Filter on the selected state
            if ( ( filter.getListIdWorkflowState( ) != null ) && !filter.getListIdWorkflowState( ).isEmpty( ) )
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
     * Add a filter for the ticket category id value
     *
     * @param queryBuilder
     *            The query builder to add the new BooleanClause
     * @param ticketCategoryIdValue
     *            The value to filter
     */
    private void addTicketCategoryIdFilter( Builder queryBuilder, int ticketCategoryIdValue, int nDepthNumber )
    {
        // Create the Query on th Type Id
        Query queryCategoryId = IntPoint.newExactQuery( TicketSearchItemConstant.FIELD_CATEGORY_ID_DEPTHNUMBER + nDepthNumber, ticketCategoryIdValue );

        // Add the ticket category id filter to the global filter
        queryBuilder.add( new BooleanClause( queryCategoryId, Occur.MUST ) );
    }

    /**
     * Add the Boolean clause on the creation date on the query builder
     *
     * @param queryBuilder
     *            The query builder to add the new BooleanClause
     * @param creationDateStart
     *            The creation date start limit
     * @param creationDateEnd
     *            The creation end date limit
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
        if ( ( listIdWorkflowState != null ) && !listIdWorkflowState.isEmpty( ) )
        {
            // Create a list of filter terms for the id of workflow state
            DocValuesTermsQuery termsFilterIdWorkflowState = TicketSearchUtil.createTermsFilter( TicketSearchItemConstant.FIELD_STATE_ID, listIdWorkflowState );

            // Return the Boolean clause on the id workflow state list
            queryBuilder.add( new BooleanClause( termsFilterIdWorkflowState, Occur.MUST ) );
        }
    }
}
