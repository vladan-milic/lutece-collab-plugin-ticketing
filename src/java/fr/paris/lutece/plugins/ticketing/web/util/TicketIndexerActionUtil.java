/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.web.util;

import fr.paris.lutece.plugins.ticketing.business.search.IndexerAction;
import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * Util class For IndexerAction for Ticket
 */
public class TicketIndexerActionUtil
{
    // CONSTANTS
    private static final String PROPERTY_WORKFLOW_ACTION_ID_NEW = "ticketing.workflow.action.id.new";

    /**
     * Create a IndexerAction from the given Ticket object
     * 
     * @param ticket
     * @return the IndexerAction from the Ticket
     */
    public static IndexerAction createIndexerActionFromTicket( Ticket ticket )
    {
        IndexerAction indexerAction = null;
        if ( ticket != null )
        {
            State state = WorkflowService.getInstance( ).getState( ticket.getId( ), Ticket.TICKET_RESOURCE_TYPE, ticket.getTicketCategory( ).getIdWorkflow( ), null );

            indexerAction = new IndexerAction( );
            indexerAction.setIdTicket( ticket.getId( ) );

            if ( state == null || state.getId( ) == AppPropertiesService.getPropertyInt( PROPERTY_WORKFLOW_ACTION_ID_NEW, 301 ) )
            {
                indexerAction.setIdTask( IndexerAction.TASK_CREATE );
            }
            else
            {
                indexerAction.setIdTask( IndexerAction.TASK_MODIFY );
            }
        }

        return indexerAction;
    }
}
