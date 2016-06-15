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
package fr.paris.lutece.plugins.ticketing.business.typicalResponse;

import fr.paris.lutece.plugins.ticketing.business.category.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.category.TicketCategoryHome;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomain;
import fr.paris.lutece.plugins.ticketing.business.domain.TicketDomainHome;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketType;
import fr.paris.lutece.plugins.ticketing.business.tickettype.TicketTypeHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for TypeResponse objects
 */
public final class TypicalResponseDAO implements ITypicalResponseDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_type_response ) FROM ticketing_types_reponses";
    private static final String SQL_QUERY_SELECT = "SELECT id_type_response, id_ticket_category, title, reponse FROM ticketing_types_reponses WHERE id_type_response = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_types_reponses ( id_type_response, id_ticket_category, title, reponse ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_types_reponses WHERE id_type_response = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_types_reponses SET id_type_response = ?, id_ticket_category = ?, title = ?, reponse = ? WHERE id_type_response = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_type_response, id_ticket_category, title, reponse FROM ticketing_types_reponses";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_type_response FROM ticketing_types_reponses";

    /**
     * Generates a new primary key
     * @param plugin The Plugin
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
    public void insert( TypicalResponse typeResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        typeResponse.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, typeResponse.getId(  ) );
        daoUtil.setInt( nIndex++, typeResponse.getIdTicketCategory(  ) );
        daoUtil.setString( nIndex++, typeResponse.getTitle(  ) );
        daoUtil.setString( nIndex++, typeResponse.getReponse(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public TypicalResponse load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        TypicalResponse typeResponse = null;

        TicketCategory ticketCategory;
        TicketDomain ticketDomain;
        TicketType ticketType;

        if ( daoUtil.next(  ) )
        {
            typeResponse = new TypicalResponse(  );

            int nIndex = 1;

            typeResponse.setId( daoUtil.getInt( nIndex++ ) );
            typeResponse.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            typeResponse.setTitle( daoUtil.getString( nIndex++ ) );
            typeResponse.setReponse( daoUtil.getString( nIndex++ ) );

            //populate label category, domain and type
            ticketCategory = TicketCategoryHome.findByPrimaryKey( typeResponse.getIdTicketCategory(  ) );
            typeResponse.setIdDomain( ticketCategory.getIdTicketDomain(  ) );

            ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
            typeResponse.setIdTicketType( ticketDomain.getIdTicketType(  ) );
        }

        daoUtil.free(  );

        return typeResponse;
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
    public void store( TypicalResponse typeResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, typeResponse.getId(  ) );
        daoUtil.setInt( nIndex++, typeResponse.getIdTicketCategory(  ) );
        daoUtil.setString( nIndex++, typeResponse.getTitle(  ) );
        daoUtil.setString( nIndex++, typeResponse.getReponse(  ) );
        daoUtil.setInt( nIndex, typeResponse.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TypicalResponse> selectTypeResponsesList( Plugin plugin )
    {
        List<TypicalResponse> typeResponseList = new ArrayList<TypicalResponse>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        TicketCategory ticketCategory;
        TicketDomain ticketDomain;
        TicketType ticketType;

        while ( daoUtil.next(  ) )
        {
            TypicalResponse typeResponse = new TypicalResponse(  );
            int nIndex = 1;

            typeResponse.setId( daoUtil.getInt( nIndex++ ) );
            typeResponse.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            typeResponse.setTitle( daoUtil.getString( nIndex++ ) );
            typeResponse.setReponse( daoUtil.getString( nIndex++ ) );

            //populate label category, domain and type
            ticketCategory = TicketCategoryHome.findByPrimaryKey( typeResponse.getIdTicketCategory(  ) );
            typeResponse.setCategory( ( ticketCategory != null ) ? ticketCategory.getLabel(  ) : "" );

            ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
            typeResponse.setDomain( ( ticketDomain != null ) ? ticketDomain.getLabel(  ) : "" );

            ticketType = TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType(  ) );
            typeResponse.setTicketType( ( ticketType != null ) ? ticketType.getLabel(  ) : "" );

            typeResponseList.add( typeResponse );
        }

        daoUtil.free(  );

        return typeResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdTypeResponsesList( Plugin plugin )
    {
        List<Integer> typeResponseList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            typeResponseList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return typeResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectTypeResponsesReferenceList( Plugin plugin )
    {
        ReferenceList typeResponseList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            typeResponseList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return typeResponseList;
    }
}
