package fr.paris.lutece.plugins.ticketing.business.marking;

import java.util.List;

import fr.paris.lutece.plugins.ticketing.service.TicketCacheService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class MarkingHome
{
    // Static variable pointed at the DAO instance
    private static IMarkingDAO _dao = SpringContextService.getBean( "ticketing.markingDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "ticketing" );
    private static TicketCacheService _ticketCacheService = TicketCacheService.getInstance( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private MarkingHome( )
    {
    }

    /**
     * Create an instance of the marking class
     *
     * @param marking
     *            The instance of the Marking which contains the informations to store
     * @return The instance of marking which has been created with its primary key.
     */
    public static Marking create( Marking marking )
    {
        _dao.insert( marking, _plugin );

        return marking;
    }

    /**
     * Update of the marking which is specified in parameter
     *
     * @param marking
     *            The instance of the Marking which contains the data to store
     * @return The instance of the marking which has been updated
     */
    public static Marking update( Marking marking )
    {
        String strCacheKey = _ticketCacheService.getMarkingByIdCacheKey( marking.getId( ) );
        _ticketCacheService.putInCache( strCacheKey, marking );
        _dao.store( marking, _plugin );

        return marking;
    }

    /**
     * Remove the marking whose identifier is specified in parameter
     *
     * @param nKey
     *            The marking Id
     */
    public static void remove( int nKey )
    {
        String strCacheKey = _ticketCacheService.getMarkingByIdCacheKey( nKey );
        _ticketCacheService.removeKey( strCacheKey );
        _dao.delete( nKey, _plugin );
    }

    /**
     * Returns an instance of a marking whose identifier is specified in parameter
     *
     * @param nKey
     *            The marking primary key
     * @return an instance of Marking
     */
    public static Marking findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the marking objects and returns them as a list
     *
     * @return the list which contains the data of all the marking objects
     */
    public static List<Marking> getMarkingsList( )
    {
        return _dao.selectMarkingsList( _plugin );
    }

    /**
     * Load the marking from cache or put it in if doesn't exist
     *
     * @param nIdMarking
     *            nIdMarking
     *
     * @return the marking object
     */
    public static Marking loadMarkingFromCache( int nIdMarking )
    {
        String strCacheKey = _ticketCacheService.getMarkingByIdCacheKey( nIdMarking );

        Marking marking = (Marking) _ticketCacheService.getFromCache( strCacheKey );

        if ( marking == null )
        {
            marking = MarkingHome.findByPrimaryKey( nIdMarking );
            if ( marking != null )
            {
                strCacheKey = _ticketCacheService.getMarkingByIdCacheKey( marking.getId( ) );
                _ticketCacheService.putInCache( strCacheKey, marking );
            }
        }

        return marking;
    }
}
