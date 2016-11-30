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

import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.util.PluginConfigurationService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.search.TicketSearchItem;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import org.jsoup.Jsoup;

import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;


/**
 * Ticket Indexer
 *
 */
public class TicketIndexer implements SearchIndexer, ITicketSearchIndexer
{
    public static final String PROPERTY_INDEXER_NAME = "ticketing.indexer.name";
    private static final String SHORT_NAME_TICKET = "tck";
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
    public String getDescription(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Document> getDocuments( String strDocument )
        throws IOException, InterruptedException, SiteMessageException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<org.apache.lucene.document.Document>(  );
        String strPortalUrl = AppPropertiesService.getProperty( PROPERTY_PAGE_BASE_URL );
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );

        Ticket ticket = TicketHome.findByPrimaryKey( Integer.parseInt( strDocument ) );

        if ( ticket != null )
        {
            UrlItem urlTicket = new UrlItem( strPortalUrl );
            urlTicket.addParameter( XPageAppService.PARAM_XPAGE_APP, PLUGIN_NAME );
            urlTicket.addParameter( TicketingConstants.PARAMETER_ID_TICKET, ticket.getId(  ) );

            org.apache.lucene.document.Document docSubject = getDocument( ticket, urlTicket.getUrl(  ), plugin );
            listDocs.add( docSubject );
        }

        return listDocs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getVersion(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void indexDocuments(  ) throws IOException, InterruptedException, SiteMessageException
    {
        TicketSearchService.getInstance(  ).processIndexing( true );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isEnable(  )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) &&
                ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString(  ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) ) &&
                PluginService.isPluginEnable( PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * Index the ticket
     * @param indexWriter index writer
     * @param ticket The ticket
     * @throws IOException if an IO error occurs
     * @throws InterruptedException if a Thread error occurs
     */
    public void indexTicket( IndexWriter indexWriter, Ticket ticket )
        throws IOException, InterruptedException
    {
        Plugin plugin = PluginService.getPlugin( PLUGIN_NAME );
        UrlItem urlTicket = new UrlItem( JSP_VIEW_TICKET );
        urlTicket.addParameter( PARAMETER_TICKET_ID, ticket.getId(  ) );

        Document docTicket = null;

        try
        {
            docTicket = getDocument( ticket, urlTicket.getUrl(  ), plugin );
        }
        catch ( Exception e )
        {
            String strMessage = "Ticket ID : " + ticket.getId(  );
            IndexationService.error( this, e, strMessage );
        }

        if ( docTicket != null )
        {
            indexWriter.addDocument( docTicket );
        }
    }

    /**
     * Get a document for indexing
     * @param ticket The ticket
     * @param strUrl The URL
     * @param plugin The plugin
     * @return The document
     * @throws IOException if an IO error occurs
     * @throws InterruptedException if a Thread error occurs
     */
    public static Document getDocument( Ticket ticket, String strUrl, Plugin plugin )
        throws IOException, InterruptedException
    {
        // make a new, empty document
        Document doc = new Document(  );

        // Add the uid as a field, so that index can be incrementally
        // maintained.
        // This field is not stored with question/answer, it is indexed, but it
        // is not
        // tokenized prior to indexing.
        String strIdTicket = String.valueOf( ticket.getId(  ) );
        doc.add( new Field( TicketSearchItem.FIELD_UID, strIdTicket + "_" + SHORT_NAME_TICKET, TextField.TYPE_NOT_STORED ) );
        doc.add( new Field( TicketSearchItem.FIELD_TICKET_ID, strIdTicket, TextField.TYPE_STORED ) );
        doc.add( new TextField( TicketSearchItem.FIELD_CONTENTS, getContentForIndexer( ticket ), Store.NO ) );
        doc.add( new TextField( TicketSearchItem.FIELD_CATEGORY, ticket.getTicketCategory(  ), Store.YES ) );
        doc.add( new TextField( TicketSearchItem.FIELD_DOMAIN, ticket.getTicketDomain(  ), Store.YES ) );
        doc.add( new TextField( TicketSearchItem.FIELD_REFERENCE, ticket.getReference(  ), Store.YES ) );

        String strDate = DateTools.dateToString( ticket.getDateCreate(  ), DateTools.Resolution.DAY );
        doc.add( new Field( TicketSearchItem.FIELD_DATE, strDate, TextField.TYPE_NOT_STORED ) );
        doc.add( new TextField( TicketSearchItem.FIELD_COMMENT, ticket.getTicketComment(  ), Store.YES ) );

        //add response for closed tickets with response 
        if ( ( getStateId( ticket ) == PluginConfigurationService.getInt( 
                    PluginConfigurationService.PROPERTY_STATE_CLOSED_ID, TicketingConstants.PROPERTY_UNSET_INT ) ) &&
                StringUtils.isNotEmpty( ticket.getUserMessage(  ) ) )
        {
            doc.add( new TextField( TicketSearchItem.FIELD_RESPONSE, ticket.getUserMessage(  ), Store.YES ) );
            doc.add( new TextField( TicketSearchItem.FIELD_TXT_RESPONSE,
                    Jsoup.parse( ticket.getUserMessage(  ) ).text(  ), Store.NO ) );
        }
        else
        {
            doc.add( new TextField( TicketSearchItem.FIELD_TXT_RESPONSE, StringUtils.EMPTY, Store.NO ) );
        }

        doc.add( new TextField( TicketSearchItem.FIELD_SUMMARY, getDisplaySummary( ticket ), Store.YES ) );
        doc.add( new TextField( TicketSearchItem.FIELD_TITLE, getDisplayTitle( ticket ), Store.YES ) );
        doc.add( new TextField( TicketSearchItem.FIELD_TYPE, getDocumentType(  ), Store.YES ) );
        if (ticket.getNomenclature() != null){
        	doc.add( new TextField (TicketSearchItem.FIELD_TICKET_NOMENCLATURE, ticket.getNomenclature(), Store.YES) );
        }

        return doc;
    }

    /**
     * return state id of ticket
     * @param ticket ticket
     * @return id of ticket state
     */
    private static int getStateId( Ticket ticket )
    {
        int nStateId = 0;

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
            int nIdWorkflow = ticketCategory.getIdWorkflow(  );

            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( nIdWorkflow );

            State state = WorkflowService.getInstance(  )
                                         .getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );

            if ( state != null )
            {
                nStateId = state.getId(  );
            }
        }

        return nStateId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getListType(  )
    {
        List<String> listType = new ArrayList<String>(  );
        listType.add( getDocumentType(  ) );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSpecificSearchAppUrl(  )
    {
        return JSP_SEARCH_TICKET;
    }

    /**
     * Get Lucene index document type
     * @return The document type
     */
    public static String getDocumentType(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_DOCUMENT_TYPE );
    }

    /**
     * process indexation
     * @param indexWriter index writer
     * @param bCreate true if index must be created (full mode) false if incremental update
     * @param sbLogs log message
     * @throws IOException io error when creating/accessing index
     * @throws InterruptedException interrupted exception
     *
     */
    public synchronized void processIndexing( IndexWriter indexWriter, boolean bCreate, StringBuffer sbLogs )
        throws IOException, InterruptedException
    {
        List<Integer> listIdTicket = new ArrayList<Integer>(  );

        if ( !bCreate )
        {
            //incremental indexing
            //delete all record which must be deleted
            for ( IndexerAction action : IndexerActionHome.getAllIndexerActionByTask( IndexerAction.TASK_DELETE ) )
            {
                sbLogTicket( sbLogs, action.getIdTicket(  ), IndexerAction.TASK_DELETE );

                Term term = new Term( TicketSearchItem.FIELD_TICKET_ID, Integer.toString( action.getIdTicket(  ) ) );
                Term[] terms = { term };

                indexWriter.deleteDocuments( terms );
                IndexerActionHome.remove( action.getIdAction(  ) );
            }

            //add all record which must be added
            for ( IndexerAction action : IndexerActionHome.getAllIndexerActionByTask( IndexerAction.TASK_CREATE ) )
            {
                sbLogTicket( sbLogs, action.getIdTicket(  ), IndexerAction.TASK_CREATE );
                listIdTicket.add( action.getIdTicket(  ) );

                Ticket ticket = TicketHome.findByPrimaryKey( action.getIdTicket(  ) );

                if ( ticket != null )
                {
                    indexTicket( indexWriter, ticket );
                    IndexerActionHome.remove( action.getIdAction(  ) );
                }
            }

            //Update all record which must be updated
            for ( IndexerAction action : IndexerActionHome.getAllIndexerActionByTask( IndexerAction.TASK_MODIFY ) )
            {
                sbLogTicket( sbLogs, action.getIdTicket(  ), IndexerAction.TASK_MODIFY );

                Ticket ticket = TicketHome.findByPrimaryKey( action.getIdTicket(  ) );
                Term term = new Term( TicketSearchItem.FIELD_TICKET_ID, Integer.toString( action.getIdTicket(  ) ) );
                Term[] terms = { term };
                indexWriter.deleteDocuments( terms );

                listIdTicket.add( action.getIdTicket(  ) );
                indexTicket( indexWriter, ticket );
                IndexerActionHome.remove( action.getIdAction(  ) );
            }
        }
        else
        {
            for ( Integer nIdticket : TicketHome.getIdTicketsList(  ) )
            {
                Ticket ticket = TicketHome.findByPrimaryKey( nIdticket.intValue(  ) );
                sbLogs.append( "Indexing Ticket" );
                sbLogs.append( "\r\n" );
                sbLogTicket( sbLogs, ticket.getId(  ), IndexerAction.TASK_CREATE );
                indexTicket( indexWriter, ticket );
            }
        }

        indexWriter.commit(  );
    }

    /**
     * Indexing action performed on the recording
     * @param sbLogs the buffer log
     * @param nIdTicket the id of the ticket
     * @param nAction the indexer action key performed
     */
    private void sbLogTicket( StringBuffer sbLogs, int nIdTicket, int nAction )
    {
        sbLogs.append( "Indexing ticket:" );

        switch ( nAction )
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
     * @param ticket ticket
     * returns a string with content fields to be indexed
     * @return string with content fields to be indexed
     */
    private static String getContentForIndexer( Ticket ticket )
    {
        StringBuilder sb = new StringBuilder(  );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm" );
        sb.append( ticket.getId(  ) ).append( SEPARATOR );

        if ( StringUtils.isNotEmpty( ticket.getReference(  ) ) )
        {
            sb.append( ticket.getReference(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getFirstname(  ) ) )
        {
            sb.append( ticket.getFirstname(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getLastname(  ) ) )
        {
            sb.append( ticket.getLastname(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getEmail(  ) ) )
        {
            sb.append( ticket.getEmail(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getFixedPhoneNumber(  ) ) )
        {
            sb.append( ticket.getFixedPhoneNumber(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getMobilePhoneNumber(  ) ) )
        {
            sb.append( ticket.getMobilePhoneNumber(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketType(  ) ) )
        {
            sb.append( ticket.getTicketType(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketDomain(  ) ) )
        {
            sb.append( ticket.getTicketDomain(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketCategory(  ) ) )
        {
            sb.append( ticket.getTicketCategory(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getNomenclature(  ) ) )
        {
            sb.append( ticket.getNomenclature(  ) ).append( SEPARATOR );
        }
        
        if ( ticket.getDateCreate(  ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateCreate(  ).getTime(  ) ) ).append( SEPARATOR );
        }

        if ( ticket.getDateUpdate(  ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateUpdate(  ).getTime(  ) ) ).append( SEPARATOR );
        }

        if ( ticket.getDateClose(  ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateClose(  ).getTime(  ) ) ).append( SEPARATOR );
        }

        if ( ticket.getListResponse(  ) != null )
        {
            for ( Response response : ticket.getListResponse(  ) )
            {
                if ( response.getResponseValue(  ) != null )
                {
                    sb.append( response.getResponseValue(  ) ).append( SEPARATOR );
                }
            }
        }

        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
            int nIdWorkflow = ticketCategory.getIdWorkflow(  );

            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( nIdWorkflow );

            State state = WorkflowService.getInstance(  )
                                         .getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticket.getIdTicketCategory(  ) );
            sb.append( state.getName(  ) ).append( SEPARATOR );
        }

        if ( ticket.getAssigneeUser(  ) != null )
        {
            sb.append( ticket.getAssigneeUser(  ).getFirstname(  ) ).append( SEPARATOR )
              .append( ticket.getAssigneeUser(  ).getLastname(  ) ).append( SEPARATOR );
        }

        if ( ticket.getChannel(  ) != null )
        {
            sb.append( ticket.getChannel(  ).getLabel(  ) ).append( SEPARATOR );
        }
        
        if (StringUtils.isNotEmpty( ticket.getTicketComment(  ) ) )
        {
            sb.append( ticket.getTicketComment(  ) ).append( SEPARATOR );
        }

        if ( ticket.getAssigneeUnit(  ) != null )
        {
        	if (StringUtils.isNotEmpty( ticket.getAssigneeUnit( ).getName(  ) ) )
        	{
        		sb.append( ticket.getAssigneeUnit(  ).getName(  ) ).append( SEPARATOR );
        	}
        }
        return sb.toString(  );
    }

    /**
     * @param ticket ticket
     * @return summary of ticket
     * returns summary of ticket to be displayed
     */
    private static String getDisplaySummary( Ticket ticket )
    {
        StringBuilder sb = new StringBuilder(  );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm" );

        if ( ticket.getDateCreate(  ) != null )
        {
            sb.append( simpleDateFormat.format( ticket.getDateCreate(  ).getTime(  ) ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getReference(  ) ) )
        {
            sb.append( ticket.getReference(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getFirstname(  ) ) )
        {
            sb.append( ticket.getFirstname(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getLastname(  ) ) )
        {
            sb.append( ticket.getLastname(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketType(  ) ) )
        {
            sb.append( ticket.getTicketType(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketDomain(  ) ) )
        {
            sb.append( ticket.getTicketDomain(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketCategory(  ) ) )
        {
            sb.append( ticket.getTicketCategory(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketComment(  ) ) )
        {
            sb.append( ticket.getTicketComment(  ) ).append( SEPARATOR );
        }

        return sb.toString(  );
    }

    /**
     * @param ticket ticket
     * @return title of ticket
     * returns title of ticket to be displayed
     */
    private static String getDisplayTitle( Ticket ticket )
    {
        StringBuilder sb = new StringBuilder(  );

        if ( StringUtils.isNotEmpty( ticket.getReference(  ) ) )
        {
            sb.append( ticket.getReference(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getFirstname(  ) ) )
        {
            sb.append( ticket.getFirstname(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getLastname(  ) ) )
        {
            sb.append( ticket.getLastname(  ) ).append( SEPARATOR );
        }

        if ( StringUtils.isNotEmpty( ticket.getTicketCategory(  ) ) )
        {
            sb.append( ticket.getTicketCategory(  ) ).append( SEPARATOR );
        }

        return sb.toString(  );
    }
}
