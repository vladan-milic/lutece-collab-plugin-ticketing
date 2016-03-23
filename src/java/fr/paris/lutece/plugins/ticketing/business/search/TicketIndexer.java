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
package fr.paris.lutece.plugins.ticketing.business.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.jsoup.Jsoup;

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.TicketHome;
import fr.paris.lutece.plugins.ticketing.service.TicketingPlugin;
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


/**
 * Ticket Indexer
 *
 */
public class TicketIndexer implements SearchIndexer, ITicketSearchIndexer
{
    public  static final String PROPERTY_INDEXER_NAME = "ticketing.indexer.name";
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
        TicketSearchService.getInstance( ).processIndexing( true );
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
    public void indexTicket( IndexWriter indexWriter, Ticket ticket ) throws IOException, InterruptedException
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
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document(  );

        FieldType ft = new FieldType( StringField.TYPE_STORED );
        ft.setOmitNorms( false );

        FieldType ftNotStored = new FieldType( StringField.TYPE_NOT_STORED );
        ftNotStored.setOmitNorms( false );
        ftNotStored.setTokenized( false );

        // Add the uid as a field, so that index can be incrementally
        // maintained.
        // This field is not stored with question/answer, it is indexed, but it
        // is not
        // tokenized prior to indexing.
        String strIdTicket = String.valueOf( ticket.getId(  ) );
        doc.add( new Field( TicketSearchItem.FIELD_UID, strIdTicket + "_" + SHORT_NAME_TICKET, ftNotStored ) );
        doc.add( new Field( TicketSearchItem.FIELD_TICKET_ID, strIdTicket, TextField.TYPE_STORED ) );
        doc.add( new Field( TicketSearchItem.FIELD_CONTENTS, ticket.toString(  ).toLowerCase(  ),
                TextField.TYPE_NOT_STORED ) );

        doc.add( new Field( TicketSearchItem.FIELD_CATEGORY, ticket.getTicketCategory(  ), TextField.TYPE_STORED ) );
        doc.add( new Field( TicketSearchItem.FIELD_REFERENCE, ticket.getReference(  ), TextField.TYPE_STORED ) );

        String strDate = DateTools.dateToString( ticket.getDateCreate(  ), DateTools.Resolution.DAY );
        doc.add( new Field( TicketSearchItem.FIELD_DATE, strDate, ftNotStored ) );

        //add response for closed tickets with response 
        if ( ( getStateId( ticket ) == AppPropertiesService.getPropertyInt( 
                    TicketingConstants.PROPERTY_TICKET_CLOSE_ID, -1 ) ) &&
                StringUtils.isNotEmpty( ticket.getUserMessage(  ) ) )
        {
            //escape html from response for indexation, not stored
            doc.add( new Field( TicketSearchItem.FIELD_TXT_RESPONSE,
                    Jsoup.parse( ticket.getUserMessage(  ) ).text(  ).toLowerCase(  ), ftNotStored ) );
            doc.add( new Field( TicketSearchItem.FIELD_RESPONSE, ticket.getUserMessage(  ), ft ) );
            doc.add( new Field( TicketSearchItem.FIELD_TXT_COMMENT, ticket.getTicketComment(  ).toLowerCase(  ),
                    ftNotStored ) );
            doc.add( new Field( TicketSearchItem.FIELD_COMMENT, ticket.getTicketComment(  ), ft ) );           
        }

        doc.add( new Field( TicketSearchItem.FIELD_SUMMARY, ticket.getDisplaySummary(  ), ft ) );
        doc.add( new Field( TicketSearchItem.FIELD_TITLE, ticket.getDisplayTitle(  ), ft ) );
        doc.add( new Field( TicketSearchItem.FIELD_TYPE, getDocumentType(  ), ft ) );

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

            State state = WorkflowService.getInstance(  ).getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
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
            Plugin plugin = PluginService.getPlugin( TicketingPlugin.PLUGIN_NAME );
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
                    Ticket ticket = TicketHome.findByPrimaryKey( action.getIdTicket( ) );
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
                    Ticket ticket = TicketHome.findByPrimaryKey( action.getIdTicket( ) );
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
                for ( Ticket ticket : TicketHome.getTicketsList(  ) )
                {
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
}
