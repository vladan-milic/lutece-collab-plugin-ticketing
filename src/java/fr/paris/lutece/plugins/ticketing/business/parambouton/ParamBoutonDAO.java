/*
<<<<<<< HEAD
 * Copyright (c) 2002-2020, Mairie de Paris
=======
 * Copyright (c) 2002-2020, City of Paris
>>>>>>> GRUTICKET-23
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
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class ParamBoutonDAO implements IParamBoutonDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT                    = "SELECT id_param, id_action, id_couleur, ordre, icone, id_groupe FROM ticketing_param_bouton_action where id_param=?";
    private static final String SQL_QUERY_SELECT_ORDER              = "select IFNULL(max(ordre)+1,1) from ticketing_param_bouton_action where id_groupe=?";
    private static final String SQL_QUERY_INSERT                    = "INSERT INTO ticketing_param_bouton_action (id_action, id_couleur, ordre, icone, id_groupe) VALUES( ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_UPDATE                    = "UPDATE ticketing_param_bouton_action SET id_action=?, id_couleur=?, ordre=?, icone=?, id_groupe=? WHERE id_param=?";
    private static final String SQL_QUERY_UPDATE_WITHOUT_ORDER      = "UPDATE ticketing_param_bouton_action SET id_couleur=?, icone=? WHERE id_param=?";
    private static final String SQL_QUERY_UPDATE_GROUP              = "UPDATE ticketing_param_bouton_action SET ordre=?, id_groupe=? WHERE id_param=?";
    private static final String SQL_QUERY_UPDATE_ORDER_OLD_GROUP    = "UPDATE ticketing_param_bouton_action SET ordre=(ordre-1) where id_groupe=? and ordre>?";
    private static final String SQL_QUERY_SELECT_BY_GROUP           = "select id_param, param.id_couleur, param.id_action, couleur, ordre, icone, id_groupe,  name, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from ticketing_param_bouton_action param join ticketing_couleur_bouton couleur on couleur.id_couleur = param.id_couleur join workflow_action act on act.id_action = param.id_action where id_groupe=? order by ordre asc";
    private static final String SQL_QUERY_SELECT_WITHOUT_GROUP      = "SELECT id_action, name, (select name from  workflow_state state where id_state =id_state_before) state_before , (select name from  workflow_state state where id_state =id_state_after) state_after from workflow_action act where id_action not in (select id_action FROM ticketing_param_bouton_action)";
    private static final String SQL_QUERY_SELECT_COULEUR_LIST       = "SELECT id_couleur from ticketing_couleur_bouton";
    private static final String SQL_QUERY_SELECT_DATA_TICKET_DETAIL = "SELECT pba.id_action, pba.ordre, pba.icone, cb.couleur, ga.id_groupe, ga.ordre, wa.name FROM ticketing_param_bouton_action pba, ticketing_couleur_bouton  cb, ticketing_groupe_action ga, workflow_action wa WHERE pba.id_couleur = cb.id_couleur AND pba.id_groupe = ga.id_groupe AND pba.id_action  = wa.id_action AND pba.id_action IN ({0}) ORDER BY ga.ordre ASC, pba.ordre ASC";

    private static final int    DEFAULT_GROUPE                      = 1;
    private static final String DEFAULT_COLOR                       = "Bleu foncé";
    private static final String DEFAULT_ICONE                       = "fa fa-question-circle-o";

    /**
     * Gets the new order group.
     *
     * @param idGroupe
     *            the id groupe
     * @param plugin
     *            the plugin
     * @return the new order group
     */
    private int getNewOrderGroup( int idGroupe, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ORDER, plugin );
        daoUtil.setInt( 1, idGroupe );
        daoUtil.executeQuery( );
        int newOrder = 1;

        if ( daoUtil.next( ) )
        {
            newOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return newOrder;
    }

    /**
     * Insert.
     *
     * @param paramBouton
     *            the param bouton
     * @param plugin
     *            the plugin
     */
    @Override
    public void insert( ParamBouton paramBouton, Plugin plugin )
    {
        paramBouton.setOrdre( getNewOrderGroup( paramBouton.getIdGroupe( ), plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, paramBouton.getIdAction( ) );
        daoUtil.setString( nIndex++, paramBouton.getIdCouleur( ) );
        daoUtil.setInt( nIndex++, paramBouton.getOrdre( ) );
        daoUtil.setString( nIndex++, paramBouton.getIcone( ) );
        daoUtil.setInt( nIndex, paramBouton.getIdGroupe( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Store.
     *
     * @param paramBouton
     *            the param bouton
     * @param plugin
     *            the plugin
     */
    @Override
    public void store( ParamBouton paramBouton, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, paramBouton.getIdAction( ) );
        daoUtil.setString( nIndex++, paramBouton.getIdCouleur( ) );
        daoUtil.setInt( nIndex++, paramBouton.getOrdre( ) );
        daoUtil.setString( nIndex++, paramBouton.getIcone( ) );
        daoUtil.setInt( nIndex++, paramBouton.getIdGroupe( ) );
        daoUtil.setInt( nIndex, paramBouton.getIdparam( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Select param bouton list by group.
     *
     * @param idGroup
     *            the id group
     * @param plugin
     *            the plugin
     * @return the list
     */
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

    /**
     * Select param bouton list without group.
     *
     * @param plugin
     *            the plugin
     * @return the list
     */
    @Override
    public List<ParamBouton> selectParamBoutonListWithoutGroup( Plugin plugin )
    {
        List<ParamBouton> listParamBouton = new ArrayList<>( );

        // Récupération des actions sans groupe
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_WITHOUT_GROUP, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;
            ParamBouton paramBouton = new ParamBouton( );
            Action action = new Action( );
            State stateBefore = new State( );
            State stateAfter = new State( );

            paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );

            action.setName( daoUtil.getString( nIndex++ ) );
            stateBefore.setName( daoUtil.getString( nIndex++ ) );
            stateAfter.setName( daoUtil.getString( nIndex ) );
            action.setStateAfter( stateAfter );
            action.setStateBefore( stateBefore );

            paramBouton.setAction( action );
            paramBouton.setIcone( DEFAULT_ICONE );
            paramBouton.setIdCouleur( DEFAULT_COLOR );
            paramBouton.setIdGroupe( DEFAULT_GROUPE );
            paramBouton.setOrdre( getNewOrderGroup( DEFAULT_GROUPE, plugin ) );

            listParamBouton.add( paramBouton );
        }

        daoUtil.free( );

        return listParamBouton;
    }

    /**
     * Select param bouton list ticket detail.
     *
     * @param lstIdAction
     *            the lst id action
     * @param plugin
     *            the plugin
     * @return the list
     */
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

        Action action = new Action( );
        State stateBefore = new State( );
        State stateAfter = new State( );

        paramBouton.setIdparam( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIdCouleur( daoUtil.getString( nIndex++ ) );
        paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
        paramBouton.setCouleur( daoUtil.getString( nIndex++ ) );
        paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
        paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
        paramBouton.setIdGroupe( daoUtil.getInt( nIndex++ ) );
        action.setName( daoUtil.getString( nIndex++ ) );
        stateBefore.setName( daoUtil.getString( nIndex++ ) );
        stateAfter.setName( daoUtil.getString( nIndex ) );

        action.setStateAfter( stateAfter );
        action.setStateBefore( stateBefore );
        paramBouton.setAction( action );
    }

    /**
     * Load.
     *
     * @param nKey
     *            the n key
     * @param plugin
     *            the plugin
     * @return the param bouton
     */
    @Override
    public ParamBouton load( int nKey, Plugin plugin )
    {
        ParamBouton paramBouton = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            int nIndex = 1;
            paramBouton = new ParamBouton( );
            paramBouton.setIdparam( daoUtil.getInt( nIndex++ ) );
            paramBouton.setIdAction( daoUtil.getInt( nIndex++ ) );
            paramBouton.setIdCouleur( daoUtil.getString( nIndex++ ) );
            paramBouton.setOrdre( daoUtil.getInt( nIndex++ ) );
            paramBouton.setIcone( daoUtil.getString( nIndex++ ) );
            paramBouton.setIdGroupe( daoUtil.getInt( nIndex ) );
        }

        daoUtil.free( );

        return paramBouton;
    }

    /**
     * Update group.
     *
     * @param paramBouton
     *            the param bouton
     * @param plugin
     *            the plugin
     */
    @Override
    public void updateGroup( ParamBouton paramBouton, Plugin plugin )
    {
        paramBouton.setOrdre( getNewOrderGroup( paramBouton.getIdGroupe( ), plugin ) );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_GROUP, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, paramBouton.getOrdre( ) );
        daoUtil.setInt( nIndex++, paramBouton.getIdGroupe( ) );
        daoUtil.setInt( nIndex, paramBouton.getIdparam( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update without order.
     *
     * @param paramBouton
     *            the param bouton
     * @param plugin
     *            the plugin
     */
    @Override
    public void updateWithoutOrder( ParamBouton paramBouton, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_WITHOUT_ORDER, plugin );
        int nIndex = 1;

        daoUtil.setString( nIndex++, paramBouton.getIdCouleur( ) );
        daoUtil.setString( nIndex++, paramBouton.getIcone( ) );
        daoUtil.setInt( nIndex, paramBouton.getIdparam( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Gets the couleurs list.
     *
     * @param plugin
     *            the plugin
     * @return the couleurs list
     */
    @Override
    public List<String> getCouleursList( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COULEUR_LIST, plugin );
        daoUtil.executeQuery( );

        List<String> listCouleur = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            listCouleur.add( daoUtil.getString( 1 ) );
        }

        daoUtil.free( );

        return listCouleur;
    }

    /**
     * Update order old group.
     *
     * @param oldGroupId
     *            the old group id
     * @param oldOrdre
     *            the old ordre
     * @param plugin
     *            the plugin
     */
    @Override
    public void updateOrderOldGroup( int oldGroupId, int oldOrdre, Plugin plugin )
    {

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ORDER_OLD_GROUP, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, oldGroupId );
        daoUtil.setInt( nIndex, oldOrdre );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
