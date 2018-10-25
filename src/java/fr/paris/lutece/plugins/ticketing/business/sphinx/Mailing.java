package fr.paris.lutece.plugins.ticketing.business.sphinx;

public class Mailing
{

    private int    _nId;
    private String _strEMail;
    private String _strDuration;
    private String _strDomains;

    /**
     * @return the _nId
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * @param _nId
     *            the _nId to set
     */
    public void setId( int _nId )
    {
        this._nId = _nId;
    }

    /**
     * @return the _strEMail
     */
    public String getEMail( )
    {
        return _strEMail;
    }

    /**
     * @param _strEMail
     *            the _strEMail to set
     */
    public void setEMail( String _strEMail )
    {
        this._strEMail = _strEMail;
    }

    /**
     * @return the _strDuration
     */
    public String getDuration( )
    {
        return _strDuration;
    }

    /**
     * @param _strDuration
     *            the _strDuration to set
     */
    public void setDuration( String _strDuration )
    {
        this._strDuration = _strDuration;
    }

    /**
     * @return the _strDomains
     */
    public String getDomains( )
    {
        return _strDomains;
    }

    /**
     * @param _strDomains
     *            the _strDomains to set
     */
    public void setDomains( String _strDomains )
    {
        this._strDomains = _strDomains;
    }

}
