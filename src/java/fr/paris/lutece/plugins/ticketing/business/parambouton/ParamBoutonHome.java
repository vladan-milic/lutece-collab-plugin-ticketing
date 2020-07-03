/*
 * Copyright (c) 2002-2020, Mairie de Paris
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

// TODO: Auto-generated Javadoc
/**
 * The Class ParamBoutonHome.
 */
public class ParamBoutonHome
{

    /** The dao. */
    // Static variable pointed at the DAO instance
    private static IParamBoutonDAO _dao                = SpringContextService.getBean( "ticketing.paramBoutonDAO" );

    /** The plugin. */
    private static Plugin          _plugin             = PluginService.getPlugin( "ticketing" );

    /** The Constant CONSTANT_STEP_ORDER. */
    private static final int       CONSTANT_STEP_ORDER = 1;

    /**
     * Private constructor - this class need not be instantiated.
     */
    private ParamBoutonHome( )
    {
    }

    /**
     * Creates the.
     *
     * @param paramBouton
     *            the param bouton
     * @return the group action
     */
    public static ParamBouton create( ParamBouton paramBouton )
    {
        _dao.insert( paramBouton, _plugin );

        return paramBouton;
    }

    /**
     * Update group.
     *
     * @param paramBouton
     *            the param bouton
     * @param oldGroupId
     *            the old group id
     * @param oldOrdre
     *            the old ordre
     * @return the param bouton
     */
    public static ParamBouton updateGroup( ParamBouton paramBouton, int oldGroupId, int oldOrdre )
    {
        _dao.updateGroup( paramBouton, _plugin );

        // MAJ de l'ordre de l'ancien groupe
        _dao.updateOrderOldGroup( oldGroupId, oldOrdre, _plugin );

        return paramBouton;
    }

    /**
     * Update without order.
     *
     * @param paramBouton
     *            the param bouton
     * @return the param bouton
     */
    public static ParamBouton updateWithoutOrder( ParamBouton paramBouton )
    {
        _dao.updateWithoutOrder( paramBouton, _plugin );
        return paramBouton;
    }

    /**
     * Select param bouton list by group.
     *
     * @param idGroup
     *            the id group
     * @return the list
     */
    public static List<ParamBouton> selectParamBoutonListByGroup( int idGroup )
    {
        return _dao.selectParamBoutonListByGroup( idGroup, _plugin );
    }

    /**
     * Select param bouton list without group.
     *
     * @return the list
     */
    public static List<ParamBouton> selectParamBoutonListWithoutGroup( )
    {
        return _dao.selectParamBoutonListWithoutGroup( _plugin );
    }

    /**
     * Get Configuration action workflow button.
     *
     * @param lstAction
     *            list action workflow
     *
     * @return button configuration
     */
    public static List<ParamBouton> getParamBouttonWorkflowAction( Collection<Action> lstAction )
    {

        List<ParamBouton> lstParamButton = _dao.selectParamBoutonListTicketDetail( lstAction.stream( ).map( Action::getId ).collect( Collectors.toList( ) ), _plugin );

        lstAction.forEach( ( Action action ) ->
        {
            for ( ParamBouton paramButton : lstParamButton )
            {
                if ( paramButton.getIdAction( ) == action.getId( ) )
                {
                    paramButton.setAction( action );
                }
            }
        } );

        return lstParamButton;
    }

    /**
     * Find by primary key.
     *
     * @param nKey
     *            the n key
     * @return the param bouton
     */
    /*
     * Find by primary key.
     *
     * @param nKey the n key
     *
     * @return the param bouton
     */
    public static ParamBouton findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Gets the couleurs list.
     *
     * @return the couleurs list
     */
    public static List<String> getCouleursList( )
    {
        return _dao.getCouleursList( _plugin );
    }

    /**
     * Creates the defaut param.
     */
    public static void createDefautParam( )
    {
        // Récupération des actions sans parametre
        List<ParamBouton> paramBoutonList = selectParamBoutonListWithoutGroup( );

        // Création du parametre par défaut
        paramBoutonList.stream( ).forEach( ParamBoutonHome::create );
    }

    /**
     * Change order.
     *
     * @param paramBouton
     *            the param bouton
     * @param nNewOrder
     *            the n new order
     */
    public static void changeOrder( ParamBouton paramBouton, int nNewOrder )
    {
        // MAJ de l'ordre des autres parametres du groupe
        int oldOrder = paramBouton.getOrdre( );
        if ( nNewOrder < oldOrder )
        {
            for ( ParamBouton ParamBoutonToUpdateOrder : selectParamBoutonListByGroup( paramBouton.getIdGroupe( ) ) )
            {
                int nParamBoutonToUpdateOrder = ParamBoutonToUpdateOrder.getOrdre( );

                if ( ( nParamBoutonToUpdateOrder >= nNewOrder ) && ( nParamBoutonToUpdateOrder < oldOrder ) )
                {
                    ParamBoutonToUpdateOrder.setOrdre( nParamBoutonToUpdateOrder + CONSTANT_STEP_ORDER );
                    _dao.store( ParamBoutonToUpdateOrder, _plugin );
                }
            }
        }
        else if ( nNewOrder > oldOrder )
        {
            for ( ParamBouton ParamBoutonToUpdateOrder : selectParamBoutonListByGroup( paramBouton.getIdGroupe( ) ) )
            {
                int nParamBoutonToUpdateOrder = ParamBoutonToUpdateOrder.getOrdre( );

                if ( ( nParamBoutonToUpdateOrder <= nNewOrder ) && ( nParamBoutonToUpdateOrder > oldOrder ) )
                {
                    ParamBoutonToUpdateOrder.setOrdre( nParamBoutonToUpdateOrder - CONSTANT_STEP_ORDER );
                    _dao.store( ParamBoutonToUpdateOrder, _plugin );
                }
            }
        }

        // MAJ du paramètre
        paramBouton.setOrdre( nNewOrder );
        _dao.store( paramBouton, _plugin );
    }

}
