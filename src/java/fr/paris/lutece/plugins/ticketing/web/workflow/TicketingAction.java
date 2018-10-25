/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.web.workflow;

import java.util.Collection;

import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.icon.Icon;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;

/**
 * This class is a decorator of the class {@link fr.paris.lutece.plugins.workflowcore.business.action.Action} for Ticketing
 *
 */
public class TicketingAction extends Action
{
    private Action  _action;
    private boolean _bDisplayTasksForm;

    /**
     * Constructor
     * 
     * @param action
     *            the action
     * @param bIsDisplayTasksForm
     *            {@code true} if the task form must be displayed, {@code false} otherwise
     */
    public TicketingAction( Action action, boolean bIsDisplayTasksForm )
    {
        _action = action;
        _bDisplayTasksForm = bIsDisplayTasksForm;
    }

    /**
     * {@inheritDoc }
     */
    public int getId( )
    {
        return _action.getId( );
    }

    /**
     * {@inheritDoc }
     */
    public void setId( int idAction )
    {
        _action.setId( idAction );
    }

    /**
     * {@inheritDoc }
     */
    public String getName( )
    {
        return _action.getName( );
    }

    /**
     * {@inheritDoc }
     */
    public void setName( String strName )
    {
        _action.setName( strName );
    }

    /**
     * {@inheritDoc }
     */
    public String getDescription( )
    {
        return _action.getDescription( );
    }

    /**
     * {@inheritDoc }
     */
    public void setDescription( String strDescription )
    {
        _action.setDescription( strDescription );
    }

    /**
     * {@inheritDoc }
     */
    public Icon getIcon( )
    {
        return _action.getIcon( );
    }

    /**
     * {@inheritDoc }
     */
    public void setIcon( Icon icon )
    {
        _action.setIcon( icon );
    }

    /**
     * {@inheritDoc }
     */
    public State getStateBefore( )
    {
        return _action.getStateBefore( );
    }

    /**
     * {@inheritDoc }
     */
    public void setStateBefore( State stateBefore )
    {
        _action.setStateBefore( stateBefore );
    }

    /**
     * {@inheritDoc }
     */
    public State getStateAfter( )
    {
        return _action.getStateAfter( );
    }

    /**
     * {@inheritDoc }
     */
    public void setStateAfter( State stateAfter )
    {
        _action.setStateAfter( stateAfter );
    }

    /**
     * {@inheritDoc }
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc }
     */
    public String getResourceId( )
    {
        return Integer.toString( _action.getId( ) );
    }

    /**
     * {@inheritDoc }
     */
    public Workflow getWorkflow( )
    {
        return _action.getWorkflow( );
    }

    /**
     * {@inheritDoc }
     */
    public void setWorkflow( Workflow workflow )
    {
        _action.setWorkflow( workflow );
    }

    /**
     * {@inheritDoc }
     */
    public boolean isAutomaticState( )
    {
        return _action.isAutomaticState( );
    }

    /**
     * {@inheritDoc }
     */
    public void setAutomaticState( Boolean automaticState )
    {
        _action.setAutomaticState( automaticState );
    }

    /**
     * {@inheritDoc }
     */
    public void setMassAction( boolean bIsMassAction )
    {
        _action.setMassAction( bIsMassAction );
    }

    /**
     * {@inheritDoc }
     */
    public boolean isMassAction( )
    {
        return _action.isMassAction( );
    }

    /**
     * {@inheritDoc }
     */
    public void setListIdsLinkedAction( Collection<Integer> listIdsLinkedAction )
    {
        _action.setListIdsLinkedAction( listIdsLinkedAction );
    }

    /**
     * {@inheritDoc }
     */
    public Collection<Integer> getListIdsLinkedAction( )
    {
        return _action.getListIdsLinkedAction( );
    }

    /**
     * {@inheritDoc }
     */
    public int getOrder( )
    {
        return _action.getOrder( );
    }

    /**
     * {@inheritDoc }
     */
    public void setOrder( int nOrder )
    {
        _action.setOrder( nOrder );
    }

    /**
     * {@inheritDoc }
     */
    public boolean isAutomaticReflexiveAction( )
    {
        return _action.isAutomaticReflexiveAction( );
    }

    /**
     * {@inheritDoc }
     */
    public void setAutomaticReflexiveAction( boolean bAutomaticReflexiveAction )
    {
        _action.setAutomaticReflexiveAction( bAutomaticReflexiveAction );
    }

    /**
     * Checks if the task form must be displayed or not
     * 
     * @return {@code true} if the task form must be displayed, {@code false} otherwise
     */
    public boolean isDisplayTasksForm( )
    {
        return _bDisplayTasksForm;
    }
}
