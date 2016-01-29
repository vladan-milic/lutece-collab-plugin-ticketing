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
package fr.paris.lutece.plugins.ticketing.service;

import fr.paris.lutece.plugins.ticketing.business.Ticket;
import fr.paris.lutece.plugins.ticketing.business.TicketCategory;
import fr.paris.lutece.plugins.ticketing.business.TicketCategoryHome;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.workflow.WorkflowService;


/**
 * TicketingUtils
 */
public final class TicketingUtils
{
    /**
     * Default constructor
     */
    private TicketingUtils(  )
    {
    }

    /**
     * set workflow attributes for the specified ticket
     * @param ticket the Ticket
     * @param user the user
     */
    public static void setWorkflowAttributes( Ticket ticket, AdminUser user )
    {
        if ( WorkflowService.getInstance(  ).isAvailable(  ) )
        {
            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );
            int nIdWorkflow = ticketCategory.getIdWorkflow(  );

            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( nIdWorkflow );

            State state = WorkflowService.getInstance(  )
                                         .getState( ticket.getId(  ), Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow,
                    ticketCategory.getId(  ) );

            if ( state != null )
            {
                ticket.setState( state );
            }

            if ( nIdWorkflow > 0 )
            {
                ticket.setListWorkflowActions( WorkflowService.getInstance(  )
                                                              .getActions( ticket.getId(  ),
                        Ticket.TICKET_RESOURCE_TYPE, nIdWorkflow, user ) );
            }
        }
    }
}
