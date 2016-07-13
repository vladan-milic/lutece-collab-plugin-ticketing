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
package fr.paris.lutece.plugins.ticketing.web.search;


/***
 * Constants for Ticket searching
 * @author s267533
 *
 */
public final class SearchConstants
{
    // Parameters
    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_PAGE_INDEX = "page_index";
    public static final String PARAMETER_SEARCH_FIELD = "searched_field";
    public static final String PARAMETER_CATEGORY = "category";
    public static final String PARAMETER_DOMAIN = "domain";

    // Markers
    public static final String MARK_QUERY = "query";
    public static final String MARK_RESULT = "result";
    public static final String MARK_PAGINATOR = "paginator";
    public static final String MARK_SEARCH_FIELDS_LIST = "search_fields_list";
    public static final String MARK_SEARCH_FIELD = "searched_field";
    public static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    public static final String MARK_ERRORS = "errors";

    // Properties
    public static final String PROPERTY_PAGE_TITLE_TICKET_SEARCH = "ticketing.search_ticket.pageTitle";
    public static final String PROPERTY_PAGE_TITLE_RESPONSE_RESULTS = "ticketing.search_response_results.pageTitle";
    public static final String PROPERTY_DEFAULT_RESULT_PER_PAGE = "ticketing.search_ticket.labelItemsPerPag";
    public static final String MESSAGE_SEARCH_NO_INPUT = "ticketing.search_ticket.noInput";
    public static final String MESSAGE_SEARCH_ERROR = "ticketing.search_ticket.error";
    public static final String PROPERTY_MODEL_RESPONSE_LIMIT_PER_QUERY = "ticketing.modelResponsesIndexerDaemon.limitResponse";

    // for search purpose
    public static final String BEAN_SEARCH_ENGINE = "ticketing.ticketSearchEngine";

    /**
     * constructor
     */
    private SearchConstants(  )
    {
    }
}
