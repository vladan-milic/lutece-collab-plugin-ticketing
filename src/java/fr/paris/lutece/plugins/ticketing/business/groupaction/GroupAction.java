/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.business.groupaction;

import java.io.Serializable;

public class GroupAction implements Serializable
{

    private static final long serialVersionUID = 5773266512794634020L;

    private int               _nIdGroup;
    private String            _strLibelleIdentifiant;
    private String            _strCle;
    private String            _strDescription;
    private int               _nOrdre;

    public int getOrdre( )
    {
        return _nOrdre;
    }

    public void setOrdre( int nOrdre )
    {
        _nOrdre = nOrdre;
    }

    public int getIdGroup( )
    {
        return _nIdGroup;
    }

    public void setIdGroup( int idGroup )
    {
        _nIdGroup = idGroup;
    }

    public String getLibelleIdentifiant( )
    {
        return _strLibelleIdentifiant;
    }

    public void setLibelleIdentifiant( String libelleIdentifiant )
    {
        _strLibelleIdentifiant = libelleIdentifiant;
    }

    public String getCle( )
    {
        return _strCle;
    }

    public void setCle( String cle )
    {
        _strCle = cle;
    }

    public String getDescription( )
    {
        return _strDescription;
    }

    public void setDescription( String description )
    {
        _strDescription = description;
    }

}
