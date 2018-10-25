package fr.paris.lutece.plugins.ticketing.business.marking;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class MarkingDAO implements IMarkingDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK    = "SELECT max( id_marking ) FROM ticketing_markings";
    private static final String SQL_QUERY_SELECT    = "SELECT id_marking, title, label_color, background_color FROM ticketing_markings WHERE id_marking = ?";
    private static final String SQL_QUERY_INSERT    = "INSERT INTO ticketing_markings ( id_marking, title, label_color, background_color ) VALUES ( ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE    = "DELETE FROM ticketing_markings WHERE id_marking = ? ";
    private static final String SQL_QUERY_UPDATE    = "UPDATE ticketing_markings SET id_marking = ?, title = ?, label_color = ?, background_color =? WHERE id_marking = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_marking, title, label_color, background_color FROM ticketing_markings";

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
    public void insert( Marking marking, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        marking.setId( newPrimaryKey( plugin ) );

        int nIndex = 1;

        daoUtil.setInt( nIndex++, marking.getId( ) );
        daoUtil.setString( nIndex++, marking.getTitle( ) );
        daoUtil.setString( nIndex++, marking.getLabelColor( ) );
        daoUtil.setString( nIndex++, marking.getBackgroundColor( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Marking load( int nKey, Plugin plugin )
    {
        Marking marking = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nKey );

        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            marking = new Marking( );

            int nIndex = 1;

            marking.setId( daoUtil.getInt( nIndex++ ) );
            marking.setTitle( daoUtil.getString( nIndex++ ) );
            marking.setLabelColor( daoUtil.getString( nIndex++ ) );
            marking.setBackgroundColor( daoUtil.getString( nIndex++ ) );
        }

        daoUtil.free( );

        return marking;
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
    public void store( Marking marking, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int nIndex = 1;

        daoUtil.setInt( nIndex++, marking.getId( ) );
        daoUtil.setString( nIndex++, marking.getTitle( ) );
        daoUtil.setString( nIndex++, marking.getLabelColor( ) );
        daoUtil.setString( nIndex++, marking.getBackgroundColor( ) );
        daoUtil.setInt( nIndex, marking.getId( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Marking> selectMarkingsList( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery( );

        List<Marking> listMarkings = new ArrayList<Marking>( );

        while ( daoUtil.next( ) )
        {
            Marking marking = new Marking( );
            int nIndex = 1;

            marking.setId( daoUtil.getInt( nIndex++ ) );
            marking.setTitle( daoUtil.getString( nIndex++ ) );
            marking.setLabelColor( daoUtil.getString( nIndex++ ) );
            marking.setBackgroundColor( daoUtil.getString( nIndex++ ) );

            listMarkings.add( marking );
        }

        daoUtil.free( );

        return listMarkings;
    }
}
