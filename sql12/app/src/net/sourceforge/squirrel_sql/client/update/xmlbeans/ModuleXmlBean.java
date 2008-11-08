/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A module is a group of Artifacts that work together to provide of unit of 
 * functionality.  For instance, each plugin is a module; the core jars 
 * squirrel-sql.jar and fw.jar are together a module. 
 * 
 * @author manningr
 *
 */
public class ModuleXmlBean implements Serializable {

    private static final long serialVersionUID = -6047289718869161323L;

    private String name;
    
    private Set<ArtifactXmlBean> artifacts = new HashSet<ArtifactXmlBean>();
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the artifacts
     */
    public Set<ArtifactXmlBean> getArtifacts() {
        return artifacts;
    }

    /**
     * @param artifacts the artifacts to set
     */
    public void setArtifacts(Set<ArtifactXmlBean> artifacts) {
        this.artifacts = artifacts;
    }
    
    /**
     * Adds an artifact to the set.
     * 
     * @param artifact the artifact to add.
     */
    public void addArtifact(ArtifactXmlBean artifact) {
        this.artifacts.add(artifact);
    }
    
    
}
