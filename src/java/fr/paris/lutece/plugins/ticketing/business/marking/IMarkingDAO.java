package fr.paris.lutece.plugins.ticketing.business.marking;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IMarkingDAO {
    /**
     * Insert a new record in the table.
     * 
     * @param marking
     *            instance of the Marking object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Marking marking, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param marking
     *            the reference of the Marking
     * @param plugin
     *            the Plugin
     */
    void store( Marking marking, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nKey
     *            The identifier of the marking to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nKey, Plugin plugin );

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the marking
     * @param plugin
     *            the Plugin
     * @return The instance of the marking
     */
    Marking load( int nKey, Plugin plugin );
    
    /**
     * Load the data of all the marking objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the marking objects
     */
    List<Marking> selectMarkingsList( Plugin plugin );
}
