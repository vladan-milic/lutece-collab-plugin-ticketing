


package fr.paris.lutece.plugins.ticketing.business.modelresponse.search;

import fr.paris.lutece.portal.service.daemon.Daemon;


public class ModelResponsesIndexerDaemon extends Daemon
{
    
    

    /**
     * {@inheritDoc }
     */
    @Override
    public void run()  
    {
        setLastRunLogs( LuceneModelResponseIndexerServices.instance().addAll());
    }

}
