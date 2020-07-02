package fr.paris.lutece.plugins.ticketing.business.groupaction;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class GroupActionDAO implements IGroupActionDAO
{

    // Constants
    private static final String SQL_QUERY_SELECT_MAX_ID_GROUP                 = "SELECT max(id_groupe) FROM ticketing_groupe_action";
    private static final String SQL_QUERY_SELECT                              = "SELECT id_groupe, libelle_identifiant, cle, description, ordre FROM ticketing_groupe_action WHERE id_groupe = ?";
    private static final String SQL_QUERY_INSERT                              = "INSERT INTO ticketing_groupe_action (libelle_identifiant, cle, description, ordre) VALUES(?,?,?,?)";
    private static final String SQL_QUERY_DELETE                              = "DELETE FROM ticketing_groupe_action WHERE id_groupe=?";
    private static final String SQL_QUERY_UPDATE                              = "UPDATE ticketing_groupe_action SET libelle_identifiant=?, cle=?, description=?, ordre=? WHERE id_groupe=?";
    private static final String SQL_QUERY_UPDATE_PARAMETER_FROM_DELETED_GROUP = "UPDATE ticketing_param_bouton_action SET id_groupe=1, ordre=(select IFNULL(max(ordre)+1,1) from ticketing_param_bouton_action where id_groupe=1) WHERE id_groupe=?";
    private static final String SQL_QUERY_SELECTALL                           = "SELECT id_groupe, libelle_identifiant, cle, description, ordre FROM ticketing_groupe_action order by ordre asc ";

    private int getMaxIdGroup( Plugin plugin )
    {
        int maxIdGroup = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_ID_GROUP, plugin );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            maxIdGroup = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return maxIdGroup;
    }

    /**
     * Insert.
     *
     * @param groupAction
     *            the group action
     * @param plugin
     *            the plugin
     */
    @Override
    public GroupAction insert( GroupAction groupAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nIndex = 1;

        daoUtil.setString( nIndex++, groupAction.getLibelleIdentifiant( ) );
        daoUtil.setString( nIndex++, groupAction.getCle( ) );
        daoUtil.setString( nIndex++, groupAction.getDescription( ) );
        daoUtil.setInt( nIndex, groupAction.getOrdre( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        groupAction.setIdGroup( getMaxIdGroup( plugin ) );

        return groupAction;

    }

    /**
     * Store.
     *
     * @param groupAction
     *            the group action
     * @param plugin
     *            the plugin
     */
    @Override
    public void store( GroupAction groupAction, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setString( nIndex++, groupAction.getLibelleIdentifiant( ) );
        daoUtil.setString( nIndex++, groupAction.getCle( ) );
        daoUtil.setString( nIndex++, groupAction.getDescription( ) );
        daoUtil.setInt( nIndex++, groupAction.getOrdre( ) );
        daoUtil.setInt( nIndex, groupAction.getIdGroup( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

    }

    /**
     * Delete.
     *
     * @param nKey
     *            the n key
     * @param plugin
     *            the plugin
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
     * Load.
     *
     * @param nKey
     *            the n key
     * @param plugin
     *            the plugin
     * @return the group action
     */
    @Override
    public GroupAction load( int nKey, Plugin plugin )
    {
        GroupAction groupAction = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            groupAction = new GroupAction( );
            fillGroupAction( daoUtil, groupAction );
        }

        daoUtil.free( );

        return groupAction;
    }

    /**
     * Select group action list.
     *
     * @param plugin
     *            the plugin
     * @return the list
     */
    @Override
    public List<GroupAction> selectGroupActionList( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        List<GroupAction> listGroupAction = new ArrayList<>( );

        while ( daoUtil.next( ) )
        {
            GroupAction groupAction = new GroupAction( );
            fillGroupAction( daoUtil, groupAction );
            listGroupAction.add( groupAction );
        }

        daoUtil.free( );

        return listGroupAction;
    }

    /**
     * Fill group action.
     *
     * @param daoUtil
     *            the dao util
     * @param groupAction
     *            the group action
     */
    private void fillGroupAction( DAOUtil daoUtil, GroupAction groupAction )
    {
        int nIndex = 1;

        groupAction.setIdGroup( daoUtil.getInt( nIndex++ ) );
        groupAction.setLibelleIdentifiant( daoUtil.getString( nIndex++ ) );
        groupAction.setCle( daoUtil.getString( nIndex++ ) );
        groupAction.setDescription( daoUtil.getString( nIndex++ ) );
        groupAction.setOrdre( daoUtil.getInt( nIndex ) );
    }

    /**
     * Reassign parameter from deleted group.
     *
     * @param nGroup
     *            the n group
     * @param _plugin
     *            the plugin
     */
    @Override
    public void reassignParameterFromDeletedGroup( int nGroup, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_PARAMETER_FROM_DELETED_GROUP, plugin );

        daoUtil.setInt( 1, nGroup );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
