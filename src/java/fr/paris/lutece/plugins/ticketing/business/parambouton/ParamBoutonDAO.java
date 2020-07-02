/*
 * Copyright (c) 2002-2020, City of Paris
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
package fr.paris.lutece.plugins.ticketing.business.parambouton;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.ticketing.business.groupaction.GroupAction;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class ParamBoutonDAO implements IParamBoutonDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT                    = "SELECT id_groupe, libelle_identifiant, cle, description, ordre FROM ticketing_groupe_action WHERE id_groupe = ?";
    private static final String SQL_QUERY_INSERT                    = "INSERT INTO ticketing_groupe_action (libelle_identifiant, cle, description, ordre) VALUES(?,?,?,?)";
    private static final String SQL_QUERY_UPDATE                    = "UPDATE ticketing_groupe_action SET libelle_identifiant=?, cle=?, description=?, ordre=? WHERE id_groupe=?";
    private static final String SQL_QUERY_SELECT_BY_GROUP           = "SELECT id_param, id_action, couleur, ordre, icone, id_groupe FROM ticketing_param_bouton_action param join ticketing_couleur_bouton couleur on couleur.id_couleur = param.id_couleur where id_groupe=?";
    private static final String SQL_QUERY_SELECT_WITHOUT_GROUP      = "SELECT id_action from workflow_action where id_action not in (select id_action FROM ticketing_param_bouton_action)";
    private static final String SQL_QUERY_SELECT_WITH_DEFAULT_GROUP = "SELECT id_action from workflow_action where id_action not in (select id_action FROM ticketing_param_bouton_action)";
    private static final String SQL_QUERY_SELECT_DATA_TICKET_DETAIL = "SELECT pba.id_action, pba.ordre, pba.icone, cb.couleur, ga.id_groupe, ga.ordre, wa.name FROM ticketing_param_bouton_action pba, ticketing_couleur_bouton  cb, ticketing_groupe_action ga, workflow_action wa WHERE pba.id_couleur = cb.id_couleur AND pba.id_groupe = ga.id_groupe AND pba.id_action  = wa.id_action AND pba.id_action IN ({0}) ORDER BY ga.ordre ASC, pba.ordre ASC";

    private static final int    ID_GROUPE_NON_CONFIGURE             = 1;

    @Override
    public void insert( ParamBouton paramBouton, Plugin plugin )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void store( ParamBouton paramBouton, Plugin plugin )
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<ParamBouton> selectParamBoutonListByGroup( int idGroup, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_GROUP, plugin );
        daoUtil.setInt( 1, idGroup );
        daoUtil.executeQuery( );

        List<ParamBouton> listParamBouton = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            ParamBouton paramBouton = new ParamBouton( );
            fillParamBouton( daoUtil, paramBouton );
            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        return listParamBouton;
    }

    @Override
    public List<ParamBouton> selectParamBoutonListWithoutGroup( Plugin plugin )
    {
        // Récupération des actions sans groupe
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WITHOUT_GROUP, plugin );
        daoUtil.executeQuery( );

        List<ParamBouton> listParamBouton = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            ParamBouton paramBouton = new ParamBouton( );
            paramBouton.setIdAction( daoUtil.getInt( 1 ) );
            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        // Récupération des actions affectées au gruope non configuré
        daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_GROUP, plugin );
        daoUtil.setInt( 1, ID_GROUPE_NON_CONFIGURE );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ParamBouton paramBouton = new ParamBouton( );
            fillParamBouton( daoUtil, paramBouton );
            listParamBouton.add( paramBouton );
        }

        return listParamBouton;
    }

    @Override
    public List<ParamBouton> selectParamBoutonListTicketDetail( List<Integer> lstIdAction, Plugin plugin )
    {

        String unionQuery = StringUtils.join( lstIdAction, "," );

        // Récupération du parametrage pour les boutons d'actions workflow
        DAOUtil daoUtil = new DAOUtil( MessageFormat.format( SQL_QUERY_SELECT_DATA_TICKET_DETAIL, unionQuery ), plugin );
        daoUtil.executeQuery( );

        List<ParamBouton> listParamBouton = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;

            ParamBouton paramBouton = new ParamBouton( );
            paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
            paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
            paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
            paramBouton.setCouleur( daoUtil.getString( nIndex++ ) );
            paramBouton.setIdGroupe( daoUtil.getInt( nIndex++ ) );
            GroupAction groupAction = new GroupAction( );
            groupAction.setIdGroup( paramBouton.getIdGroupe( ) );
            groupAction.setOrdre( daoUtil.getInt( nIndex++ ) );
            paramBouton.setGroupAction( groupAction );
            Action action = new Action( );
            action.setId( paramBouton.getIdAction( ) );
            action.setName( daoUtil.getString( nIndex ) );
            paramBouton.setAction( action );
            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        return listParamBouton;
    }

    private void fillParamBouton( DAOUtil daoUtil, ParamBouton paramBouton )
    {
        int nIndex = 1;

        paramBouton.setIdparam( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
        paramBouton.setCouleur( daoUtil.getString( nIndex++ ) );
        paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
        paramBouton.setIdGroupe( daoUtil.getInt( nIndex ) );
    }
}
