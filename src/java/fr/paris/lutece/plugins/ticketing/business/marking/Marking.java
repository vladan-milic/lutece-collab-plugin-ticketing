package fr.paris.lutece.plugins.ticketing.business.marking;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Marking implements Serializable 
{
	
	private static final long serialVersionUID = 1L;

	// Variables declarations
    private int _nId;
    
    @NotEmpty( message = "#i18n{ticketing.validation.marking.Title.notEmpty}" )
    @Size( max = 500, message = "#i18n{ticketing.validation.marking.Title.size}" )
    private String _strTitle;
    @NotEmpty( message = "#i18n{ticketing.validation.marking.LabelColor.notEmpty}" )
    @Size( max = 500, message = "#i18n{ticketing.validation.marking.LabelColor.size}" )
    private String _strLabelColor;
    @NotEmpty( message = "#i18n{ticketing.validation.marking.BackgroundColor.notEmpty}" )
    @Size( max = 500, message = "#i18n{ticketing.validation.marking.BackgroundColor.size}" )
    private String _strBackgroundColor;
    
	/**
	 * @return the _nId
	 */
	public int getId() 
	{
		return _nId;
	}
	
	/**
	 * @param nId the nId to set
	 */
	public void setId(int nId) 
	{
		this._nId = nId;
	}
	
	/**
	 * @return the _strTitle
	 */
	public String getTitle() 
	{
		return _strTitle;
	}
	
	/**
	 * @param strTitle the strTitle to set
	 */
	public void setTitle(String strTitle) 
	{
		this._strTitle = strTitle;
	}
	
	/**
	 * @return the _strLabelColor
	 */
	public String getLabelColor() 
	{
		return _strLabelColor;
	}
	
	/**
	 * @param strLabelColor the strLabelColor to set
	 */
	public void setLabelColor(String strLabelColor) 
	{
		this._strLabelColor = strLabelColor;
	}
	
	/**
	 * @return the _strBackgroundColor
	 */
	public String getBackgroundColor() 
	{
		return _strBackgroundColor;
	}
	
	/**
	 * @param strBackgroundColor the strBackgroundColor to set
	 */
	public void setBackgroundColor(String strBackgroundColor) 
	{
		this._strBackgroundColor = strBackgroundColor;
	}
    
    
}
