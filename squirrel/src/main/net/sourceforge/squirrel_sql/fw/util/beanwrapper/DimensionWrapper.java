package net.sourceforge.squirrel_sql.fw.util.beanwrapper;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Dimension;

/**
 * This class is a wrapper around a <TT>java.awt.Dimension</TT> that turns
 * it into a JavaBean.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DimensionWrapper {
    /** Property names for this bean. */
    public interface IPropertyNames {
        /** Width of <TT>Dimension</TT>. */
        String WIDTH = "width";

        /** Height of <TT>Dimension</TT>. */
        String HEIGHT = "height";
    }

    /** Width of <TT>Dimension</TT>. */
    private int _width;

    /** Height of <TT>Dimension</TT>. */
    private int _height;

    /**
     * Default ctor.
     */
    public DimensionWrapper() {
        this(null);
    }

    /**
     * Ctor specifying the <TT>Dimension</TT> that this object will be
     * wrapped around.
     *
     * @param   dm  The <TT>Dimension</TT> that this object will be
     *              wrapped around.
     */
    public DimensionWrapper(Dimension dm) {
        super();
        setFrom(dm);
    }

    public int getWidth() {
        return _width;
    }

    public void setWidth(int value) {
        _width = value;
    }

    public int getHeight() {
        return _height;
    }

    public void setHeight(int value) {
        _height = value;
    }

    public Dimension createDimension() {
        return new Dimension(_width, _height);
    }

    public void setFrom(Dimension dm) {
        if (dm != null) {
            _width = (int)dm.getWidth();
            _height = (int)dm.getHeight();
        }
    }
}