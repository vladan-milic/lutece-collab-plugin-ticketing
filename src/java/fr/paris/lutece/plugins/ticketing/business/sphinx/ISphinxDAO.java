package fr.paris.lutece.plugins.ticketing.business.sphinx;

import java.util.List;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface ISphinxDAO
{

    /**
     * Retrieve the list of the new mailing list of the day
     *
     * @param idAction
     *            action id
     *
     * @param plugin
     *            the plugin
     * @return the list of the new mailing list of the day
     */
    List<Mailing> getNewMailingList( int idAction, Plugin plugin );
}
