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
package fr.paris.lutece.plugins.ticketing.business.parambouton;

import java.io.Serializable;

import fr.paris.lutece.plugins.ticketing.business.groupaction.GroupAction;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;

public class ParamBouton implements Serializable
{
    private static final long serialVersionUID = 4229191492315251652L;

    private int               _nIdparam;
    private int               _nIdAction;
    private int               _nIdGroupe;
    private int               _nOrdre;
    private String            _strIcone;
    private String            _strCouleur;
    private transient Action  _action;
    private GroupAction       _groupAction;

    public int getIdparam( )
    {
        return _nIdparam;
    }

    public void setIdparam( int nIdparam )
    {
        _nIdparam = nIdparam;
    }

    public int getIdAction( )
    {
        return _nIdAction;
    }

    public void setIdAction( int nIdAction )
    {
        _nIdAction = nIdAction;
    }

    public int getIdGroupe( )
    {
        return _nIdGroupe;
    }

    public void setIdGroupe( int idGroupe )
    {
        _nIdGroupe = idGroupe;
    }

    public int getOrdre( )
    {
        return _nOrdre;
    }

    public void setOrdre( int nOrdre )
    {
        _nOrdre = nOrdre;
    }

    public String getIcone( )
    {
        return _strIcone;
    }

    public void setIcone( String strIcone )
    {
        _strIcone = strIcone;
    }

    public String getCouleur( )
    {
        return _strCouleur;
    }

    public void setCouleur( String strCouleur )
    {
        _strCouleur = strCouleur;
    }

    public Action getAction( )
    {
        return _action;
    }

    public GroupAction getGroupAction( )
    {
        return _groupAction;
    }

    public void setAction( Action action )
    {
        _action = action;
    }

    public void setGroupAction( GroupAction groupAction )
    {
        _groupAction = groupAction;
    }

}
