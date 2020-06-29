package fr.paris.lutece.plugins.ticketing.business.parambouton;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class ParamBoutonHome
{
    // Static variable pointed at the DAO instance
    private static IParamBoutonDAO _dao    = SpringContextService.getBean( "ticketing.paramBoutonDAO" );
    private static Plugin          _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ParamBoutonHome( )
    {
    }

    /**
     * Creates the.
     *
     * @param groupAction
     *            the group action
     * @return the group action
     */
    public static ParamBouton create( ParamBouton paramBouton )
    {
        _dao.insert( paramBouton, _plugin );

        return paramBouton;
    }

    /**
     * Update.
     *
     * @param groupAction
     *            the group action
     * @return the group action
     */
    public static ParamBouton update( ParamBouton paramBouton )
    {
        _dao.store( paramBouton, _plugin );

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

}
