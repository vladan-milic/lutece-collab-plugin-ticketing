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
package fr.paris.lutece.plugins.ticketing.business.search;

import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.BytesRef;

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.search.TicketSearchItemConstant;
import fr.paris.lutece.plugins.ticketing.web.util.TicketIndexWriterUtil;
import fr.paris.lutece.plugins.ticketing.web.util.TicketSearchUtil;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Ticket Indexer
 *
 */
public class TicketIndexer implements SearchIndexer, ITicketSearchIndexer
{
    public static final String PROPERTY_INDEXER_NAME = "ticketing.indexer.name";
    private static final String PARAMETER_TICKET_ID = "ticket_id";
    private static final String PLUGIN_NAME = "ticketing";
    private static final String PROPERTY_PAGE_BASE_URL = "search.pageIndexer.baseUrl";
    private static final String PROPERTY_DOCUMENT_TYPE = "ticketing.indexer.documentType";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "ticketing.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "ticketing.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "ticketing.indexer.enable";
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String JSP_VIEW_TICKET = "jsp/admin/plugins/ticketing/TicketView.jsp";
    private static final String JSP_SEARCH_TICKET = "jsp/admin/plugins/ticketing/TicketSearch.jsp?action=search";
    private static final int CONSTANT_ID_NULL = -1;
    private static final String SEPARATOR = " ";

    /**
     * {@inheritDoc }
     */
    @Override
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Document> getDocuments( String strDocument ) throws IOException, InterruptedException, SiteMessageException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<org.apache.lucene.document.Document>( );
        String strPortalUrl = AppPropertiesService.getProperty( PROPERTY_PAGE_BASE_URL );
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );

        Ticket ticket = TicketHome.findByPrimaryKey( Integer.parseInt( strDocument ) );

        if ( ticket != null )
        {
            UrlItem urlTicket = new UrlItem( strPortalUrl );
            urlTicket.addParameter( XPageAppService.PARAM_XPAGE_APP, PLUGIN_NAME );
            urlTicket.addParameter( TicketingConstants.PARAMETER_ID_TICKET, ticket.getId( ) );

            org.apache.lucene.document.Document docSubject = getDocument( ticket, urlTicket.getUrl( ), plugin );
            listDocs.add( docSubject );
        }

        return listDocs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        TicketSearchService.getInstance( ).processIndexing( true );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isEnable( )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) && ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString( ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) )
                && PluginService.isPluginEnable( PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * Index or update a Ticket in the index
     * 
     * @param indexWriter
     *            the indexWriter Lucene
     * @param ticket
     *            the ticket to index or update
     * @throws TicketIndexerException
     * @throws IOException
     */
    public void indexTicket( IndexWriter indexWriter, Ticket ticket ) throws TicketIndexerException, IOException
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        UrlItem urlTicket = new UrlItem( JSP_VIEW_TICKET );
        urlTicket.addParameter( PARAMETER_TICKET_ID, ticket.getId( ) );

        Document docTicket = null;

        try
        {
            docTicket = getDocument( ticket, urlTicket.getUrl( ), plugin );
        }
        catch( Exception e )
        {
            String strMessage = "Ticket ID : " + ticket.getId( );
            IndexationService.error( this, e, strMessage );
            throw new TicketIndexerException( );
        }

        if ( docTicket != null )
        {
            indexWriter.updateDocument( new Term( TicketSearchItemConstant.FIELD_DOCUMENT_ID, String.valueOf( ticket.getId( ) ) ), docTicket );
        }
    }

    /**
     * Get a document for indexing
     * 
     * @param ticket
     *            The ticket
     * @param strUrl
     *            The URL
     * @param plugin
     *            The plugin
     * @return The document
     * @throws IOException
     *             if an IO error occurs
     * @throws InterruptedException
     *             if a Thread error occurs
     */
    public static Document getDocument( Ticket ticket, String strUrl, Plugin plugin )
    {
        // Get the state associate to the ticket
        State ticketState = null;
        if ( ticket.getTicketCategory( ) != null )
        {
            IStateService stateService = SpringContextService.getBean( StateService.BEAN_SERVICE );
            ticketState = stateService.findByResource( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, ticket.getTicketCategory( ).getIdWorkflow( ) );
            ticket.setState( ticketState );
        }

        // make a new, empty document
        Document doc = new Document( );

        int nIdTicket = ticket.getId( );

        // --- document identifier
        doc.add( new StringField( TicketSearchItemConstant.FIELD_DOCUMENT_ID, String.valueOf( nIdTicket ), Store.YES ) );

        // --- ticket identifier
        doc.add( new IntPoint( TicketSearchItemConstant.FIELD_TICKET_ID, nIdTicket ) );
        doc.add( new NumericDocValuesField( TicketSearchItemConstant.FIELD_TICKET_ID, nIdTicket ) );
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_TICKET_ID, nIdTicket ) );

        // --- field contents
        doc.add( new TextField( TicketSearchItemConstant.FIELD_CONTENTS, manageNullValue( getContentForIndexer( ticket ) ), Store.NO ) );

        // --- ticket reference
        String strReference = manageNullValue( ticket.getReference( ) );
        doc.add( new StringField( TicketSearchItemConstant.FIELD_REFERENCE, strReference, Store.YES ) );
        doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_REFERENCE, new BytesRef( strReference ) ) );

        // --- ticket date creation
        Long longCreationDate = ticket.getDateCreate( ) == null ? 0 : ticket.getDateCreate( ).getTime( );
        doc.add( new LongPoint( TicketSearchItemConstant.FIELD_DATE_CREATION, longCreationDate ) );
        doc.add( new NumericDocValuesField( TicketSearchItemConstant.FIELD_DATE_CREATION, longCreationDate ) );
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_DATE_CREATION, longCreationDate ) );

        // --- ticket comment
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_COMMENT, manageNullValue( ticket.getTicketComment( ) ) ) );

        // --- ticket nomenclature
        String strNomenclature = manageNullValue( ticket.getNomenclature( ) );
        doc.add( new TextField( TicketSearchItemConstant.FIELD_TICKET_NOMENCLATURE, strNomenclature, Store.YES ) );
        doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_TICKET_NOMENCLATURE, new BytesRef( strNomenclature ) ) );

        // --- ticket criticality
        int nCriticality = ticket.getCriticality( );
        doc.add( new IntPoint( TicketSearchItemConstant.FIELD_CRITICALITY, nCriticality ) );
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_CRITICALITY, nCriticality ) );

        // --- ticket priority
        int nPriority = ticket.getPriority( );
        doc.add( new IntPoint( TicketSearchItemConstant.FIELD_PRIORITY, nPriority ) );
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_PRIORITY, nPriority ) );

        // --- ticket status
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_STATUS, ticket.getTicketStatus( ) ) );

        // --- ticket category
        for ( TicketCategory ticketCategory : ticket.getBranch( ) ) 
        {
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_CATEGORY_ID_DEPTHNUMBER + ticketCategory.getDepth( ).getDepthNumber( ), ticketCategory.getId( ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_CATEGORY_ID_DEPTHNUMBER + ticketCategory.getDepth( ).getDepthNumber( ), ticketCategory.getId( ) ) );
            doc.add( new StringField( TicketSearchItemConstant.FIELD_CATEGORY_ID_DEPTHNUMBER + ticketCategory.getDepth( ).getDepthNumber( ), Integer.toString( ticketCategory.getId( ) ), Store.YES ) );
            doc.add( new TextField( TicketSearchItemConstant.FIELD_CATEGORY_DEPTHNUMBER + ticketCategory.getDepth( ).getDepthNumber( ), ticketCategory.getLabel( ), Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_CATEGORY_DEPTHNUMBER + ticketCategory.getDepth( ).getDepthNumber( ), new BytesRef( ticketCategory.getLabel( ) ) ) );
            doc.add( new StringField( TicketSearchItemConstant.FIELD_CATEGORY_DEPTHNUMBER + ticketCategory.getDepth( ).getDepthNumber( ), manageNullValue( ticketCategory.getLabel( ) ), Store.YES ) );
        }            

        // --- ticket user title
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_USER_TITLE, manageNullValue( ticket.getUserTitle( ) ) ) );

        // --- ticket user firstname
        String strFirstname = manageNullValue( ticket.getFirstname( ) );
        doc.add( new TextField( TicketSearchItemConstant.FIELD_FIRSTNAME, strFirstname, Store.YES ) );
        doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_FIRSTNAME, new BytesRef( strFirstname ) ) );

        // --- ticket user lastname
        String strLastName = manageNullValue( ticket.getLastname( ) );
        doc.add( new TextField( TicketSearchItemConstant.FIELD_LASTNAME, strLastName, Store.YES ) );
        doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_LASTNAME, new BytesRef( strLastName ) ) );

        // --- ticket user email
        String strEmail = manageNullValue( ticket.getEmail( ) );
        doc.add( new StringField( TicketSearchItemConstant.FIELD_EMAIL, strEmail, Store.YES ) );
        doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_EMAIL, new BytesRef( strEmail ) ) );

        // --- ticket phone number
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_MOBILE_PHONE_NUMBER, manageNullValue( ticket.getMobilePhoneNumber( ) ) ) );
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_FIXED_PHONE_NUMBER, manageNullValue( ticket.getFixedPhoneNumber( ) ) ) );

        // --- ticket state
        if ( ticket.getState( ) != null )
        {
            int nIdState = ticket.getState( ).getId( );
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_STATE_ID, nIdState ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_STATE_ID, TicketSearchUtil.getBytesRef( BigInteger.valueOf( nIdState ) ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_STATE_ID, nIdState ) );

            String strState = manageNullValue( ticket.getState( ).getName( ) );
            doc.add( new TextField( TicketSearchItemConstant.FIELD_STATE, strState, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_STATE, new BytesRef( strState ) ) );
        }
        else
        {
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_STATE_ID, CONSTANT_ID_NULL ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_STATE_ID, TicketSearchUtil.getBytesRef( BigInteger.valueOf( CONSTANT_ID_NULL ) ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_STATE_ID, CONSTANT_ID_NULL ) );

            doc.add( new TextField( TicketSearchItemConstant.FIELD_STATE, StringUtils.EMPTY, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_STATE, new BytesRef( StringUtils.EMPTY ) ) );
        }

        // --- ticket channel
        if ( ticket.getChannel( ) != null )
        {
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_CHANNEL_ICONFONT, manageNullValue( ticket.getChannel( ).getIconFont( ) ) ) );

            String strChannelLabel = manageNullValue( ticket.getChannel( ).getLabel( ) );
            doc.add( new TextField( TicketSearchItemConstant.FIELD_CHANNEL_LABEL, strChannelLabel, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_CHANNEL_LABEL, new BytesRef( strChannelLabel ) ) );
        }
        else
        {
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_CHANNEL_ICONFONT, StringUtils.EMPTY ) );

            doc.add( new TextField( TicketSearchItemConstant.FIELD_CHANNEL_LABEL, StringUtils.EMPTY, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_CHANNEL_LABEL, new BytesRef( StringUtils.EMPTY ) ) );
        }

        // --- ticket assigneeUnit
        if ( ticket.getAssigneeUnit( ) != null )
        {
            int nAssigneeUnitId = ticket.getAssigneeUnit( ).getUnitId( );
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, nAssigneeUnitId ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, TicketSearchUtil.getBytesRef( BigInteger
                    .valueOf( nAssigneeUnitId ) ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, nAssigneeUnitId ) );

            String strAssigneeUnitName = manageNullValue( ticket.getAssigneeUnit( ).getName( ) );
            doc.add( new TextField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME, strAssigneeUnitName, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME, new BytesRef( strAssigneeUnitName ) ) );
        }
        else
        {
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, CONSTANT_ID_NULL ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, TicketSearchUtil.getBytesRef( BigInteger
                    .valueOf( CONSTANT_ID_NULL ) ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_ID, CONSTANT_ID_NULL ) );

            doc.add( new TextField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME, StringUtils.EMPTY, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNEE_UNIT_NAME, new BytesRef( StringUtils.EMPTY ) ) );
        }

        // --- ticket assigneeUser
        if ( ticket.getAssigneeUser( ) != null )
        {
            int nAdminUserId = ticket.getAssigneeUser( ).getAdminUserId( );
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID, nAdminUserId ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID, nAdminUserId ) );

            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_FIRSTNAME, manageNullValue( ticket.getAssigneeUser( ).getFirstname( ) ) ) );

            String strAssigneeUserLastname = manageNullValue( ticket.getAssigneeUser( ).getLastname( ) );
            doc.add( new TextField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME, strAssigneeUserLastname, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME, new BytesRef( strAssigneeUserLastname ) ) );
        }
        else
        {
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID, CONSTANT_ID_NULL ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_ADMIN_ID, CONSTANT_ID_NULL ) );

            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_FIRSTNAME, StringUtils.EMPTY ) );

            doc.add( new TextField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME, StringUtils.EMPTY, Store.YES ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNEE_USER_LASTNAME, new BytesRef( StringUtils.EMPTY ) ) );
        }

        // --- ticket assignerUnit
        if ( ticket.getAssignerUnit( ) != null )
        {
            int nAssignerUnitId = ticket.getAssignerUnit( ).getUnitId( );
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, nAssignerUnitId ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, TicketSearchUtil.getBytesRef( BigInteger
                    .valueOf( nAssignerUnitId ) ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, nAssignerUnitId ) );
        }
        else
        {
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, CONSTANT_ID_NULL ) );
            doc.add( new SortedDocValuesField( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, TicketSearchUtil.getBytesRef( BigInteger
                    .valueOf( CONSTANT_ID_NULL ) ) ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNER_UNIT_ID, CONSTANT_ID_NULL ) );
        }

        // --- ticket assignerUser
        if ( ticket.getAssignerUser( ) != null )
        {
            int nAssignerUserId = ticket.getAssignerUser( ).getAdminUserId( );
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID, nAssignerUserId ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID, nAssignerUserId ) );
        }
        else
        {
            doc.add( new IntPoint( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID, CONSTANT_ID_NULL ) );
            doc.add( new StoredField( TicketSearchItemConstant.FIELD_ASSIGNER_USER_ID, CONSTANT_ID_NULL ) );
        }

        // --- ticket read marker Id
        int nMarkingId = ticket.getIdTicketMarking( );
        doc.add( new IntPoint( TicketSearchItemConstant.FIELD_TICKET_MARKING_ID, nMarkingId ) );
        doc.add( new StoredField( TicketSearchItemConstant.FIELD_TICKET_MARKING_ID, nMarkingId ) );

        return doc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<String>( );
        listType.add( getDocumentType( ) );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSpecificSearchAppUrl( )
    {
        return JSP_SEARCH_TICKET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void indexTicket( Ticket ticket ) throws TicketIndexerException
    {
        IndexWriter indexWriter = null;
        try
        {
            indexWriter = TicketSearchService.getInstance( ).getTicketIndexWriter( false );
            indexTicket( indexWriter, ticket );
        }
        catch( LockObtainFailedException e )
        {
            // When a writer is already writing in the index directory
            throw new TicketIndexerException( );
        }
        catch( IOException | TicketIndexerException e )
        {
            AppLogService.error( "Error during the indexation of the ticket : " + e.getMessage( ), e );
            throw new TicketIndexerException( );
        }
        finally
        {
            TicketIndexWriterUtil.manageCloseWriter( indexWriter );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTicketIndex( int nTicketId ) throws TicketIndexerException
    {
        IndexWriter indexWriter = null;
        try
        {
            indexWriter = TicketSearchService.getInstance( ).getTicketIndexWriter( false );
            indexWriter.deleteDocuments( new Term( TicketSearchItemConstant.FIELD_DOCUMENT_ID, String.valueOf( nTicketId ) ) );
        }
        catch( LockObtainFailedException e )
        {
            // When a writer is already writing in the index directory
            throw new TicketIndexerException( );
        }
        catch( IOException e )
        {
            AppLogService.error( "Error during the indexation of the ticket : " + e.getMessage( ), e );
            throw new TicketIndexerException( );
        }
        finally
        {
            TicketIndexWriterUtil.manageCloseWriter( indexWriter );
        }
    }

    /**
     * Get Lucene index document type
     * 
     * @return The document type
     */
    public static String getDocumentType( )
    {
        return AppPropertiesService.getProperty( PROPERTY_DOCUMENT_TYPE );
    }

    /**
     * process indexation
     * 
     * @param indexWriter
     *            index writer
     * @param bCreate
     *            true if index must be created (full mode) false if incremental update
     * @param sbLogs
     *            log message
     * @throws IOException
     *             io error when creating/accessing index
     * @throws InterruptedException
     *             interrupted exception
     * @throws TicketIndexerException
     *
     */
    public synchronized void processIndexing( IndexWriter indexWriter, boolean bCreate, StringBuffer sbLogs ) throws TicketIndexerException
    {
        if ( !bCreate )
        {
            // incremental indexing
            // delete all record which must be deleted
            for ( IndexerAction action : IndexerActionHome.getAllIndexerActionByTask( IndexerAction.TASK_DELETE ) )
            {
                sbLogTicket( sbLogs, action.getIdTicket( ), IndexerAction.TASK_DELETE );

                try
                {
                    indexWriter.deleteDocuments( new Term( TicketSearchItemConstant.FIELD_DOCUMENT_ID, String.valueOf( action.getIdTicket( ) ) ) );
                    IndexerActionHome.remove( action.getIdAction( ) );
                }
                catch( IOException e )
                {
                    throw new TicketIndexerException( e.getMessage( ) );
                }
            }

            // add all record which must be added
            for ( IndexerAction action : IndexerActionHome.getAllIndexerActionByTask( IndexerAction.TASK_CREATE ) )
            {
                sbLogTicket( sbLogs, action.getIdTicket( ), IndexerAction.TASK_CREATE );

                Ticket ticket = TicketHome.findByPrimaryKey( action.getIdTicket( ) );

                if ( ticket != null )
                {
                    try
                    {
                        indexTicket( indexWriter, ticket );
                        IndexerActionHome.remove( action.getIdAction( ) );
                    }
                    catch( IOException e )
                    {
                        throw new TicketIndexerException( e.getMessage( ) );
                    }
                }
            }

            // Update all record which must be updated
            for ( IndexerAction action : IndexerActionHome.getAllIndexerActionByTask( IndexerAction.TASK_MODIFY ) )
            {
                sbLogTicket( sbLogs, action.getIdTicket( ), IndexerAction.TASK_MODIFY );

                Ticket ticket = TicketHome.findByPrimaryKey( action.getIdTicket( ) );

                try
                {
                    indexTicket( indexWriter, ticket );
                    IndexerActionHome.remove( action.getIdAction( ) );
                }
                catch( IOException e )
                {
                    throw new TicketIndexerException( e.getMessage( ) );
                }
            }
        }
        else
        {
            for ( Integer nIdticket : TicketHome.getIdTicketsList( ) )
            {
                try
                {
                    Ticket ticket = TicketHome.findByPrimaryKey( nIdticket.intValue( ) );
                    sbLogs.append( "Indexing Ticket" );
                    sbLogs.append( "\r\n" );
                    sbLogTicket( sbLogs, ticket.getId( ), IndexerAction.TASK_CREATE );
                    indexTicket( indexWriter, ticket );
                }
                catch( IOException e )
                {
                    throw new TicketIndexerException( e.getMessage( ) );
                }
            }
        }

        try
        {
            indexWriter.commit( );
        }
        catch( IOException e )
        {
            throw new TicketIndexerException( );
        }
    }

    /**
     * Indexing action performed on the recording
     * 
     * @param sbLogs
     *            the buffer log
     * @param nIdTicket
     *            the id of the ticket
     * @param nAction
     *            the indexer action key performed
     */
    private void sbLogTicket( StringBuffer sbLogs, int nIdTicket, int nAction )
    {
        sbLogs.append( "Indexing ticket:" );

        switch( nAction )
        {
            case IndexerAction.TASK_CREATE:
                sbLogs.append( "Insert " );

                break;

            case IndexerAction.TASK_MODIFY:
                sbLogs.append( "Modify " );

                break;

            case IndexerAction.TASK_DELETE:
                sbLogs.append( "Delete " );

                break;

            default:
                break;
        }

        if ( nIdTicket != CONSTANT_ID_NULL )
        {
            sbLogs.append( "id_ticket=" );
            sbLogs.append( nIdTicket );
        }

        sbLogs.append( "\r\n" );
    }

    /**
     * @param ticket
     *            ticket returns a string with content fields to be indexed
     * @return string with content fields to be indexed
     */
    private static String getContentForIndexer( Ticket ticket )
    {
        StringBuilder sb = new StringBuilder( );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy" );
        sb.append( ticket.getId( ) ).append( SEPARATOR );

        if ( StringUtils.isNotBlank( ticket.getReference( ) ) )
        {
            sb.append( ticket.getReference( ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotBlank( ticket.getFirstname( ) ) )
        {
            sb.append( ticket.getFirstname( ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotBlank( ticket.getLastname( ) ) )
        {
            sb.append( ticket.getLastname( ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotBlank( ticket.getEmail( ) ) )
        {
            sb.append( ticket.getEmail( ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotBlank( ticket.getFixedPhoneNumber( ) ) )
        {
            sb.append( ticket.getFixedPhoneNumber( ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotBlank( ticket.getMobilePhoneNumber( ) ) )
        {
            sb.append( ticket.getMobilePhoneNumber( ) ).append( SEPARATOR );
        }

        for ( TicketCategory ticketCategory : ticket.getBranch( ) ) 
        {
            if ( ticketCategory != null && StringUtils.isNotBlank( ticketCategory.getLabel( ) ) )
            {
                sb.append( ticketCategory.getLabel( ) ).append( SEPARATOR );
            }
        }            
        
        if ( StringUtils.isNotBlank( ticket.getNomenclature( ) ) )
        {
            sb.append( ticket.getNomenclature( ) ).append( SEPARATOR );
        }

        if ( ticket.getDateCreate( ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateCreate( ).getTime( ) ) ).append( SEPARATOR );
        }

        if ( ticket.getDateUpdate( ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateUpdate( ).getTime( ) ) ).append( SEPARATOR );
        }

        if ( ticket.getDateClose( ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateClose( ).getTime( ) ) ).append( SEPARATOR );
        }

        if ( ticket.getListResponse( ) != null )
        {
            for ( Response response : ticket.getListResponse( ) )
            {
                if ( StringUtils.isNotBlank( response.getResponseValue( ) ) )
                {
                    sb.append( response.getResponseValue( ) ).append( SEPARATOR );
                }
            }
        }

        if ( WorkflowService.getInstance( ).isAvailable( ) && ticket.getTicketCategory( ) != null )
        {
            State state = WorkflowService.getInstance( ).getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, ticket.getTicketCategory( ).getIdWorkflow( ),
                    null );

            if ( state != null && StringUtils.isNotBlank( state.getName( ) ) )
            {
                sb.append( state.getName( ) ).append( SEPARATOR );
            }
        }

        if ( ticket.getAssigneeUser( ) != null )
        {
            AssigneeUser assigneeUser = ticket.getAssigneeUser( );

            if ( StringUtils.isNotBlank( assigneeUser.getFirstname( ) ) )
            {
                sb.append( assigneeUser.getFirstname( ) ).append( SEPARATOR );
            }

            if ( StringUtils.isNotBlank( assigneeUser.getLastname( ) ) )
            {
                sb.append( assigneeUser.getLastname( ) ).append( SEPARATOR );
            }
        }

        if ( ticket.getChannel( ) != null && StringUtils.isNotBlank( ticket.getChannel( ).getLabel( ) ) )
        {
            sb.append( ticket.getChannel( ).getLabel( ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotBlank( ticket.getTicketComment( ) ) )
        {
            sb.append( ticket.getTicketComment( ) ).append( SEPARATOR );
        }

        if ( ticket.getAssigneeUnit( ) != null && StringUtils.isNotBlank( ticket.getAssigneeUnit( ).getName( ) ) )
        {
            sb.append( ticket.getAssigneeUnit( ).getName( ) ).append( SEPARATOR );
        }

        return sb.toString( );
    }

    /**
     * Manages the case the specified String is {@code null}
     * 
     * @param strValue
     *            the String to manage
     * @return the correct String when the specified String is {@code null}, {@code strValue} otherwise
     */
    private static String manageNullValue( String strValue )
    {
        return ( strValue == null ) ? StringUtils.EMPTY : strValue;
    }
}
