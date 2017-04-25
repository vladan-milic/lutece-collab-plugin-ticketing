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
package fr.paris.lutece.plugins.ticketing.business.modelresponse.search;

import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author root
 */
public interface IModelResponseIndexer
{
    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "ticketing.modelResponsesServices";

    /**
     * update index with modelResponse
     * 
     * @param modelReponse
     *            modelResponse
     * @throws IOException
     *             ioexception
     */
    void update( ModelResponse modelReponse ) throws IOException;

    /**
     * delete index with modelResponse
     * 
     * @param modelReponse
     *            modelResponse
     * @throws IOException
     *             ioexception
     */
    void delete( ModelResponse modelReponse ) throws IOException;

    /**
     * add modelResponse to index
     * 
     * @param modelReponse
     *            modelResponse
     * @throws IOException
     *             ioexception
     */
    void add( ModelResponse modelReponse ) throws IOException;

    /**
     * add all modelResponse to index
     * 
     * @return indexation log string
     */
    String addAll( );

    /**
     * search modelResponse matching query
     * 
     * @param strQuery
     *            lucene query
     * @param setIdDomain
     *            set of domain id
     * @return list of modelResponse matching query
     */
    List<ModelResponse> searchResponses( String strQuery, Set<String> setIdDomain );
}
