package fr.paris.lutece.plugins.ticketing.business.formcategory;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class FormCategoryHome
{
    // Static variable pointed at the DAO instance
    private static IFormCategoryDao _dao    = SpringContextService.getBean( "ticketing.formCategoryDAO" );
    private static Plugin           _plugin = PluginService.getPlugin( "ticketing" );

    /**
     * Create an instance of the form class
     *
     * @param nIdCategory
     *            The category Id
     * @param nIdForm
     *            The form Id
     */
    public static void create( int nIdCategory, int nIdForm )
    {
        _dao.insert( nIdCategory, nIdForm, _plugin );
    }

    /**
     * Update of the form which is specified in parameter
     *
     * @param nIdCategory
     *            The category Id
     * @param nIdForm
     *            The form Id
     */
    public static void update( int nIdCategory, int nIdForm )
    {
        _dao.store( nIdCategory, nIdForm, _plugin );
    }

    /**
     * Remove the form whose identifier is specified in parameter
     *
     * @param nIdCategory
     *            The category Id
     * @param nIdForm
     *            The form Id
     */
    public static void remove( int nIdCategory, int nIdForm )
    {
        _dao.delete( nIdCategory, nIdForm, _plugin );
    }

    /**
     * Remove the formCategory by id category
     *
     * @param nKey
     *            The category Id
     */
    public static void removeByIdCategory( int nKey )
    {
        _dao.deleteByIdCategory( nKey, _plugin );
    }

    /**
     * Returns an instance of a form whose identifier is specified in parameter
     *
     * @param nIdForm
     *            id form
     *
     * @return an instance of Form
     */
    public static List<FormCategory> findByForm( int nIdForm )
    {
        return _dao.loadByForm( nIdForm, _plugin );
    }

    /**
     * Returns an instance of a form whose identifier is specified in parameter
     *
     * @param nIdCategory
     *            The form primary key
     * @return an instance of Form
     */
    public static List<FormCategory> findByCategory( int nIdCategory )
    {
        return _dao.loadByCategory( nIdCategory, _plugin );
    }

    /**
     * Load the data of all the category objects and returns them as a list
     *
     * @return The list which contains the data of all the category objects
     */
    public static List<FormCategory> findAll( )
    {
        return _dao.selectAll( _plugin );
    }
}
