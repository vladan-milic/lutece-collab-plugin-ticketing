package fr.paris.lutece.plugins.ticketing.business.sphinx;

import java.io.IOException;

public interface ISphinxServices
{

    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "ticketing.sphinxServices";

    /**
     * update index with modelResponse
     *
     * @return message
     * @throws IOException
     *             IOException
     *
     */
    String mailingToSphinx( );

}
