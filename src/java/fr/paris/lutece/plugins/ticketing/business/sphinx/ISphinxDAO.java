package fr.paris.lutece.plugins.ticketing.business.sphinx;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface ISphinxDAO {

    /**
     * Retrieve the list of the new mailing list of the day
     * @param plugin
     *            the plugin
     */
	List<Mailing> getNewMailingList(int idAction, Plugin plugin );
}