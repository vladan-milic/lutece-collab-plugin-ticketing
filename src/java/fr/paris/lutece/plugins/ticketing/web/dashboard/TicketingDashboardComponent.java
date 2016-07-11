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
package fr.paris.lutece.plugins.ticketing.web.dashboard;

import fr.paris.lutece.plugins.ticketing.business.ticket.Ticket;
import fr.paris.lutece.plugins.ticketing.business.ticket.TicketFilter;
import fr.paris.lutece.plugins.ticketing.web.ticketfilter.TicketFilterHelper;
import fr.paris.lutece.plugins.ticketing.web.util.TicketUtils;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Ticketing Dashboard Component
 * This component displays summary tickets associated to the connected user
 */
public class TicketingDashboardComponent extends DashboardComponent
{
    // MARKS
    private static final String MARK_URL = "url";
    private static final String MARK_ICON = "icon";
    private static final String MARK_TICKET_ASSIGNED_TO_ME_COUNTER = "ticket_assigned_to_me_counter";
    private static final String MARK_TICKET_ASSIGNED_TO_MY_GROUP_COUNTER = "ticket_assigned_to_my_group_counter";
    private static final String MARK_TICKET_ASSIGNED_TO_MY_DOMAIN_COUNTER = "ticket_assigned_to_my_domain_counter";
    private static final String MARK_WORFLOWSERVICE_UNAVAILABLE = "workflow_service_unavailable";

    // PARAMETERS
    private static final String PARAMETER_PLUGIN_NAME = "plugin_name";

    // TEMPLATES
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/ticketing/ticketing_dashboard.html";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Right right = RightHome.findByPrimaryKey( getRight(  ) );
        Plugin plugin = PluginService.getPlugin( right.getPluginName(  ) );

        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( PARAMETER_PLUGIN_NAME, right.getPluginName(  ) );
        
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_ICON, plugin.getIconUrl(  ) );
        
        if ( WorkflowService.getInstance(  ).isAvailable(  ) ){
            TicketFilter filter = TicketFilterHelper.getFilter( request, user );

            List<Ticket> listAgentTickets = new ArrayList<Ticket>(  );
            List<Ticket> listGroupTickets = new ArrayList<Ticket>(  );
            List<Ticket> listDomainTickets = new ArrayList<Ticket>(  );

            TicketUtils.setTicketsListByPerimeter( user, filter, request, listAgentTickets, listGroupTickets,
            listDomainTickets );

            
            model.put( MARK_TICKET_ASSIGNED_TO_ME_COUNTER, listAgentTickets.size(  ) );
            model.put( MARK_TICKET_ASSIGNED_TO_MY_GROUP_COUNTER, listGroupTickets.size(  ) );
            model.put( MARK_TICKET_ASSIGNED_TO_MY_DOMAIN_COUNTER, listDomainTickets.size(  ) );
            model.put( MARK_TICKET_ASSIGNED_TO_MY_DOMAIN_COUNTER, listDomainTickets.size(  ) );
            model.put( MARK_WORFLOWSERVICE_UNAVAILABLE, false );
        }
        else{
            model.put( MARK_WORFLOWSERVICE_UNAVAILABLE, true );
        }
        
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_DASHBOARD, user.getLocale(  ), model );
        return t.getHtml(  );
    }
}



