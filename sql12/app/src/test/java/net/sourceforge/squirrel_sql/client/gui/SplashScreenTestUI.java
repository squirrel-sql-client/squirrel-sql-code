package net.sourceforge.squirrel_sql.client.gui;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

public class SplashScreenTestUI {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ApplicationArguments.initialize(new String[] {});
        SquirrelPreferences _prefs = SquirrelPreferences.load();
        SquirrelSplashScreen screen = new SquirrelSplashScreen(_prefs, 16);
        screen.indicateNewTask("task 1");
        Thread.sleep(2000);
        screen.indicateNewTask("task 2");
        Thread.sleep(2000);
        screen.indicateNewTask("task 3");
        Thread.sleep(2000);
        screen.indicateNewTask("task 4");
        Thread.sleep(2000);   
        screen.indicateNewTask("task 5");
        Thread.sleep(2000);   
        
        while (true) {
      	  Thread.sleep(2000);
        }
    }

}
