/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.business.file;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.portal.service.plugin.Plugin;

/**
 * ITicketFileDAO Interface
 */
public interface ITicketFileDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param nIdFile
     *            id file
     * @param strIdBlob
     *            id blob
     *
     * @param plugin
     *            the Plugin
     */
    void insert( int nIdFile, String strIdBlob, Plugin plugin );

    /**
     * Find Blob id By Id File
     *
     * @param idFile
     *            id file from core_file
     * @param plugin
     *            the Plugin
     * @return blob id
     */
    String findIdBlobByIdFile( int idFile, Plugin plugin );

    /**
     * delete relation between file and blob
     *
     * @param idFile
     *            id file from core_file
     * @param plugin
     *            the Plugin
     */
    void delete( int idFile, Plugin plugin );

    /**
     * find creation date
     *
     * @param nIdFile
     *            id file from core_file
     * @param plugin
     *            the Plugin
     * @return migration date
     */
    Timestamp findCreationDateByIdFile( int nIdFile, Plugin plugin );

    /**
     * find blobs from start date
     *
     * @param date
     *            start date
     * @param _plugin
     *            the Plugin
     * @return map id_blob, id_file
     */
    Map<String, Integer> findListIdBlobByDate( Date date, Plugin _plugin );

    /**
     * find id file list
     *
     * @param _plugin
     *            the Plugin
     *
     * @return list id
     */
    List<Integer> findListIdFile( Plugin _plugin );

}
