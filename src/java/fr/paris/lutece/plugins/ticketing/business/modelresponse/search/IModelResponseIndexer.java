/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.paris.lutece.plugins.ticketing.business.modelresponse.search;

import fr.paris.lutece.plugins.ticketing.business.modelresponse.ModelResponse;

import java.io.IOException;

import java.util.List;


/**
 *
 * @author root
 */
interface IModelResponseIndexer
{
    final String PATH_INDEX = "/WEB-INF/plugins/ticketing/model-responses/indexes";
    final String FIELD_MODEL_RESPONSE_INFOS = "model_responses";
    final String FIELD_ID = "id";
    final String FIELD_TITLE = "title";
    final String FIELD_RESPONSE = "response";
    final String FIELD_KEYWORD = "keyword";
    final String FIELD_SEARCH_CONTENT = "content";

    void update( ModelResponse modelReponse ) throws IOException;

    void delete( ModelResponse modelReponse ) throws IOException;

    void add( ModelResponse modelReponse ) throws IOException;

    String addAll(  );

    List<ModelResponse> searchResponses( String strQuery, String strDomain );
}
