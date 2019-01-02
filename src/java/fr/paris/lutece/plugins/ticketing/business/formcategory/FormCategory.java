package fr.paris.lutece.plugins.ticketing.business.formcategory;

public class FormCategory
{
    private int _nIdForm;
    private int _nIdCategory;

    /**
     * Constructor TicketFormCategory
     */
    public FormCategory( )
    {
        setIdForm( -1 );
        setIdCategory( -1 );
    }

    /**
     * get id formulaire
     *
     * @return id formulaire
     */
    public int getIdForm( )
    {
        return _nIdForm;
    }

    /**
     * set id formulaire
     *
     * @param _nIdForm
     *            id formulaire
     */
    public void setIdForm( int _nIdForm )
    {
        this._nIdForm = _nIdForm;
    }

    /**
     * get id category
     *
     * @return id category
     */
    public int getIdCategory( )
    {
        return _nIdCategory;
    }

    /**
     * set id category
     *
     * @param _nIdCategory
     *            id category
     */
    public void setIdCategory( int _nIdCategory )
    {
        this._nIdCategory = _nIdCategory;
    }

}
