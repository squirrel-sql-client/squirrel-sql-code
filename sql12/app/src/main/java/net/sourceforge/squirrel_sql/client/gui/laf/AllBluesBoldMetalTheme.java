package net.sourceforge.squirrel_sql.client.gui.laf;
/*
 * Copyright (c) 2002 Karsten Lentzsch. All Rights Reserved.
 *
 * This software is the proprietary information of Karsten Lentzsch.  
 * Use is subject to license terms.
 *
 * From an email from Karsten Lentzsch "I have attached a class for my theme
 * "All Blues - Bold" and grant you a license to use it in Squirrel."
 */
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AllBluesBoldMetalTheme extends DefaultMetalTheme
{
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(AllBluesBoldMetalTheme.class);
    
        // light blue colors
        private final ColorUIResource primary1  = new ColorUIResource( 44,  73, 135);
        private final ColorUIResource primary2  = new ColorUIResource( 85, 115, 170); // 58, 110, 165);
        private final ColorUIResource primary3  = new ColorUIResource(172, 210, 248); //189, 220, 251); //153, 179, 205); 

        private final ColorUIResource secondary1 = new ColorUIResource(110, 110, 110);
        private final ColorUIResource secondary2 = new ColorUIResource(170, 170, 170);
        private final ColorUIResource secondary3 = new ColorUIResource(220, 220, 220); 
        
        
        public String getName() {
            // i18n[AllBluesBoldMetalTheme.name=All Blues - Bold]       
            return s_stringMgr.getString("AllBluesBoldMetalTheme.name"); 
        }
        
        public  ColorUIResource getMenuItemSelectedBackground() { return getPrimary2(); }
        public  ColorUIResource getMenuItemSelectedForeground() { return getWhite();            }
        public  ColorUIResource getMenuSelectedBackground()     { return getSecondary2();       }
        
        protected ColorUIResource getPrimary1()                                 { return primary1; }
        protected ColorUIResource getPrimary2()                                 { return primary2; }
        protected ColorUIResource getPrimary3()                                 { return primary3; }
        protected ColorUIResource getSecondary1()                                       { return secondary1; }
        protected ColorUIResource getSecondary2()                                       { return secondary2; }
        protected ColorUIResource getSecondary3()                                       { return secondary3; }

}
