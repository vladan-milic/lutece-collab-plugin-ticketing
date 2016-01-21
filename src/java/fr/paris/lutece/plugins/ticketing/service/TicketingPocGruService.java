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
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * This class provides utility methods for the POC GRU
 *
 */
public final class TicketingPocGruService
{
    // Properties
    private static final String PROPERTY_POC_GRU_COMPANIES = "ticketing.pocgru.companies";
    private static final String PROPERTY_POC_GRU_USERS_COMPANY = "ticketing.pocgru.users.company.";
    private static final String PROPERTY_POC_GRU_WORKFLOW_ID = "ticketing.pocgru.workflowId";

    // Other constants
    private static final String SEPARATOR = ",";

    /**
     * Default constructor
     */
    private TicketingPocGruService(  )
    {
    }

    /**
     * Retrieves the workflow id associated to the specified ticket
     *
     * @param ticket
     *            the ticket
     * @return the workflow id
     */
    public static int getWorkflowId( Ticket ticket )
    {
        int nWorkflowId = -1;

        String strCompany = getCompany( ticket.getGuid(  ) );

        if ( strCompany != null )
        {
            nWorkflowId = Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_POC_GRU_WORKFLOW_ID ) );
        }
        else
        {
            TicketCategory ticketCategory = TicketCategoryHome.findByPrimaryKey( ticket.getIdTicketCategory(  ) );

            nWorkflowId = ticketCategory.getIdWorkflow(  );
        }

        return nWorkflowId;
    }

    /**
     * Retrieves the company depending on the user GUID
     *
     * @param strGuid
     *            the user GUID
     * @return the company if it exists, {@code null} otherwise
     */
    public static String getCompany( String strGuid )
    {
        String strCompany = null;

        if ( strGuid != null )
        {
            String[] companies = AppPropertiesService.getProperty( PROPERTY_POC_GRU_COMPANIES ).split( SEPARATOR );

            for ( String strCompanyFromProperties : companies )
            {
                String[] users = AppPropertiesService.getProperty( PROPERTY_POC_GRU_USERS_COMPANY +
                        strCompanyFromProperties ).split( SEPARATOR );

                for ( String user : users )
                {
                    if ( strGuid.equals( user ) )
                    {
                        strCompany = strCompanyFromProperties;

                        break;
                    }
                }

                if ( strCompany != null )
                {
                    break;
                }
            }
        }

        return strCompany;
    }
}
