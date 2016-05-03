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
package fr.paris.lutece.plugins.ticketing.business.config;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


public class PluginConfigurationDAO implements IPluginConfigurationDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT ticketing_value FROM ticketing_configuration WHERE ticketing_key = ? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO ticketing_configuration ( ticketing_key, ticketing_value ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM ticketing_configuration WHERE ticketing_key = ? ";

    @Override
    public void store( String strKey, String strValue, Plugin plugin )
    {
        if ( strKey != null )
        {
            // First, deletes the values
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

            daoUtil.setString( 1, strKey );

            daoUtil.executeUpdate(  );
            daoUtil.free(  );

            // Secondly, inserts in database if not null
            if ( strValue != null )
            {
                daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

                daoUtil.setString( 1, strKey );
                daoUtil.setString( 2, strValue );

                daoUtil.executeUpdate(  );
                daoUtil.free(  );
            }
        }
    }

    @Override
    public String load( String strKey, Plugin plugin )
    {
        String strResult = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );

        daoUtil.setString( 1, strKey );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            strResult = daoUtil.getString( 1 );
        }

        return strResult;
    }
}
