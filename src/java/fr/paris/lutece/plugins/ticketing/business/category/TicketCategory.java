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

import fr.paris.lutece.plugins.ticketing.service.tree.AbstractNode;
import fr.paris.lutece.plugins.unittree.business.unit.Unit;
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
    public static final String RESOURCE_TYPE = "CATEGORY_DEMAND";
    public static final String PROPERTY_LABEL_RESOURCE_TYPE = "ticketing.category.ressourceType.label";

    // Perimissions
    public static final String PERMISSION_VIEW_LIST = "VIEW_DEMAND_LIST";
    public static final String PROPERTY_LABEL_PERMISSION_VIEW = "ticketing.category.permission.view.label";

    public static final String PERMISSION_VIEW_DETAILS = "VIEW_DEMAND_DETAILS";
    public static final String PROPERTY_LABEL_PERMISSION_VIEW_DETAILS = "ticketing.category.permission.viewDetails.label";

    // Variables declarations
    private int _nId;

    private int _nIdParent;

    private TicketCategoryType _categoryType;

    @NotEmpty( message = "#i18n{ticketing.validation.category.Label.notEmpty}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.category.Label.size}" )
    private String _strLabel;

    private int _nOrder;

    private int _nIdWorkflow;

    @NotEmpty( message = "#i18n{ticketing.validation.category.Code.notEmpty}" )
    @Size( max = 255, message = "#i18n{ticketing.validation.category.Code.size}" )
    private String _strCode;

    private Unit _defaultAssignUnit;

    /**
     * Constructor for Catagory
     */
    public TicketCategory( )
    {
        _nIdParent = -1;
        _categoryType = new TicketCategoryType( );
        _categoryType.setId( -1 );
        _defaultAssignUnit = new Unit( );
        _defaultAssignUnit.setIdUnit( -1 );
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
     * Returns the Label
     * 
     * @return The Label
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
    public Unit getDefaultAssignUnit( )
    {
        return _defaultAssignUnit;
    }

    /**
     * Set the default assign unit
     * 
     * @param defaultAssignUnit
     */
    public void setDefaultAssignUnit( Unit defaultAssignUnit )
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
        return _categoryType;
    }

    /**
     * Set the category type
     * 
     * @param categoryType
     *            the category Type
     */
    public void setCategoryType( TicketCategoryType categoryType )
    {
        _categoryType = categoryType;
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
