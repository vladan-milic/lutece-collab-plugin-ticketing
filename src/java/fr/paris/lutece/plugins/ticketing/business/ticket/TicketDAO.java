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
package fr.paris.lutece.plugins.ticketing.business.ticket;

import fr.paris.lutece.plugins.ticketing.business.address.TicketAddress;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUser;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.channel.Channel;
import fr.paris.lutece.plugins.ticketing.service.category.TicketCategoryService;
import fr.paris.lutece.plugins.ticketing.web.TicketingConstants;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides Data Access methods for Ticket objects
 */
public final class TicketDAO implements ITicketDAO
{
    // Constants
    private static final String TICKET_RESOURCE_TYPE = "ticket";

    // SQL Queries
    private static final String SQL_SELECT_WITH_JOIN_DATA_TICKET = "SELECT a.id_ticket, a.ticket_reference, a.guid, a.id_user_title, b.label, a.firstname, a.lastname, a.email, "
            + " a.fixed_phone_number, a.mobile_phone_number, a.id_marking, "
            + " a.id_ticket_category, a.id_contact_mode, f.code, a.ticket_comment, "
            + " a.ticket_status, a.ticket_status_text, a.date_update, a.date_create, a.date_close, a.priority, a.criticality, a.id_customer, a.id_admin_user, g.first_name, g.last_name, a.id_unit, h.label, a.id_assigner_user, a.id_assigner_unit, h2.label, a.user_message, a.url, a.id_channel, x.label, x.icon_font, a.nomenclature, "
            + " ad.address, ad.address_detail, ad.postal_code, ad.city"
            + " FROM ticketing_ticket a"
            + " LEFT JOIN core_admin_user g ON g.id_user=a.id_admin_user"
            + " LEFT JOIN unittree_unit h ON h.id_unit=a.id_unit"
            + " LEFT JOIN unittree_unit h2 ON h2.id_unit=a.id_assigner_unit"
            + " LEFT JOIN ticketing_ticket_address ad ON ad.id_ticket=a.id_ticket"
            + " JOIN ticketing_user_title b ON a.id_user_title = b.id_user_title"
            + " JOIN ticketing_contact_mode f ON a.id_contact_mode = f.id_contact_mode" + " JOIN ticketing_channel x ON a.id_channel = x.id_channel";
    
    private static final String SQL_SELECT_ALL_ID_TICKET = "SELECT a.id_ticket " + " FROM ticketing_ticket a"
            + " LEFT JOIN core_admin_user g ON g.id_user=a.id_admin_user" + " LEFT JOIN unittree_unit h ON h.id_unit=a.id_unit"
            + " LEFT JOIN ticketing_ticket_address ad ON ad.id_ticket=a.id_ticket" + " JOIN ticketing_user_title b ON a.id_user_title = b.id_user_title"
            + " JOIN ticketing_contact_mode f ON a.id_contact_mode = f.id_contact_mode" + " JOIN ticketing_channel x ON a.id_channel = x.id_channel";
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_ticket ) FROM ticketing_ticket";
    private static final String SQL_QUERY_SELECT = SQL_SELECT_WITH_JOIN_DATA_TICKET + " WHERE a.id_ticket = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket ( id_ticket, ticket_reference , guid, id_user_title, firstname, lastname, email, "
            + " fixed_phone_number, mobile_phone_number, id_ticket_category, "
            + " id_contact_mode, ticket_comment, ticket_status, ticket_status_text, date_update, date_create, "
            + " priority, criticality, id_customer, id_admin_user, id_unit, id_assigner_user, id_assigner_unit, user_message, url, id_channel, nomenclature ) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE t, ad FROM ticketing_ticket t LEFT JOIN ticketing_ticket_address ad ON ad.id_ticket = t.id_ticket WHERE t.id_ticket = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket SET id_ticket = ?, ticket_reference = ?, guid = ?, id_user_title = ?, firstname = ?, lastname = ?, email = ?, fixed_phone_number = ?, mobile_phone_number = ?, "
            + " id_ticket_category = ?, id_contact_mode = ?, ticket_comment = ?, ticket_status = ?, ticket_status_text = ?, date_update = ?,"
            + " date_close = ? , priority = ? , criticality = ? , id_customer = ? , id_admin_user = ? , id_unit = ?, id_assigner_user = ? , id_assigner_unit = ?, user_message = ?, url = ?, id_channel = ?, nomenclature = ? "
            + " WHERE id_ticket = ?";
    private static final String SQL_QUERY_SELECTALL_SELECT_CLAUSE = SQL_SELECT_WITH_JOIN_DATA_TICKET;
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket FROM ticketing_ticket";
    private static final String SQL_FILTER_STATUS = " AND a.ticket_status = UPPER(?) ";
    private static final String SQL_FILTER_ID_TICKET = " AND  a.id_ticket= ? ";
    private static final String SQL_FILTER_CREATION_DATE = " AND a.date_create >= ? AND a.date_create < ? ";
    private static final String SQL_FILTER_CREATION_START_DATE = " AND a.date_create >= ? ";
    private static final String SQL_FILTER_CREATION_END_DATE = " AND a.date_create <= ? ";
    private static final String SQL_FILTER_LASTUPDATE_DATE = " AND a.date_update >= ? AND a.date_update < ? ";
    private static final String SQL_FILTER_LASTUPDATE_START_DATE = " AND a.date_update >= ? ";
    private static final String SQL_FILTER_LASTUPDATE_END_DATE = " AND a.date_update <= ? ";
    private static final String SQL_FILTER_ID_USER = "  AND a.guid = ? ";
    private static final String SQL_FILTER_ID_CHANNEL = " AND x.id_channel = ? ";
    private static final String SQL_FILTER_NOMENCLATURE = " AND a.nomenclature = ? ";
    private static final String SQL_FILTER_EMAIL = " AND a.email = UPPER(?) ";
    private static final String SQL_FILTER_LASTNAME = " AND a.lastname = UPPER(?) ";
    private static final String SQL_FILTER_FIRSTNAME = " AND a.firstname = UPPER(?) ";
    private static final String SQL_FILTER_FIXED_PHONE_NUMBER = " AND a.fixed_phone_number = ? ";
    private static final String SQL_FILTER_MOBILE_PHONE_NUMBER = " AND a.mobile_phone_number = ? ";
    private static final String SQL_FILTER_CLOSE_DATE = " AND a.date_close <= ? AND a.date_close < ? ";
    private static final String SQL_FILTER_URGENCY = " AND ( ( a.priority = ? AND a.criticality <= a.priority)  OR ( a.criticality = ? AND a.priority  <= a.criticality) )";
    private static final String SQL_FILTER_ID_SINGLE_STATE = " AND j.id_state = ? ";
    private static final String SQL_FILTER_ID_MULTIPLE_STATES_START = " AND ( ";
    private static final String SQL_FILTER_ID_STATE = " j.id_state = ? ";
    private static final String CONSTANT_OPEN_PARENTHESIS = " ( ";
    private static final String CONSTANT_CLOSE_PARENTHESIS = " ) ";
    private static final String CONSTANT_OR = " OR ";
    private static final String CONSTANT_AND = " AND ";
    private static final String SQL_FILTER_VIEW_AGENT = " AND ( a.id_admin_user = ? OR a.id_assigner_user = ? ) ";
    private static final String SQL_FILTER_VIEW_GROUP = " AND a.id_admin_user != ? AND a.id_assigner_user != ? ";
    private static final String SQL_FILTER_VIEW_GROUP_UNIT_ASSIGNEE = " a.id_unit IN ( ";
    private static final String SQL_FILTER_VIEW_GROUP_UNIT_ASSIGNER = " a.id_assigner_unit IN ( ";
    private static final String SQL_FILTER_VIEW_DOMAIN = " AND a.id_admin_user != ? AND a.id_assigner_user != ? ";
    private static final String SQL_FILTER_VIEW_DOMAIN_UNIT_ASSIGNEE = " a.id_unit NOT IN ( ";
    private static final String SQL_FILTER_VIEW_DOMAIN_UNIT_ASSIGNER = " a.id_assigner_unit NOT IN ( ";
    private static final String SQL_FILTER_LIMIT = " LIMIT ?,? ";

    private static final String SQL_SELECT_ALL_WORKFLOW_JOIN_CLAUSE = " LEFT JOIN  workflow_resource_workflow i ON i.id_resource=a.id_ticket"
            + " LEFT JOIN workflow_state j ON j.id_state=i.id_state";
    private static final String SQL_SELECT_ALL_WORKFLOW_WHERE_CLAUSE = " WHERE (i.id_resource IS NULL OR i.resource_type='" + TICKET_RESOURCE_TYPE + "')";

    // SQL commands to manage ticket's generic attributes responses
    private static final String SQL_QUERY_INSERT_TICKET_RESPONSE = "INSERT INTO ticketing_ticket_response (id_ticket, id_response) VALUES (?,?)";
    private static final String SQL_QUERY_SELECT_TICKET_RESPONSE_LIST = "SELECT id_response FROM ticketing_ticket_response WHERE id_ticket = ?";
    private static final String SQL_QUERY_DELETE_TICKET_RESPONSE          = "DELETE a, b, c, d FROM ticketing_ticket_response a"
            + " JOIN genatt_response b ON b.id_response = a.id_response LEFT JOIN core_file c ON c.id_file = b.id_file"
            + " LEFT JOIN core_physical_file d ON d.id_physical_file = c.id_physical_file  WHERE a.id_ticket = ? ";
    private static final String SQL_QUERY_REMOVE_FROM_ID_RESPONSE = "DELETE a, b, c, d FROM ticketing_ticket_response a "
            + " JOIN genatt_response b ON b.id_response = a.id_response " + " LEFT JOIN core_file c ON c.id_file = b.id_file"
            + " LEFT JOIN core_physical_file d ON d.id_physical_file = c.id_physical_file  WHERE a.id_response = ? ";
    private static final String SQL_QUERY_FIND_ID_TICKET_FROM_ID_RESPONSE = " SELECT id_ticket FROM ticketing_ticket_response WHERE id_response = ? ";
    private static final String SQL_QUERY_INSERT_TICKET_ADDRESS           = "INSERT INTO ticketing_ticket_address ( id_ticket, address, address_detail, postal_code, city)"
            + " VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_UPDATE_TICKET_ADDRESS = "UPDATE ticketing_ticket_address SET  address = ?, address_detail = ?, postal_code = ?, city = ?"
            + " WHERE id_ticket = ?";

    private static final String SQL_QUERY_SELECT_TICKET_ADDRESS           = "SELECT id_ticket from ticketing_ticket_address WHERE id_ticket = ? ";
    private static final String SQL_QUERY_UPDATE_ID_MARKING               = "UPDATE ticketing_ticket SET id_marking = ? WHERE id_ticket = ?";
    private static final String SQL_QUERY_SELECT_ID_MARKING               = "SELECT id_marking FROM ticketing_ticket WHERE id_ticket = ?";
    private static final String SQL_QUERY_RESET_ID_MARKING = "UPDATE ticketing_ticket SET id_marking = " + TicketingConstants.DEFAULT_MARKING
            + " WHERE id_marking = ?";
    private static final String SQL_QUERY_SELECT_TICKET_BY_ID_UNIT        = SQL_QUERY_SELECTALL_SELECT_CLAUSE + " WHERE a.id_unit = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey = 1;

        if ( daoUtil.next( ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free( );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void insert( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticket.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticket.getId( ) );
        daoUtil.setString( nIndex++, ticket.getReference( ) );
        daoUtil.setString( nIndex++, ticket.getGuid( ) );
        daoUtil.setInt( nIndex++, ticket.getIdUserTitle( ) );
        daoUtil.setString( nIndex++, ticket.getFirstname( ) );
        daoUtil.setString( nIndex++, ticket.getLastname( ) );
        daoUtil.setString( nIndex++, ticket.getEmail( ) );
        daoUtil.setString( nIndex++, ticket.getFixedPhoneNumber( ) );
        daoUtil.setString( nIndex++, ticket.getMobilePhoneNumber( ) );
        daoUtil.setInt( nIndex++, ticket.getTicketCategory( ).getId( ) );
        daoUtil.setInt( nIndex++, ticket.getIdContactMode( ) );
        daoUtil.setString( nIndex++, ticket.getTicketComment( ) );
        daoUtil.setInt( nIndex++, ticket.getTicketStatus( ) );
        daoUtil.setString( nIndex++, ticket.getTicketStatusText( ) );

        ticket.setDateUpdate( new Timestamp( new java.util.Date( ).getTime( ) ) );
        ticket.setDateCreate( new Timestamp( new java.util.Date( ).getTime( ) ) );

        daoUtil.setTimestamp( nIndex++, ticket.getDateUpdate( ) );
        daoUtil.setTimestamp( nIndex++, ticket.getDateCreate( ) );
        daoUtil.setInt( nIndex++, ticket.getPriority( ) );
        daoUtil.setInt( nIndex++, ticket.getCriticality( ) );
        daoUtil.setString( nIndex++, ticket.getCustomerId( ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssigneeUser( ) != null ) ? ticket.getAssigneeUser( ).getAdminUserId( ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssigneeUnit( ) != null ) ? ticket.getAssigneeUnit( ).getUnitId( ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssignerUser( ) != null ) ? ticket.getAssignerUser( ).getAdminUserId( ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssignerUnit( ) != null ) ? ticket.getAssignerUnit( ).getUnitId( ) : ( -1 ) );
        daoUtil.setString( nIndex++, ticket.getUserMessage( ) );
        daoUtil.setString( nIndex++, ticket.getUrl( ) );

        if ( ( ticket.getChannel( ) != null ) && TicketUtils.isIdSet( ticket.getChannel( ).getId( ) ) )
        {
            daoUtil.setInt( nIndex++, ticket.getChannel( ).getId( ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        daoUtil.setString( nIndex++, ticket.getNomenclature( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        TicketAddress _ticketAddress = ticket.getTicketAddress( );
        if ( _ticketAddress != null
                && ( StringUtils.isNotBlank( _ticketAddress.getAddress( ) ) || StringUtils.isNotBlank( _ticketAddress.getAddressDetail( ) )
                        || StringUtils.isNotBlank( _ticketAddress.getPostalCode( ) ) || StringUtils.isNotBlank( _ticketAddress.getCity( ) ) ) )
        {
            storeTicketAddress( ticket, plugin );
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Ticket load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery( );

        Ticket ticket = null;

        if ( daoUtil.next( ) )
        {
            ticket = dataToTicket( daoUtil );
        }

        daoUtil.free( );

        return ticket;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticket.getId( ) );
        daoUtil.setString( nIndex++, ticket.getReference( ) );
        daoUtil.setString( nIndex++, ticket.getGuid( ) );
        daoUtil.setInt( nIndex++, ticket.getIdUserTitle( ) );
        daoUtil.setString( nIndex++, ticket.getFirstname( ) );
        daoUtil.setString( nIndex++, ticket.getLastname( ) );
        daoUtil.setString( nIndex++, ticket.getEmail( ) );
        daoUtil.setString( nIndex++, ticket.getFixedPhoneNumber( ) );
        daoUtil.setString( nIndex++, ticket.getMobilePhoneNumber( ) );
        daoUtil.setInt( nIndex++, ticket.getTicketCategory( ).getId( ) );
        daoUtil.setInt( nIndex++, ticket.getIdContactMode( ) );
        daoUtil.setString( nIndex++, ticket.getTicketComment( ) );
        daoUtil.setInt( nIndex++, ticket.getTicketStatus( ) );
        daoUtil.setString( nIndex++, ticket.getTicketStatusText( ) );

        ticket.setDateUpdate( new Timestamp( new java.util.Date( ).getTime( ) ) );
        daoUtil.setTimestamp( nIndex++, ticket.getDateUpdate( ) );
        daoUtil.setTimestamp( nIndex++, ticket.getDateClose( ) );
        daoUtil.setInt( nIndex++, ticket.getPriority( ) );
        daoUtil.setInt( nIndex++, ticket.getCriticality( ) );
        daoUtil.setString( nIndex++, ticket.getCustomerId( ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssigneeUser( ) != null ) ? ticket.getAssigneeUser( ).getAdminUserId( ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssigneeUnit( ) != null ) ? ticket.getAssigneeUnit( ).getUnitId( ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssignerUser( ) != null ) ? ticket.getAssignerUser( ).getAdminUserId( ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssignerUnit( ) != null ) ? ticket.getAssignerUnit( ).getUnitId( ) : ( -1 ) );
        daoUtil.setString( nIndex++, ticket.getUserMessage( ) );
        daoUtil.setString( nIndex++, ticket.getUrl( ) );

        if ( ( ticket.getChannel( ) != null ) && TicketUtils.isIdSet( ticket.getChannel( ).getId( ) ) )
        {
            daoUtil.setInt( nIndex++, ticket.getChannel( ).getId( ) );
        }
        else
        {
            daoUtil.setIntNull( nIndex++ );
        }

        daoUtil.setString( nIndex++, ticket.getNomenclature( ) );

        daoUtil.setInt( nIndex++, ticket.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        TicketAddress _ticketAddress = ticket.getTicketAddress( );
        if ( _ticketAddress != null )
        {

            storeTicketAddress( ticket, plugin );
        }
    }

    /**
     * Update or create the TicketAddress record in the table
     * 
     * @param ticket
     *            the reference of the Ticket
     * @param plugin
     *            the Plugin
     */
    private void storeTicketAddress( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TICKET_ADDRESS, plugin );
        daoUtil.setInt( 1, ticket.getId( ) );
        daoUtil.executeQuery( );

        boolean _bAddressExists = daoUtil.next( );
        daoUtil.free( );

        if ( _bAddressExists )
        {
            daoUtil = new DAOUtil( SQL_QUERY_UPDATE_TICKET_ADDRESS, plugin );
            int nIndex = 1;
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getAddress( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getAddressDetail( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getPostalCode( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getCity( ) );
            daoUtil.setInt( nIndex++, ticket.getId( ) );

            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
        else
        {
            daoUtil = new DAOUtil( SQL_QUERY_INSERT_TICKET_ADDRESS, plugin );
            int nIndex = 1;
            daoUtil.setInt( nIndex++, ticket.getId( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getAddress( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getAddressDetail( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getPostalCode( ) );
            daoUtil.setString( nIndex++, ticket.getTicketAddress( ).getCity( ) );

            daoUtil.executeUpdate( );
            daoUtil.free( );
        }

    }

    /**
     * returns baseQuery query built from workflow and filter dependencies
     * 
     * @param strBaseQuery
     *            base select query to update
     * @param filter
     *            filter use for the query
     * 
     * @return select all query
     */
    private String getSelectAllQuery( String strBaseQuery, TicketFilter filter )
    {
        StringBuilder sqlQuery = new StringBuilder( );
        boolean bWorkflowAvail = WorkflowService.getInstance( ).isAvailable( );

        sqlQuery.append( strBaseQuery );
        if ( bWorkflowAvail )
        {
            sqlQuery.append( SQL_SELECT_ALL_WORKFLOW_JOIN_CLAUSE );
            sqlQuery.append( SQL_SELECT_ALL_WORKFLOW_WHERE_CLAUSE );
        }

        if ( filter != null )
        {
            String strFilterClause = getFilterCriteriaClauses( filter );
            if ( StringUtils.isNotEmpty( strFilterClause ) )
            {
                if ( !bWorkflowAvail )
                {
                    sqlQuery.append( " WHERE " );
                    strFilterClause = strFilterClause.substring( 5 );
                }
                sqlQuery.append( strFilterClause );
            }
        }

        return sqlQuery.toString( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Ticket> selectTicketsList( Plugin plugin )
    {
        List<Ticket> ticketList = new ArrayList<Ticket>( );

        DAOUtil daoUtil = new DAOUtil( getSelectAllQuery( SQL_QUERY_SELECTALL_SELECT_CLAUSE, null ), plugin );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Ticket ticket = dataToTicket( daoUtil );

            ticketList.add( ticket );
        }

        daoUtil.free( );

        return ticketList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketsList( Plugin plugin )
    {
        List<Integer> ticketList = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ticketList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return ticketList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int loadTicketMarkingId( int nIdTicket, Plugin plugin )
    {
        int idMarking = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ID_MARKING, plugin );
        daoUtil.setInt( 1, nIdTicket );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            idMarking = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return idMarking;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void storeTicketMarkingId( int nIdTicket, int nIdMarking, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ID_MARKING, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, nIdMarking );
        daoUtil.setInt( nIndex++, nIdTicket );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void resetMarkingId( int nIdMarking, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_RESET_ID_MARKING, plugin );

        daoUtil.setInt( 1, nIdMarking );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void resetTicketMarkingId( int nIdTicket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ID_MARKING, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, TicketingConstants.DEFAULT_MARKING );
        daoUtil.setInt( nIndex++, nIdTicket );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    // ----------------------------------------
    // Ticket response management
    // ----------------------------------------

    /**
     * {@inheritDoc }
     */
    @Override
    public void insertTicketResponse( int nIdTicket, int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_TICKET_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdTicket );
        daoUtil.setInt( 2, nIdResponse );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> findListIdResponse( int nIdTicket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TICKET_RESPONSE_LIST, plugin );
        daoUtil.setInt( 1, nIdTicket );
        daoUtil.executeQuery( );

        List<Integer> listIdResponse = new ArrayList<Integer>( );

        while ( daoUtil.next( ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIdResponse;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteTicketResponse( int nIdTicket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_TICKET_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdTicket );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTicketResponsesByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findIdTicketByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ID_TICKET_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeQuery( );

        int nIdTicket = 0;

        if ( daoUtil.next( ) )
        {
            nIdTicket = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nIdTicket;
    }

    /**
     * Creates a Ticket object from data
     * 
     * @param daoUtil
     *            the data
     * @return the Ticket object
     */
    private static Ticket dataToTicket( DAOUtil daoUtil )
    {
        Ticket ticket = new Ticket( );

        int nIndex = 1;

        ticket.setId( daoUtil.getInt( nIndex++ ) );
        ticket.setReference( daoUtil.getString( nIndex++ ) );
        ticket.setGuid( daoUtil.getString( nIndex++ ) );
        ticket.setIdUserTitle( daoUtil.getInt( nIndex++ ) );
        ticket.setUserTitle( daoUtil.getString( nIndex++ ) );
        ticket.setFirstname( daoUtil.getString( nIndex++ ) );
        ticket.setLastname( daoUtil.getString( nIndex++ ) );
        ticket.setEmail( daoUtil.getString( nIndex++ ) );
        ticket.setFixedPhoneNumber( daoUtil.getString( nIndex++ ) );
        ticket.setMobilePhoneNumber( daoUtil.getString( nIndex++ ) );
        ticket.setIdTicketMarking( daoUtil.getInt( nIndex++ ) );

        TicketCategory ticketCategory = TicketCategoryService.getInstance( ).findCategoryById( daoUtil.getInt( nIndex++ ) );
        ticket.setTicketCategory( ticketCategory );

        ticket.setIdContactMode( daoUtil.getInt( nIndex++ ) );
        ticket.setContactMode( daoUtil.getString( nIndex++ ) );
        ticket.setTicketComment( daoUtil.getString( nIndex++ ) );
        ticket.setTicketStatus( daoUtil.getInt( nIndex++ ) );
        ticket.setTicketStatusText( daoUtil.getString( nIndex++ ) );
        ticket.setDateUpdate( daoUtil.getTimestamp( nIndex++ ) );
        ticket.setDateCreate( daoUtil.getTimestamp( nIndex++ ) );
        ticket.setDateClose( daoUtil.getTimestamp( nIndex++ ) );
        ticket.setPriority( daoUtil.getInt( nIndex++ ) );
        ticket.setCriticality( daoUtil.getInt( nIndex++ ) );
        ticket.setCustomerId( daoUtil.getString( nIndex++ ) );

        // assignee user
        AssigneeUser assigneeUser = new AssigneeUser( );
        assigneeUser.setAdminUserId( daoUtil.getInt( nIndex++ ) );
        assigneeUser.setFirstname( daoUtil.getString( nIndex++ ) );
        assigneeUser.setLastname( daoUtil.getString( nIndex++ ) );
        if ( assigneeUser.getAdminUserId( ) != -1 )
        {
            ticket.setAssigneeUser( assigneeUser );
        }

        // assignee unit
        AssigneeUnit assigneeUnit = new AssigneeUnit( );
        assigneeUnit.setUnitId( daoUtil.getInt( nIndex++ ) );
        assigneeUnit.setName( daoUtil.getString( nIndex++ ) );
        if ( assigneeUnit.getUnitId( ) != -1 )
        {
            ticket.setAssigneeUnit( assigneeUnit );
        }

        // assigner user
        int nId = daoUtil.getInt( nIndex++ );

        if ( nId != -1 )
        {
            AssigneeUser assignerUser = new AssigneeUser( );
            assignerUser.setAdminUserId( nId );
            ticket.setAssignerUser( assignerUser );
        }

        // assigner unit
        nId = daoUtil.getInt( nIndex++ );
        String sAssignerUnitName = daoUtil.getString( nIndex++ );
        if ( nId != -1 )
        {
            AssigneeUnit assignerUnit = new AssigneeUnit( );
            assignerUnit.setUnitId( nId );
            assignerUnit.setName( sAssignerUnitName );
            ticket.setAssignerUnit( assignerUnit );
        }

        ticket.setUserMessage( daoUtil.getString( nIndex++ ) );
        ticket.setUrl( daoUtil.getString( nIndex++ ) );

        // channel
        Channel channel = new Channel( );
        channel.setId( daoUtil.getInt( nIndex++ ) );
        channel.setLabel( daoUtil.getString( nIndex++ ) );
        channel.setIconFont( daoUtil.getString( nIndex++ ) );
        if ( TicketUtils.isIdSet( channel.getId( ) ) )
        {
            ticket.setChannel( channel );
        }

        ticket.setNomenclature( daoUtil.getString( nIndex++ ) );

        // Ticket Address
        TicketAddress ticketAddress = new TicketAddress( );
        ticketAddress.setAddress( daoUtil.getString( nIndex++ ) );
        ticketAddress.setAddressDetail( daoUtil.getString( nIndex++ ) );
        ticketAddress.setPostalCode( daoUtil.getString( nIndex++ ) );
        ticketAddress.setCity( daoUtil.getString( nIndex++ ) );

        if ( StringUtils.isNotEmpty( ticketAddress.getAddress( ) ) || StringUtils.isNotBlank( ticketAddress.getAddressDetail( ) )
                || StringUtils.isNotBlank( ticketAddress.getPostalCode( ) ) || StringUtils.isNotBlank( ticketAddress.getCity( ) ) )
        {
            ticket.setTicketAddress( ticketAddress );
        }

        return ticket;
    }

    /**
     * get criteria to request
     *
     * @param sbSQL
     *            request
     * @param filter
     *            filter
     */
    private String getFilterCriteriaClauses( TicketFilter filter )
    {
        StringBuilder sbSQL = new StringBuilder( );
        sbSQL.append( filter.containsCreationDate( ) ? SQL_FILTER_CREATION_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsCreationStartDate( ) ? SQL_FILTER_CREATION_START_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsCreationEndDate( ) ? SQL_FILTER_CREATION_END_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastUpdateDate( ) ? SQL_FILTER_LASTUPDATE_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastUpdateStartDate( ) ? SQL_FILTER_LASTUPDATE_START_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastUpdateEndDate( ) ? SQL_FILTER_LASTUPDATE_END_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsStatus( ) ? SQL_FILTER_STATUS : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdTicket( ) ? SQL_FILTER_ID_TICKET : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdUser( ) ? SQL_FILTER_ID_USER : StringUtils.EMPTY );
        sbSQL.append( filter.containsEmail( ) ? SQL_FILTER_EMAIL : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastName( ) ? SQL_FILTER_LASTNAME : StringUtils.EMPTY );
        sbSQL.append( filter.containsFirstName( ) ? SQL_FILTER_FIRSTNAME : StringUtils.EMPTY );
        sbSQL.append( filter.containsFixedPhoneNumber( ) ? SQL_FILTER_FIXED_PHONE_NUMBER : StringUtils.EMPTY );
        sbSQL.append( filter.containsMobilePhoneNumber( ) ? SQL_FILTER_MOBILE_PHONE_NUMBER : StringUtils.EMPTY );
        sbSQL.append( filter.containsCloseDate( ) ? SQL_FILTER_CLOSE_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsUrgency( ) ? SQL_FILTER_URGENCY : StringUtils.EMPTY );
        sbSQL.append( filter.containsChannel( ) ? SQL_FILTER_ID_CHANNEL : StringUtils.EMPTY );
        sbSQL.append( filter.containsNomenclature( ) ? SQL_FILTER_NOMENCLATURE : StringUtils.EMPTY );

        if ( filter.containsListIdWorkflowState( ) )
        {
            if ( filter.getListIdWorkflowState( ).size( ) == 1 )
            {
                sbSQL.append( SQL_FILTER_ID_SINGLE_STATE );
            }
            else
            {
                sbSQL.append( SQL_FILTER_ID_MULTIPLE_STATES_START );

                for ( Iterator<Integer> iterator = filter.getListIdWorkflowState( ).iterator( ); iterator.hasNext( ); )
                {
                    iterator.next( );
                    sbSQL.append( SQL_FILTER_ID_STATE );

                    if ( iterator.hasNext( ) )
                    {
                        sbSQL.append( CONSTANT_OR );
                    }
                }

                sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );
            }
        }

        // specific filter for tickets view
        boolean bNotEmptyAssigneeUnit = CollectionUtils.isNotEmpty( filter.getFilterIdAssigneeUnit( ) );
        boolean bNotEmptyAssignerUnit = CollectionUtils.isNotEmpty( filter.getFilterIdAssignerUnit( ) );
        switch( filter.getFilterView( ) )
        {
            case AGENT:
                sbSQL.append( SQL_FILTER_VIEW_AGENT );
                break;

            case GROUP:
                sbSQL.append( SQL_FILTER_VIEW_GROUP );
                if ( bNotEmptyAssigneeUnit || bNotEmptyAssignerUnit )
                {
                    sbSQL.append( CONSTANT_AND );
                    sbSQL.append( CONSTANT_OPEN_PARENTHESIS );
                    if ( bNotEmptyAssigneeUnit )
                    {
                        sbSQL.append( SQL_FILTER_VIEW_GROUP_UNIT_ASSIGNEE );
                        sbSQL.append( getInCriteriaClause( filter.getFilterIdAssigneeUnit( ).size( ) ) );
                        sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );
                        if ( bNotEmptyAssignerUnit )
                        {
                            sbSQL.append( CONSTANT_OR );
                        }
                    }
                    if ( bNotEmptyAssignerUnit )
                    {
                        sbSQL.append( SQL_FILTER_VIEW_GROUP_UNIT_ASSIGNER );
                        sbSQL.append( getInCriteriaClause( filter.getFilterIdAssignerUnit( ).size( ) ) );
                        sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );
                    }
                    sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );
                }
                break;

            case DOMAIN:
                sbSQL.append( SQL_FILTER_VIEW_DOMAIN );
                if ( bNotEmptyAssigneeUnit )
                {
                    sbSQL.append( CONSTANT_AND );
                    sbSQL.append( SQL_FILTER_VIEW_DOMAIN_UNIT_ASSIGNEE );
                    sbSQL.append( getInCriteriaClause( filter.getFilterIdAssigneeUnit( ).size( ) ) );
                    sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );
                }
                if ( bNotEmptyAssignerUnit )
                {
                    sbSQL.append( CONSTANT_AND );
                    sbSQL.append( SQL_FILTER_VIEW_DOMAIN_UNIT_ASSIGNER );
                    sbSQL.append( getInCriteriaClause( filter.getFilterIdAssignerUnit( ).size( ) ) );
                    sbSQL.append( CONSTANT_CLOSE_PARENTHESIS );
                }

                break;

            case ALL:
            default:
                // nothing
                break;
        }

        if ( filter.containsOrderBy( ) )
        {
            sbSQL.append( filter.getOrderBySqlClause( filter.isOrderASC( ) ) );
        }
        else
        {
            // always apply default sorting
            sbSQL.append( filter.getDefaultOrderBySqlClause( filter.isOrderASC( ) ) );
        }

        if ( filter.containsLimit( ) )
        {
            sbSQL.append( SQL_FILTER_LIMIT );
        }

        return sbSQL.toString( );
    }

    private String getInCriteriaClause( int nSize )
    {
        StringBuilder sbInClause = new StringBuilder( );
        if ( nSize > 0 )
        {
            sbInClause.append( "?" );
            for ( int nIdx = 1; nIdx < nSize; nIdx++ )
            {
                sbInClause.append( ", ?" );
            }
        }
        return sbInClause.toString( );
    }

    /**
     * add filter values to request
     *
     * @param daoUtil
     *            daoUtil
     * @param filter
     *            filter
     */
    private void addFilterCriteriaValues( DAOUtil daoUtil, TicketFilter filter )
    {
        int nIndex = 1;

        if ( filter.containsCreationDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCreationDate( ).getTime( ) ) );
            daoUtil.setDate( nIndex++, new Date( getNextDayDate( filter.getCreationDate( ) ).getTime( ) ) );
        }

        if ( filter.containsCreationStartDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCreationStartDate( ).getTime( ) ) );
        }

        if ( filter.containsCreationEndDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCreationEndDate( ).getTime( ) ) );
        }

        if ( filter.containsLastUpdateDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getLastUpdateDate( ).getTime( ) ) );
            daoUtil.setDate( nIndex++, new Date( getNextDayDate( filter.getLastUpdateDate( ) ).getTime( ) ) );
        }

        if ( filter.containsLastUpdateStartDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getLastUpdateStartDate( ).getTime( ) ) );
        }

        if ( filter.containsLastUpdateEndDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getLastUpdateEndDate( ).getTime( ) ) );
        }

        if ( filter.containsStatus( ) )
        {
            daoUtil.setString( nIndex++, filter.getStatus( ) );
        }

        if ( filter.containsIdTicket( ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdTicket( ) );
        }

        if ( filter.containsIdUser( ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdUser( ) );
        }

        if ( filter.containsEmail( ) )
        {
            daoUtil.setString( nIndex++, filter.getEmail( ).toUpperCase( ) );
        }

        if ( filter.containsLastName( ) )
        {
            daoUtil.setString( nIndex++, filter.getLastName( ).toUpperCase( ) );
        }

        if ( filter.containsFirstName( ) )
        {
            daoUtil.setString( nIndex++, filter.getFirstName( ).toUpperCase( ) );
        }

        if ( filter.containsFixedPhoneNumber( ) )
        {
            daoUtil.setString( nIndex++, filter.getFixedPhoneNumber( ).toUpperCase( ) );
        }

        if ( filter.containsMobilePhoneNumber( ) )
        {
            daoUtil.setString( nIndex++, filter.getMobilePhoneNumber( ).toUpperCase( ) );
        }

        if ( filter.containsNomenclature( ) )
        {
            daoUtil.setString( nIndex++, filter.getNomenclature( ) );
        }

        if ( filter.containsCloseDate( ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCloseDate( ).getTime( ) ) );
            daoUtil.setDate( nIndex++, new Date( getNextDayDate( filter.getCreationDate( ) ).getTime( ) ) );
        }

        if ( filter.containsUrgency( ) )
        {
            daoUtil.setInt( nIndex++, filter.getUrgency( ) );
            daoUtil.setInt( nIndex++, filter.getUrgency( ) );
        }

        if ( filter.containsListIdWorkflowState( ) )
        {
            for ( Integer nId : filter.getListIdWorkflowState( ) )
            {
                daoUtil.setInt( nIndex++, nId );
            }
        }

        // specific filter for tickets view
        boolean bNotEmptyAssigneeUnit = CollectionUtils.isNotEmpty( filter.getFilterIdAssigneeUnit( ) );
        boolean bNotEmptyAssignerUnit = CollectionUtils.isNotEmpty( filter.getFilterIdAssignerUnit( ) );
        switch( filter.getFilterView( ) )
        {
            case AGENT:
                daoUtil.setInt( nIndex++, filter.getFilterIdAdminUser( ) );
                daoUtil.setInt( nIndex++, filter.getFilterIdAdminUser( ) );
                break;

            case GROUP:
                daoUtil.setInt( nIndex++, filter.getFilterIdAdminUser( ) );
                daoUtil.setInt( nIndex++, filter.getFilterIdAdminUser( ) );
                if ( bNotEmptyAssigneeUnit )
                {
                    for ( Integer nIdUnit : filter.getFilterIdAssigneeUnit( ) )
                    {
                        daoUtil.setInt( nIndex++, nIdUnit );
                    }
                }
                if ( bNotEmptyAssignerUnit )
                {
                    for ( Integer nIdUnit : filter.getFilterIdAssignerUnit( ) )
                    {
                        daoUtil.setInt( nIndex++, nIdUnit );
                    }
                }
                break;

            case DOMAIN:
                daoUtil.setInt( nIndex++, filter.getFilterIdAdminUser( ) );
                daoUtil.setInt( nIndex++, filter.getFilterIdAdminUser( ) );
                if ( bNotEmptyAssigneeUnit )
                {
                    for ( Integer nIdUnit : filter.getFilterIdAssigneeUnit( ) )
                    {
                        daoUtil.setInt( nIndex++, nIdUnit );
                    }
                }
                if ( bNotEmptyAssignerUnit )
                {
                    for ( Integer nIdUnit : filter.getFilterIdAssignerUnit( ) )
                    {
                        daoUtil.setInt( nIndex++, nIdUnit );
                    }
                }
                break;

            case ALL:
            default:
                // nothing
                break;
        }

        if ( filter.containsLimit( ) )
        {
            daoUtil.setInt( nIndex++, filter.getTicketsLimitStart( ) );
            daoUtil.setInt( nIndex++, filter.getTicketsLimitCount( ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> selectTicketsList( TicketFilter filter, Plugin plugin )
    {
        List<Ticket> ticketList = new ArrayList<Ticket>( );

        if ( filter != null && TicketFilterViewEnum.DOMAIN == filter.getFilterView( ) && CollectionUtils.isEmpty( filter.getAdminUserRoles( ) ) )
        {
            // domain case with no rbac role on user, empty return
            return ticketList;
        }

        StringBuilder sbSQL = new StringBuilder( getSelectAllQuery( SQL_QUERY_SELECTALL_SELECT_CLAUSE, filter ) );
        DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin );

        if ( filter != null )
        {
            addFilterCriteriaValues( daoUtil, filter );
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            Ticket ticket = dataToTicket( daoUtil );

            ticketList.add( ticket );
        }

        daoUtil.free( );

        return ticketList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> selectIdTicketsList( TicketFilter filter, Plugin plugin )
    {
        List<Integer> listIdTickets = new ArrayList<Integer>( );

        if ( filter != null && TicketFilterViewEnum.DOMAIN == filter.getFilterView( ) && CollectionUtils.isEmpty( filter.getAdminUserRoles( ) ) )
        {
            // domain case with no rbac role on user, empty return
            return listIdTickets;
        }

        StringBuilder sbSQL = new StringBuilder( getSelectAllQuery( SQL_SELECT_ALL_ID_TICKET, filter ) );
        DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin );

        if ( filter != null )
        {
            addFilterCriteriaValues( daoUtil, filter );
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            
            listIdTickets.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIdTickets;
    }

    /**
     * returns next day from input date
     *
     * @param date
     *            date to increment
     * @return date + 1day
     */
    private java.util.Date getNextDayDate( java.util.Date date )
    {
        Calendar cal = Calendar.getInstance( );
        cal.setTime( date );
        cal.add( Calendar.DATE, 1 );

        return new java.util.Date( cal.getTimeInMillis( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> selectTicketsListByUnitId( int nIdUnit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TICKET_BY_ID_UNIT, plugin );
        daoUtil.setInt( 1, nIdUnit );
        daoUtil.executeQuery( );

        List<Ticket> listTickets = new ArrayList<Ticket>( );

        if ( daoUtil.next( ) )
        {
            Ticket ticket = dataToTicket( daoUtil );
            listTickets.add( ticket );
        }

        daoUtil.free( );

        return listTickets;
    }
}
