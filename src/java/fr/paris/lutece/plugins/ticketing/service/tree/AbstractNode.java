/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.ticketing.service.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractNode
{
    protected List<AbstractNode> _childrenNodes;

    protected AbstractNode       _parentNode;

    protected List<AbstractNode> _leaves;

    protected AbstractDepth      _depth;

    private boolean              _bLeaf;

    /**
     * Constructor of the Abstract Node
     */
    public AbstractNode( )
    {
        _childrenNodes = new ArrayList<>( );
    }

    /**
     * Get the id of the node
     *
     * @return the id of the node
     */
    public abstract int getId( );

    /**
     * Get the parent id of the node
     *
     * @return the parent id of the node
     */
    public abstract int getIdParent( );

    /**
     * Get the boolean indicating the node is a leaf
     *
     * @return boolean is a leaf
     */
    public boolean getLeaf( )
    {
        return _bLeaf;
    }

    /**
     * Set the boolean indicating the node is a leaf
     *
     * @param bLeaf
     */
    public void setLeaf( boolean bLeaf )
    {
        _bLeaf = bLeaf;
    }

    /**
     * Get the Depth object of the node
     *
     * @return the Depth java object
     */
    public AbstractDepth getDepth( )
    {
        return _depth;
    }

    /**
     * Set the depth of the node
     *
     * @param depth
     *            the Depth
     */
    public void setDepth( AbstractDepth depth )
    {
        _depth = depth;
    }

    /**
     * Get the children list of a node
     *
     * @return list of a node
     */
    public List<? extends AbstractNode> getChildren( )
    {
        return _childrenNodes;
    }

    /**
     * Set the children list of a node
     *
     * @param children
     */
    public void setChildren( List<AbstractNode> children )
    {
        _childrenNodes = children;
    }

    /**
     * Get the Parent node of the node
     *
     * @return the Parent node
     */
    public AbstractNode getParent( )
    {
        return _parentNode;
    }

    /**
     * Set the parent node of the node
     *
     * @param parentNode
     *            the parent node
     */
    public void setParent( AbstractNode parentNode )
    {
        _parentNode = parentNode;
    }

    /**
     * Get the leaves of the node
     *
     * @return the leaves of the node
     */
    public List<? extends AbstractNode> getLeaves( )
    {
        return _leaves;
    }

    /**
     * Set the leaf of a node
     *
     * @param leaves
     *            the leaves of the node
     */
    public void setLeaves( List<AbstractNode> leaves )
    {
        _leaves = leaves;
    }

    /**
     * Get the list of nodes behind this node, from root to node.
     *
     * @param <Node>
     *            the Node type
     * @return the list of nodes behind a node
     */
    public <Node extends AbstractNode> List<Node> getBranch( )
    {
        List<Node> listNode = new ArrayList<>( );
        listNode.add( ( Node ) this );
        Node parent = ( Node ) getParent( );
        while ( parent != null )
        {
            listNode.add( parent );
            parent = ( Node ) parent.getParent( );
        }
        Collections.reverse( listNode );
        return listNode;
    }

    /**
     * Return if true if rootElement, false otherwise
     *
     * @return true if the node is a root node, false otherwise
     */
    public boolean isRootNode( )
    {
        return getParent( ) == null;
    }
}
