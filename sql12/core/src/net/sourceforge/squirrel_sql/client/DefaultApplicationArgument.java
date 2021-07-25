/*
 * Copyright (C) 2014 David Greene
 * david@trumpetx.com
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
package net.sourceforge.squirrel_sql.client;

import java.util.Collection;
import java.util.Collections;

/**
 * Introduced in Feb. 2014.
 * Currently used by Plugins not hosted with SQuirreL only.
 * It's not clear if such Plugins really exist.
 * Implementors must override {@link net.sourceforge.squirrel_sql.client.plugin.IPlugin#getPluginApplicationArguments()}.
 */
public class DefaultApplicationArgument implements IApplicationArgument {

    private String argumentName;
    private String longArgumentName = null;
    private String description;
    private boolean required = false;
    private boolean set = false;
    private Collection<String> argumentValues;
    private int numberOfArgumentValues = 1;

    public DefaultApplicationArgument(String argumentName, String description){
        this.argumentName = argumentName;
        this.description = description;
    }
    
    public DefaultApplicationArgument(String argumentName, String description, boolean required){
        this(argumentName, description);
        this.required = required;
    }    
    
    @Override
    public String getArgumentName() {
        return argumentName;
    }

    public void setArgumentName(String argumentName) {
        this.argumentName = argumentName;
    }

    @Override
    public String getLongArgumentName() {
        return longArgumentName;
    }

    public void setLongArgumentName(String longArgumentName) {
        this.longArgumentName = longArgumentName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    @Override
    public Collection<String> getArgumentValues() {
        return argumentValues == null ? Collections.EMPTY_LIST : argumentValues;
    }

    @Override
    public void setArgumentValues(Collection<String> argumentValues) {
        this.set = true;
        this.argumentValues = argumentValues;
    }

    @Override
    public int getNumberOfArgumentValues() {
        return numberOfArgumentValues;
    }

    public void setNumberOfArgumentValues(int numberOfArgumentValues) {
        this.numberOfArgumentValues = numberOfArgumentValues;
    }

    @Override
    public String getValue() {
        return (isSet() && !getArgumentValues().isEmpty()) 
                ? getArgumentValues().iterator().next() 
                : null;
    }

}
