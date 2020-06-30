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
        _dao.delete( nKey, _plugin );
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
