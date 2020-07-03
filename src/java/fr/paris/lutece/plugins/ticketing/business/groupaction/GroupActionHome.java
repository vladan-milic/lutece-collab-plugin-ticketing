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
package fr.paris.lutece.plugins.ticketing.business.groupaction;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class GroupActionHome
{
    // Static variable pointed at the DAO instance
    private static IGroupActionDAO _dao                = SpringContextService.getBean( "ticketing.groupActionDAO" );
    private static Plugin          _plugin             = PluginService.getPlugin( "ticketing" );
    private static final int       CONSTANT_STEP_ORDER = 1;

    /**
     * Private constructor - this class need not be instantiated
     */
    private GroupActionHome( )
    {
    }

    /**
     * Creates the.
     *
     * @param groupAction
     *            the group action
     * @return the group action
     */
    public static GroupAction create( GroupAction groupAction )
    {
        groupAction = _dao.insert( groupAction, _plugin );

        // MAJ de l'ordre des autres groupes
        changeOrder( groupAction, -1 );

        return groupAction;
    }

    /**
     * Change order.
     *
     * @param groupAction
     *            the group action
     * @param oldOrder
     *            the old order
     */
    private static void changeOrder( GroupAction groupAction, int oldOrder )
    {
        int nNewOrder = groupAction.getOrdre( );

        // Création d'un groupe
        if ( oldOrder == -1 )
        {
            for ( GroupAction groupActionToUpdateOrder : getGroupActionList( ) )
            {
                if ( groupAction.getIdGroup( ) != groupActionToUpdateOrder.getIdGroup( ) )
                {

                    int nGroupActionToUpdateOrder = groupActionToUpdateOrder.getOrdre( );

                    if ( ( nGroupActionToUpdateOrder >= nNewOrder ) )
                    {
                        groupActionToUpdateOrder.setOrdre( nGroupActionToUpdateOrder + CONSTANT_STEP_ORDER );
                        _dao.store( groupActionToUpdateOrder, _plugin );
                    }
                }
            }
        }
        // Update de l'ordre d'un groupe
        else
        {
            if ( nNewOrder < oldOrder )
            {
                for ( GroupAction groupActionToUpdateOrder : getGroupActionList( ) )
                {
                    int nGroupActionToUpdateOrder = groupActionToUpdateOrder.getOrdre( );

                    if ( ( nGroupActionToUpdateOrder >= nNewOrder ) && ( nGroupActionToUpdateOrder < oldOrder ) )
                    {
                        groupActionToUpdateOrder.setOrdre( nGroupActionToUpdateOrder + CONSTANT_STEP_ORDER );
                        _dao.store( groupActionToUpdateOrder, _plugin );
                    }
                }
            }
            else if ( nNewOrder > oldOrder )
            {
                for ( GroupAction groupActionToUpdateOrder : getGroupActionList( ) )
                {
                    int nGroupActionToUpdateOrder = groupActionToUpdateOrder.getOrdre( );

                    if ( ( nGroupActionToUpdateOrder <= nNewOrder ) && ( nGroupActionToUpdateOrder > oldOrder ) )
                    {
                        groupActionToUpdateOrder.setOrdre( nGroupActionToUpdateOrder - CONSTANT_STEP_ORDER );
                        _dao.store( groupActionToUpdateOrder, _plugin );
                    }
                }
            }
        }
    }

    /**
     * Update.
     *
     * @param groupAction
     *            the group action
     * @return the group action
     */
    public static GroupAction update( GroupAction groupAction )
    {
        GroupAction oldGroupAction = findByPrimaryKey( groupAction.getIdGroup( ) );
        changeOrder( groupAction, oldGroupAction.getOrdre( ) );

        _dao.store( groupAction, _plugin );

        return groupAction;
    }

    /**
     * Removes the.
     *
     * @param nKey
     *            the n key
     */
    public static void remove( int nKey )
    {
        GroupAction oldGroupAction = findByPrimaryKey( nKey );

        if ( oldGroupAction != null )
        {
            updateOrderOnRemove( oldGroupAction.getOrdre( ) );
        }

        // Affectation des paramètres du groupe supprimé au groupe par défaut
        reassignParameterFromDeletedGroup( nKey );

        _dao.delete( nKey, _plugin );
    }

    /**
     * Reassign parameter from deleted group.
     *
     * @param nGroup
     *            the n group
     */
    private static void reassignParameterFromDeletedGroup( int nGroup )
    {
        _dao.reassignParameterFromDeletedGroup( nGroup, _plugin );
    }

    /**
     * Update order on remove.
     *
     * @param nOrderId
     *            the n order id
     */
    private static void updateOrderOnRemove( int nOrderId )
    {
        for ( GroupAction groupActionChange : getGroupActionList( ) )
        {
            int nGroupActionToUpdateOrder = groupActionChange.getOrdre( );

            if ( ( nGroupActionToUpdateOrder > nOrderId ) )
            {
                groupActionChange.setOrdre( nGroupActionToUpdateOrder - CONSTANT_STEP_ORDER );
                _dao.store( groupActionChange, _plugin );
            }
        }
    }

    /**
     * Find by primary key.
     *
     * @param nKey
     *            the n key
     * @return the group action
     */
    public static GroupAction findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Gets the group action list.
     *
     * @return the group action list
     */
    public static List<GroupAction> getGroupActionList( )
    {
        return _dao.selectGroupActionList( _plugin );
    }

}
