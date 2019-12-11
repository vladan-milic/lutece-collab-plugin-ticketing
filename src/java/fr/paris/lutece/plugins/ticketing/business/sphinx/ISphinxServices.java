package fr.paris.lutece.plugins.ticketing.business.sphinx;


public interface ISphinxServices
{

    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "ticketing.sphinxServices";

    /**
     * update index with modelResponse
     *
     * @return message
     *
     */
    String mailingToSphinx( );

}
