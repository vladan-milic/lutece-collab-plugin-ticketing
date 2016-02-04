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
package fr.paris.lutece.plugins.ticketing.web;


/**
 * Class providing constants for Ticketing
 *
 */
public final class TicketingConstants
{
    // Templates
    public static final String TEMPLATE_TASKS_FORM_WORKFLOW = "admin/plugins/ticketing/workflow/tasks_form_workflow.html";

    // Parameters
    public static final String PARAMETER_ID_TICKET = "id";
    public static final String PARAMETER_WORKFLOW_ID_ACTION = "id_action";
    public static final String PARAMETER_BACK = "back";

    // Attributes
    public static final String ATTRIBUTE_HIDE_NEXT_STEP_BUTTON = "hide_next_button";

    // Markers
    public static final String MARK_ID_TICKET = PARAMETER_ID_TICKET;
    public static final String MARK_WORKFLOW_ID_ACTION = PARAMETER_WORKFLOW_ID_ACTION;
    public static final String MARK_TASKS_FORM = "tasks_form";
    public static final String MARK_HIDE_NEXT_STEP_BUTTON = ATTRIBUTE_HIDE_NEXT_STEP_BUTTON;

    // Properties
    public static final String PROPERTY_PAGE_TITLE_TASKS_FORM_WORKFLOW = "ticketing.taskFormWorkflow.pageTitle";
    public static final String PROPERTY_WORKFLOW_ACTION_ID_ASSIGN_ME = "ticketing.workflow.action.id.assignMe";

    /**
     * Default constructor
     */
    private TicketingConstants(  )
    {
    }
}
