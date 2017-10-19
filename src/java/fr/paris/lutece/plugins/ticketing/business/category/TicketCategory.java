/*
 * Copyright (c) 2002-2016, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.ticketing.business.category;

import fr.paris.lutece.plugins.ticketing.business.assignee.AssigneeUnit;
import fr.paris.lutece.plugins.ticketing.service.tree.AbstractNode;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
 * This is the business class for the object Category
 */
public class TicketCategory extends AbstractNode implements Serializable, RBACResource
{
    private static final long serialVersionUID = 1L;

    // RBAC management
    public static final String RESOURCE_TYPE = "TICKET_CATEGORY";
    public static final String PROPERTY_LABEL_RESOURCE_TYPE = "ticketing.category.ressourceType.label";

    // Permissions
    public static final String PERMISSION_VIEW_LIST = "VIEW_LIST";
    public static final String PROPERTY_LABEL_PERMISSION_VIEW = "ticketing.category.permission.view.label";

    public static final String PERMISSION_VIEW_DETAIL = "VIEW_DETAIL";
    public static final String PROPERTY_LABEL_PERMISSION_VIEW_DETAIL = "ticketing.category.permission.viewDetail.label";

    public static final String PERMISSION_BELONG_TO = "BELONG_TO";
    public static final String PROPERTY_LABEL_BELONG_TO = "ticketing.category.permission.belongTo.label";
    
    // Variables declarations
    private int _nId;

    private int _nIdParent;

    @NotEmpty( message = "#i18n{ticketing.validation.category.Label.notEmpty}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.category.Label.size}" )
    private String _strLabel;

    private int _nOrder;

    private int _nIdWorkflow;

    @Size( max = 255, message = "#i18n{ticketing.validation.category.Code.size}" )
    private String _strCode;

    private AssigneeUnit _defaultAssignUnit;

    private List<Integer> _listIdInput;

    @Digits(integer=6, fraction=0, message = "#i18n{ticketing.validation.category.DemandId.int}" )
    private int _nDemandId;
    
    @Size( max = 500, message = "#i18n{ticketing.validation.category.HelpMessage.size}" )
    private String _strHelpMessage;
    
    /**
     * Constructor TicketCategory
     */
    public TicketCategory( )
    {
        _nIdParent = -1;
        _depth = new TicketCategoryType( );
        ( (TicketCategoryType) _depth ).setId( -1 );
        _defaultAssignUnit = new AssigneeUnit( );
        _defaultAssignUnit.setUnitId( -1 );
        _nIdWorkflow = 1;

    }

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    @Override
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the IdParent
     * 
     * @return The IdParent
     */
    @Override
    public int getIdParent( )
    {
        return _nIdParent;
    }

    /**
     * Sets the IdParent
     * 
     * @param nIdParent
     *            The IdParent
     */

    public void setIdParent( int nIdParent )
    {
        _nIdParent = nIdParent;
    }

    /**
     * Returns the categoryType
     * 
     * @return The categoryType
     */
    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * Sets the Label
     * 
     * @param strLabel
     *            The Label
     */
    public void setLabel( String strLabel )
    {
        _strLabel = strLabel;
    }

    /**
     * Returns the Order
     * 
     * @return The Order
     */
    public int getOrder( )
    {
        return _nOrder;
    }

    /**
     * Sets the Order
     * 
     * @param nOrder
     *            The Order
     */
    public void setOrder( int nOrder )
    {
        _nOrder = nOrder;
    }

    /**
     * Returns the Code
     * 
     * @return The Code
     */
    public String getCode( )
    {
        return _strCode;
    }

    /**
     * Sets the Code
     * 
     * @param strCode
     *            The Code
     */
    public void setCode( String strCode )
    {
        _strCode = strCode;
    }

    /**
     * Get the default assign unit
     * 
     * @return the default assign unit
     */
    public AssigneeUnit getDefaultAssignUnit( )
    {
        return _defaultAssignUnit;
    }

    /**
     * Set the default assign unit
     * 
     * @param defaultAssignUnit
     */
    public void setDefaultAssignUnit( AssigneeUnit defaultAssignUnit )
    {
        _defaultAssignUnit = defaultAssignUnit;
    }

    /**
     * Get the category type
     * 
     * @return the category Type
     */
    public TicketCategoryType getCategoryType( )
    {
        return (TicketCategoryType) _depth;
    }

    /**
     * Set the category type
     * 
     * @param categoryType
     *            the category Type
     */
    public void setCategoryType( TicketCategoryType categoryType )
    {
        _depth = categoryType;
    }

    /**
     * Get the id of the workflow related to the category
     * 
     * @return the id workflow
     */
    public int getIdWorkflow( )
    {
        return _nIdWorkflow;
    }

    /**
     * Set the id of the workflow related to the category
     * 
     * @param nIdWorkflow
     *            the workflow id
     */
    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }  

    /**
     * @return the _listIdInput
     */
    public List<Integer> getListIdInput( )
    {
        return _listIdInput;
    }

    /**
     * @param listIdInput
     *            the listIdInput to set
     */
    public void setListIdInput( List<Integer> listIdInput )
    {
        this._listIdInput = listIdInput;
    }

    /**
     * Returns the DemandId
     * 
     * @return The DemandId
     */
    public int getDemandId( )
    {
        return _nDemandId;
    }

    /**
     * Sets the DemandId
     * 
     * @param nDemandId
     *            The DemandId
     */
    public void setDemandId( int nDemandId )
    {
        _nDemandId = nDemandId;
    }

    /**
     * @return the _strHelpMessage
     */
    public String getHelpMessage( )
    {
        return _strHelpMessage;
    }

    /**
     * @param _strHelpMessage
     *            the _strHelpMessage to set
     */
    public void setHelpMessage( String _strHelpMessage )
    {
        this._strHelpMessage = _strHelpMessage;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public TicketCategory getParent( )
    {
        return (TicketCategory) super.getParent( );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> getChildren( )
    {
        List<TicketCategory> listCategories = (List<TicketCategory>) (List<?>) _childrenNodes;
        return listCategories;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> getLeaves( )
    {
        List<TicketCategory> listCategories = (List<TicketCategory>) (List<?>) _leaves;
        return listCategories;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public List<TicketCategory> getBranch( )
    {
        List<TicketCategory> listCategories = (List<TicketCategory>) (List<?>) super.getBranch( );
        return listCategories;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getResourceId( )
    {
        return Integer.toString( _nId );
    }
}
