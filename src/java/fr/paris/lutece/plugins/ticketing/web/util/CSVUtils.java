/*
 *
 *  *
 *  *  * Copyright (c) 2002-2017, Mairie de Paris
 *  *  * All rights reserved.
 *  *  *
 *  *  * Redistribution and use in source and binary forms, with or without
 *  *  * modification, are permitted provided that the following conditions
 *  *  * are met:
 *  *  *
 *  *  *  1. Redistributions of source code must retain the above copyright notice
 *  *  *     and the following disclaimer.
 *  *  *
 *  *  *  2. Redistributions in binary form must reproduce the above copyright notice
 *  *  *     and the following disclaimer in the documentation and/or other materials
 *  *  *     provided with the distribution.
 *  *  *
 *  *  *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *  *  *     contributors may be used to endorse or promote products derived from
 *  *  *     this software without specific prior written permission.
 *  *  *
 *  *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  *  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  *  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  *  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 *  *  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  *  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  *  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  *  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  *  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  *  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  *  * POSSIBILITY OF SUCH DAMAGE.
 *  *  *
 *  *  * License 1.0
 *  *
 *
 */

package fr.paris.lutece.plugins.ticketing.web.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtils
{

    private static final char DEFAULT_SEPARATOR = ';';
    private static final char QUOTE = '\"';

    /**
     * Private Constructor
     */
    private CSVUtils( )
    {

    }

    /**
     * Transcode les doubles quotes.
     *
     * @param value
     *            The value
     * @return The value with double quote
     */
    private static String followCSVformat( String value )
    {
        String result = value;
        if ( ( result != null ) && result.contains( "\"" ) )
        {
            result = result.replace( "\"", "\"\"" );
        }
        return result;

    }

    /**
     * Permet d'ecrire une ligne dans un writer avec des valeurs separees par un separateur
     *
     * @param w
     *            The writer
     * @param values
     *            The list of values
     * @throws IOException
     *             IOException
     */
    public static void writeLine( Writer w, List<String> values ) throws IOException
    {
        boolean first = true;

        StringBuilder sb = new StringBuilder( );
        for ( String value : values )
        {
            if ( !first )
            {
                sb.append( DEFAULT_SEPARATOR );
            }

            sb.append( QUOTE ).append( followCSVformat( value ) ).append( QUOTE );

            first = false;
        }
        sb.append( "\n" );
        w.append( sb.toString( ) );
    }

}
