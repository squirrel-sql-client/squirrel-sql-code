package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

public class SplashScreenTestUI {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ApplicationArguments.initialize(new String[] {});
        SquirrelResources _resources = 
            new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
        SquirrelPreferences _prefs = SquirrelPreferences.load();
        

        new SplashScreen(_resources, 15, _prefs);


    }

}
