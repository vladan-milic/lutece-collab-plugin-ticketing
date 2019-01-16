package fr.paris.lutece.plugins.ticketing.business.formcategory;

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

public class FormCategoryDao implements IFormCategoryDao
{
    private static final String SQL_QUERY_FORM_CATEGORY = "INSERT INTO ticketing_form_category (id_form, id_category) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_UPDATE = "UPDATE ticketing_form_category SET id_form = ?, id_category = ?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_form_category WHERE id_form = ? AND id_category = ?";
    private static final String SQL_QUERY_DELETE_BY_ID_CATEGORY = "DELETE FROM ticketing_form_category WHERE id_category = ? ";
    private static final String SQL_QUERY_LOAD_BY_FORM = "SELECT id_form, id_category FROM ticketing_form_category WHERE id_form = ?";
    private static final String SQL_QUERY_LOAD_BY_CATEGORY = "SELECT id_form, id_category FROM ticketing_form_category WHERE id_category = ?";
    private static final String SQL_QUERY_LOAD = "SELECT id_form, id_category FROM ticketing_form_category";

    /**
     * {@inheritDoc }
     */
    public void insert( int nIdCategory, int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FORM_CATEGORY, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nIdCategory );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    public void store( int nIdCategory, int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nIdCategory );
        daoUtil.executeQuery( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    public void delete( int nIdCategory, int nIdForm, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.setInt( 2, nIdCategory );
        daoUtil.executeQuery( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByIdCategory( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_CATEGORY, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * {@inheritDoc }
     */
    public List<FormCategory> loadByForm( int nIdForm, Plugin plugin )
    {
        List<FormCategory> formCategoryList = new ArrayList<FormCategory>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LOAD_BY_FORM, plugin );
        daoUtil.setInt( 1, nIdForm );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            FormCategory formCategory = new FormCategory( );
            int nIndex = 1;

            formCategory.setIdForm( daoUtil.getInt( nIndex++ ) );
            formCategory.setIdCategory( daoUtil.getInt( nIndex++ ) );
            formCategoryList.add( formCategory );
        }

        daoUtil.free( );
        return formCategoryList;
    }

    /**
     * {@inheritDoc }
     */
    public List<FormCategory> loadByCategory( int nIdCategory, Plugin plugin )
    {
        List<FormCategory> formCategoryList = new ArrayList<FormCategory>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LOAD_BY_CATEGORY, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            FormCategory formCategory = new FormCategory( );
            int nIndex = 1;

            formCategory.setIdForm( daoUtil.getInt( nIndex++ ) );
            formCategory.setIdCategory( daoUtil.getInt( nIndex++ ) );
            formCategoryList.add( formCategory );
        }

        daoUtil.free( );
        return formCategoryList;
    }

    /**
     * {@inheritDoc }
     */
    public List<FormCategory> selectAll( Plugin plugin )
    {
        List<FormCategory> formCategoryList = new ArrayList<FormCategory>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_LOAD, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            FormCategory formCategory = new FormCategory( );
            int nIndex = 1;

            formCategory.setIdForm( daoUtil.getInt( nIndex++ ) );
            formCategory.setIdCategory( daoUtil.getInt( nIndex++ ) );
            formCategoryList.add( formCategory );
        }

        daoUtil.free( );
        return formCategoryList;
    }
}
