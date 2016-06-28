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
package fr.paris.lutece.plugins.ticketing.business.modelresponse;

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
public final class ModelResponseDAO implements IModelResponseDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_model_response ) FROM ticketing_model_reponses";
    private static final String SQL_QUERY_SELECT = "SELECT id_model_response, id_ticket_category, title, reponse,keyword FROM ticketing_model_reponses WHERE id_model_response = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_model_reponses ( id_model_response, id_ticket_category, title, reponse, keyword ) VALUES ( ?, ?, ?, ?,? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_model_reponses WHERE id_model_response = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_model_reponses SET id_model_response = ?, id_ticket_category = ?, title = ?, reponse = ?, keyword =? WHERE id_model_response = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_model_response, id_ticket_category, title, reponse,keyword FROM ticketing_model_reponses";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_model_response FROM ticketing_model_reponses";

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
    public void insert( ModelResponse modelResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        modelResponse.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, modelResponse.getId(  ) );
        daoUtil.setInt( nIndex++, modelResponse.getIdTicketCategory(  ) );
        daoUtil.setString( nIndex++, modelResponse.getTitle(  ) );
        daoUtil.setString( nIndex++, modelResponse.getReponse(  ) );
        daoUtil.setString( nIndex++, modelResponse.getKeyword( ));

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ModelResponse load( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        ModelResponse modelResponse = null;

        TicketCategory ticketCategory;
        TicketDomain ticketDomain;
        TicketType ticketType;

        if ( daoUtil.next(  ) )
        {
            modelResponse = new ModelResponse(  );

            int nIndex = 1;


            modelResponse.setId( daoUtil.getInt( nIndex++ ) );
            modelResponse.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            modelResponse.setTitle( daoUtil.getString( nIndex++ ) );
            modelResponse.setReponse( daoUtil.getString( nIndex++ ) );
            modelResponse.setKeyword(daoUtil.getString( nIndex++ ) );


            //populate label category, domain and type
            ticketCategory = TicketCategoryHome.findByPrimaryKey( modelResponse.getIdTicketCategory(  ) );
            modelResponse.setIdDomain( ticketCategory.getIdTicketDomain(  ) );

            ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
            modelResponse.setIdTicketType( ticketDomain.getIdTicketType(  ) );
        }

        daoUtil.free(  );

        return modelResponse;
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
    public void store( ModelResponse modelResponse, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;


        daoUtil.setInt( nIndex++, modelResponse.getId(  ) );
        daoUtil.setInt( nIndex++, modelResponse.getIdTicketCategory(  ) );
        daoUtil.setString( nIndex++, modelResponse.getTitle(  ) );
        daoUtil.setString( nIndex++, modelResponse.getReponse(  ) );
         daoUtil.setString(nIndex++, modelResponse.getKeyword( ));
        daoUtil.setInt( nIndex, modelResponse.getId(  ) );  
      

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<ModelResponse> selectModelResponsesList( Plugin plugin )
    {
        List<ModelResponse> typeResponseList = new ArrayList<ModelResponse>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        TicketCategory ticketCategory;
        TicketDomain ticketDomain;
        TicketType ticketType;

        while ( daoUtil.next(  ) )
        {
            ModelResponse modelResponse = new ModelResponse(  );
            int nIndex = 1;


            modelResponse.setId( daoUtil.getInt( nIndex++ ) );
            modelResponse.setIdTicketCategory( daoUtil.getInt( nIndex++ ) );
            modelResponse.setTitle( daoUtil.getString( nIndex++ ) );
            modelResponse.setReponse( daoUtil.getString( nIndex++ ) );
            modelResponse.setKeyword(daoUtil.getString( nIndex++ ) );


            //populate label category, domain and type
            ticketCategory = TicketCategoryHome.findByPrimaryKey( modelResponse.getIdTicketCategory(  ) );
            modelResponse.setCategory( ( ticketCategory != null ) ? ticketCategory.getLabel(  ) : "" );

            ticketDomain = TicketDomainHome.findByPrimaryKey( ticketCategory.getIdTicketDomain(  ) );
            modelResponse.setDomain( ( ticketDomain != null ) ? ticketDomain.getLabel(  ) : "" );

            ticketType = TicketTypeHome.findByPrimaryKey( ticketDomain.getIdTicketType(  ) );
            modelResponse.setTicketType( ( ticketType != null ) ? ticketType.getLabel(  ) : "" );

            typeResponseList.add( modelResponse );
        }

        daoUtil.free(  );

        return typeResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdModelResponsesList( Plugin plugin )
    {
        List<Integer> modelResponseList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
           modelResponseList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return modelResponseList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectModelResponsesReferenceList( Plugin plugin )
    {
        ReferenceList modelResponseList = new ReferenceList(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            modelResponseList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return modelResponseList;
    }
}