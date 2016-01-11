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
package fr.paris.lutece.plugins.ticketing.business;

import fr.paris.lutece.plugins.unittree.business.unit.Unit;
import fr.paris.lutece.plugins.unittree.business.unit.UnitHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import org.apache.commons.lang.StringUtils;

import java.sql.Date;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * This class provides Data Access methods for Ticket objects
 */
public final class TicketDAO implements ITicketDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_ticket ) FROM ticketing_ticket";
    private static final String SQL_QUERY_SELECT = "SELECT a.id_ticket, a.ticket_reference, a.guid, a.id_user_title, b.label, a.firstname, a.lastname, a.email, " +
        " a.fixed_phone_number, a.mobile_phone_number, c.id_ticket_type, c.label, d.id_ticket_domain, " +
        " d.label, a.id_ticket_category, e.label, a.id_contact_mode, f.label, a.ticket_comment," +
        " a.ticket_status, a.ticket_status_text, a.date_update, a.date_create, a.date_close, a.priority, a.criticality, a.id_customer, a.id_admin_user, a.id_unit " +
        " FROM ticketing_ticket a, ticketing_user_title b, ticketing_ticket_type c, ticketing_ticket_domain d, ticketing_ticket_category e, ticketing_contact_mode f " +
        " WHERE a.id_ticket = ? AND a.id_user_title = b.id_user_title AND a.id_ticket_category = e.id_ticket_category AND e.id_ticket_domain = d.id_ticket_domain AND d.id_ticket_type = c.id_ticket_type AND a.id_contact_mode = f.id_contact_mode";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_ticket ( id_ticket, ticket_reference , guid, id_user_title, firstname, lastname, email, " +
        " fixed_phone_number, mobile_phone_number, id_ticket_category, " +
        " id_contact_mode, ticket_comment, ticket_status, ticket_status_text, date_update, date_create, " +
        " priority, criticality, id_customer, id_admin_user, id_unit ) " +
        " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_ticket WHERE id_ticket = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_ticket SET id_ticket = ?, ticket_reference = ?, guid = ?, id_user_title = ?, firstname = ?, lastname = ?, email = ?, fixed_phone_number = ?, mobile_phone_number = ?, " +
        " id_ticket_category = ?, id_contact_mode = ?, ticket_comment = ?, ticket_status = ?, ticket_status_text = ?, date_update = ?," +
        " date_close = ? , priority = ? , criticality = ? , id_customer = ? , id_admin_user = ? , id_unit = ? " +
        " WHERE id_ticket = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT a.id_ticket, a.ticket_reference, a.guid, a.id_user_title, b.label, a.firstname, a.lastname, a.email, a.fixed_phone_number, a.mobile_phone_number," +
        " c.id_ticket_type, c.label, d.id_ticket_domain, d.label, a.id_ticket_category, e.label, a.id_contact_mode, f.label, a.ticket_comment," +
        " a.ticket_status, a.ticket_status_text, a.date_update, a.date_create, a.date_close, a.priority, a.criticality, a.id_customer, a.id_admin_user, a.id_unit " +
        " FROM ticketing_ticket a, ticketing_user_title b, ticketing_ticket_type c, ticketing_ticket_domain d, ticketing_ticket_category e, ticketing_contact_mode f " +
        " WHERE a.id_user_title = b.id_user_title AND a.id_ticket_category = e.id_ticket_category AND e.id_ticket_domain = d.id_ticket_domain AND d.id_ticket_type = c.id_ticket_type AND a.id_contact_mode = f.id_contact_mode";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_ticket FROM ticketing_ticket";
    private static final String SQL_FILTER_STATUS = " AND a.ticket_status = UPPER(?) ";
    private static final String SQL_FILTER_ID_TICKET = " AND  a.id_ticket= ? ";
    private static final String SQL_FILTER_CREATION_DATE = " AND a.date_create >= ? AND a.date_create < ? ";
    private static final String SQL_FILTER_CREATION_START_DATE = " AND a.date_create >= ? ";
    private static final String SQL_FILTER_CREATION_END_DATE = " AND a.date_create <= ? ";
    private static final String SQL_FILTER_LASTUPDATE_DATE = " AND a.date_update >= ? AND a.date_update < ? ";
    private static final String SQL_FILTER_LASTUPDATE_START_DATE = " AND a.date_update >= ? ";
    private static final String SQL_FILTER_LASTUPDATE_END_DATE = " AND a.date_update <= ? ";
    private static final String SQL_FILTER_DOMAIN = " AND d.id_ticket_domain = ? ";
    private static final String SQL_FILTER_TYPE = "  AND c.id_ticket_type = ?  ";
    private static final String SQL_FILTER_CATEGORY = " AND a.id_ticket_category = ?  ";
    private static final String SQL_FILTER_ID_USER = "  AND a.guid = ? ";
    private static final String SQL_FILTER_EMAIL = " AND a.email = UPPER(?) ";
    private static final String SQL_FILTER_LASTNAME = " AND a.lastname = UPPER(?) ";
    private static final String SQL_FILTER_FIRSTNAME = " AND a.firstname = UPPER(?) ";
    private static final String SQL_FILTER_FIXED_PHONE_NUMBER = " AND a.fixed_phone_number = ? ";
    private static final String SQL_FILTER_MOBILE_PHONE_NUMBER = " AND a.mobile_phone_number = ? ";
    private static final String SQL_FILTER_CLOSE_DATE = " AND a.date_close <= ? AND a.date_close < ? ";
    private static final String CONSTANT_ASC = " ASC";
    private static final String CONSTANT_DESC = " DESC";
    private static final String CONSTANT_ORDER_BY = " ORDER BY ";

    // SQL commands to manage ticket's generic attributes responses
    private static final String SQL_QUERY_INSERT_TICKET_RESPONSE = "INSERT INTO ticketing_ticket_response (id_ticket, id_response) VALUES (?,?)";
    private static final String SQL_QUERY_SELECT_TICKET_RESPONSE_LIST = "SELECT id_response FROM ticketing_ticket_response WHERE id_ticket = ?";
    private static final String SQL_QUERY_DELETE_TICKET_RESPONSE = "DELETE tr, gr FROM ticketing_ticket_response tr JOIN genatt_response gr ON gr.id_response = tr.id_response WHERE tr.id_ticket = ?";
    private static final String SQL_QUERY_REMOVE_FROM_ID_RESPONSE = "DELETE tr, gr FROM ticketing_ticket_response tr JOIN genatt_response gr ON gr.id_response = tr.id_response WHERE gr.id_response = ?";
    private static final String SQL_QUERY_FIND_ID_TICKET_FROM_ID_RESPONSE = " SELECT id_ticket FROM ticketing_ticket_response WHERE id_response = ? ";

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
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        ticket.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticket.getId(  ) );
        daoUtil.setString( nIndex++, ticket.getReference(  ) );
        daoUtil.setString( nIndex++, ticket.getGuid(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdUserTitle(  ) );
        daoUtil.setString( nIndex++, ticket.getFirstname(  ) );
        daoUtil.setString( nIndex++, ticket.getLastname(  ) );
        daoUtil.setString( nIndex++, ticket.getEmail(  ) );
        daoUtil.setString( nIndex++, ticket.getFixedPhoneNumber(  ) );
        daoUtil.setString( nIndex++, ticket.getMobilePhoneNumber(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdTicketCategory(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdContactMode(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketComment(  ) );
        daoUtil.setInt( nIndex++, ticket.getTicketStatus(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketStatusText(  ) );

        ticket.setDateUpdate( new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        ticket.setDateCreate( new Timestamp( new java.util.Date(  ).getTime(  ) ) );

        daoUtil.setTimestamp( nIndex++, ticket.getDateUpdate(  ) );
        daoUtil.setTimestamp( nIndex++, ticket.getDateCreate(  ) );
        daoUtil.setInt( nIndex++, ticket.getPriority(  ) );
        daoUtil.setInt( nIndex++, ticket.getCriticality(  ) );
        daoUtil.setString( nIndex++, ticket.getCustomerId(  ) );
        daoUtil.setInt( nIndex++,
            ( ticket.getAssigneeUser(  ) != null ) ? ticket.getAssigneeUser(  ).getAdminUserId(  ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssigneeUnit(  ) != null ) ? ticket.getAssigneeUnit(  ).getUnitId(  ) : 0 );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Ticket load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        Ticket ticket = null;

        if ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            ticket = new Ticket(  );
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
            ticket.setIdTicketType( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketType( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketDomain( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketCategory( daoUtil.getString( nIndex++ ) );
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

            int nAdminUserId = daoUtil.getInt( nIndex++ );
            AdminUser user = AdminUserHome.findByPrimaryKey( nAdminUserId );
            if( user != null )
            {
                AssigneeUser assigneeUser = new AssigneeUser( user );
                ticket.setAssigneeUser( assigneeUser );
            }

            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            ticket.setAssigneeUnit( assigneeUnit );
        }

        daoUtil.free(  );

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
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Ticket ticket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nIndex = 1;
        daoUtil.setInt( nIndex++, ticket.getId(  ) );
        daoUtil.setString( nIndex++, ticket.getReference(  ) );
        daoUtil.setString( nIndex++, ticket.getGuid(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdUserTitle(  ) );
        daoUtil.setString( nIndex++, ticket.getFirstname(  ) );
        daoUtil.setString( nIndex++, ticket.getLastname(  ) );
        daoUtil.setString( nIndex++, ticket.getEmail(  ) );
        daoUtil.setString( nIndex++, ticket.getFixedPhoneNumber(  ) );
        daoUtil.setString( nIndex++, ticket.getMobilePhoneNumber(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdTicketCategory(  ) );
        daoUtil.setInt( nIndex++, ticket.getIdContactMode(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketComment(  ) );
        daoUtil.setInt( nIndex++, ticket.getTicketStatus(  ) );
        daoUtil.setString( nIndex++, ticket.getTicketStatusText(  ) );

        ticket.setDateUpdate( new Timestamp( new java.util.Date(  ).getTime(  ) ) );
        daoUtil.setTimestamp( nIndex++, ticket.getDateUpdate(  ) );
        daoUtil.setTimestamp( nIndex++, ticket.getDateClose(  ) );
        daoUtil.setInt( nIndex++, ticket.getPriority(  ) );
        daoUtil.setInt( nIndex++, ticket.getCriticality(  ) );
        daoUtil.setString( nIndex++, ticket.getCustomerId(  ) );
        daoUtil.setInt( nIndex++,
            ( ticket.getAssigneeUser(  ) != null ) ? ticket.getAssigneeUser(  ).getAdminUserId(  ) : ( -1 ) );
        daoUtil.setInt( nIndex++, ( ticket.getAssigneeUnit(  ) != null ) ? ticket.getAssigneeUnit(  ).getUnitId(  ) : 0 );

        daoUtil.setInt( nIndex++, ticket.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Ticket> selectTicketsList( Plugin plugin )
    {
        List<Ticket> ticketList = new ArrayList<Ticket>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            Ticket ticket = new Ticket(  );
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
            ticket.setIdTicketType( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketType( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketDomain( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketCategory( daoUtil.getString( nIndex++ ) );
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

            int nAdminUserId = daoUtil.getInt( nIndex++ );
            AdminUser user = AdminUserHome.findByPrimaryKey( nAdminUserId );
            AssigneeUser assigneeUser = new AssigneeUser( user );
            ticket.setAssigneeUser( assigneeUser );

            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            ticket.setAssigneeUnit( assigneeUnit );

            ticketList.add( ticket );
        }

        daoUtil.free(  );

        return ticketList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTicketsList( Plugin plugin )
    {
        List<Integer> ticketList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            ticketList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return ticketList;
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
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> findListIdResponse( int nIdTicket, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TICKET_RESPONSE_LIST, plugin );
        daoUtil.setInt( 1, nIdTicket );
        daoUtil.executeQuery(  );

        List<Integer> listIdResponse = new ArrayList<Integer>(  );

        while ( daoUtil.next(  ) )
        {
            listIdResponse.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

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
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTicketResponsesByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_REMOVE_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findIdTicketByIdResponse( int nIdResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ID_TICKET_FROM_ID_RESPONSE, plugin );
        daoUtil.setInt( 1, nIdResponse );
        daoUtil.executeQuery(  );

        int nIdTicket = 0;

        if ( daoUtil.next(  ) )
        {
            nIdTicket = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nIdTicket;
    }

    /**
     * add criteria to request
     *
     * @param sbSQL
     *            request
     * @param filter
     *            filter
     */
    private void addFilterCriteriaClauses( StringBuilder sbSQL, TicketFilter filter )
    {
        sbSQL.append( filter.containsCreationDate(  ) ? SQL_FILTER_CREATION_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsCreationStartDate(  ) ? SQL_FILTER_CREATION_START_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsCreationEndDate(  ) ? SQL_FILTER_CREATION_END_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastUpdateDate(  ) ? SQL_FILTER_LASTUPDATE_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastUpdateStartDate(  ) ? SQL_FILTER_LASTUPDATE_START_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastUpdateEndDate(  ) ? SQL_FILTER_LASTUPDATE_END_DATE : StringUtils.EMPTY );
        sbSQL.append( filter.containsStatus(  ) ? SQL_FILTER_STATUS : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdTicket(  ) ? SQL_FILTER_ID_TICKET : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdDomain(  ) ? SQL_FILTER_DOMAIN : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdType(  ) ? SQL_FILTER_TYPE : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdCategory(  ) ? SQL_FILTER_CATEGORY : StringUtils.EMPTY );
        sbSQL.append( filter.containsIdUser(  ) ? SQL_FILTER_ID_USER : StringUtils.EMPTY );
        sbSQL.append( filter.containsEmail(  ) ? SQL_FILTER_EMAIL : StringUtils.EMPTY );
        sbSQL.append( filter.containsLastName(  ) ? SQL_FILTER_LASTNAME : StringUtils.EMPTY );
        sbSQL.append( filter.containsFirstName(  ) ? SQL_FILTER_FIRSTNAME : StringUtils.EMPTY );
        sbSQL.append( filter.containsFixedPhoneNumber(  ) ? SQL_FILTER_FIXED_PHONE_NUMBER : StringUtils.EMPTY );
        sbSQL.append( filter.containsMobilePhoneNumber(  ) ? SQL_FILTER_MOBILE_PHONE_NUMBER : StringUtils.EMPTY );
        sbSQL.append( filter.containsCloseDate(  ) ? SQL_FILTER_CLOSE_DATE : StringUtils.EMPTY );

        if ( filter.containsOrderBy(  ) )
        {
            sbSQL.append( CONSTANT_ORDER_BY );
            // sbSQL.append( filter.getOrderBy( ).replaceAll( ",", strOrderType
            // + "," ) );
            sbSQL.append( filter.getOrderBy(  ) );

            if ( filter.containsOrderSort(  ) )
            {
                String strOrderType = filter.isOrderASC(  ) ? CONSTANT_ASC : CONSTANT_DESC;
                sbSQL.append( strOrderType );
            }
        }
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

        if ( filter.containsCreationDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCreationDate(  ).getTime(  ) ) );
            daoUtil.setDate( nIndex++, new Date( getNextDayDate( filter.getCreationDate(  ) ).getTime(  ) ) );
        }

        if ( filter.containsCreationStartDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCreationStartDate(  ).getTime(  ) ) );
        }

        if ( filter.containsCreationEndDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCreationEndDate(  ).getTime(  ) ) );
        }

        if ( filter.containsLastUpdateDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getLastUpdateDate(  ).getTime(  ) ) );
            daoUtil.setDate( nIndex++, new Date( getNextDayDate( filter.getLastUpdateDate(  ) ).getTime(  ) ) );
        }

        if ( filter.containsLastUpdateStartDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getLastUpdateStartDate(  ).getTime(  ) ) );
        }

        if ( filter.containsLastUpdateEndDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getLastUpdateEndDate(  ).getTime(  ) ) );
        }

        if ( filter.containsStatus(  ) )
        {
            daoUtil.setString( nIndex++, filter.getStatus(  ) );
        }

        if ( filter.containsIdTicket(  ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdTicket(  ) );
        }

        if ( filter.containsIdDomain(  ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdDomain(  ) );
        }

        if ( filter.containsIdType(  ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdType(  ) );
        }

        if ( filter.containsIdCategory(  ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdCategory(  ) );
        }

        if ( filter.containsIdUser(  ) )
        {
            daoUtil.setInt( nIndex++, filter.getIdUser(  ) );
        }

        if ( filter.containsEmail(  ) )
        {
            daoUtil.setString( nIndex++, filter.getEmail(  ).toUpperCase(  ) );
        }

        if ( filter.containsLastName(  ) )
        {
            daoUtil.setString( nIndex++, filter.getLastName(  ).toUpperCase(  ) );
        }

        if ( filter.containsFirstName(  ) )
        {
            daoUtil.setString( nIndex++, filter.getFirstName(  ).toUpperCase(  ) );
        }

        if ( filter.containsFixedPhoneNumber(  ) )
        {
            daoUtil.setString( nIndex++, filter.getFixedPhoneNumber(  ).toUpperCase(  ) );
        }

        if ( filter.containsMobilePhoneNumber(  ) )
        {
            daoUtil.setString( nIndex++, filter.getMobilePhoneNumber(  ).toUpperCase(  ) );
        }

        if ( filter.containsCloseDate(  ) )
        {
            daoUtil.setDate( nIndex++, new Date( filter.getCloseDate(  ).getTime(  ) ) );
            daoUtil.setDate( nIndex++, new Date( getNextDayDate( filter.getCreationDate(  ) ).getTime(  ) ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Ticket> selectTicketsList( TicketFilter filter, Plugin plugin )
    {
        List<Ticket> ticketList = new ArrayList<Ticket>(  );

        StringBuilder sbSQL = new StringBuilder( SQL_QUERY_SELECTALL );

        if ( filter != null )
        {
            addFilterCriteriaClauses( sbSQL, filter );
        }

        DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );

        if ( filter != null )
        {
            addFilterCriteriaValues( daoUtil, filter );
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            Ticket ticket = new Ticket(  );
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
            ticket.setIdTicketType( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketType( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketDomain( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketDomain( daoUtil.getString( nIndex++ ) );
            ticket.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            ticket.setTicketCategory( daoUtil.getString( nIndex++ ) );
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

            int nAdminUserId = daoUtil.getInt( nIndex++ );
            AdminUser user = AdminUserHome.findByPrimaryKey( nAdminUserId );
            if( user != null )
            {    
                AssigneeUser assigneeUser = new AssigneeUser( user );
                ticket.setAssigneeUser( assigneeUser );
            }
            int nUnitId = daoUtil.getInt( nIndex++ );
            Unit unit = UnitHome.findByPrimaryKey( nUnitId );
            AssigneeUnit assigneeUnit = new AssigneeUnit( unit );
            ticket.setAssigneeUnit( assigneeUnit );
            ticketList.add( ticket );
        }

        daoUtil.free(  );

        return ticketList;
    }

    /**
     * returns next day from input date
     *
     * @param date date to increment
     * @return date + 1day
     */
    private java.util.Date getNextDayDate( java.util.Date date )
    {
        Calendar cal = Calendar.getInstance(  );
        cal.setTime( date );
        cal.add( Calendar.DATE, 1 );

        return new java.util.Date( cal.getTimeInMillis(  ) );
    }
}
