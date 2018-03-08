package fr.paris.lutece.plugins.ticketing.business.formcategory;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IFormCategoryDao
{
    /**
     * Insert into ticketing_form_category
     * 
     * @param nIdCategory
     * 
     * @param nIdForm
     * @param plugin
     */
    public void insert(int nIdCategory, int nIdForm, Plugin plugin);
    
    /**
     * Update the record in the table
     * 
     * @param nIdCategory
     * @param nIdForm
     * @param plugin
     */
    void store( int nIdCategory, int nIdForm, Plugin plugin );
    
    /**
     * Delete a record from the table
     * 
     * @param nIdCategory
     * @param nIdForm
     * @param plugin
     */
    void delete( int nIdCategory, int nIdForm, Plugin plugin );
    
    /**
     * load list by formulaire id
     * 
     * @param nIdForm
     * @param plugin
     * @return
     */
    public List<TicketFormCategory> loadByForm( int nIdForm, Plugin plugin );
    
    /**
     * 
     * @param nIdCategory
     * @param plugin
     * @return
     */
    public List<TicketFormCategory> loadByCategory( int nIdCategory, Plugin plugin );
    
    /**
     * Load the data of all the category objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the category objects
     */
    public List<TicketFormCategory> selectAll( Plugin plugin );
}
