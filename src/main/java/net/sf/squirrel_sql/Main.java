package net.sf.squirrel_sql;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String[] appCtx = new String[] {
			"classpath:dao.xml",
			"classpath:service.xml",
		};
				
		ApplicationContext ctx = new ClassPathXmlApplicationContext(appCtx);
		MavenMultiTreeDependencyAnalyzer analyzer = (MavenMultiTreeDependencyAnalyzer)
			ctx.getBean("net.sf.squirrel_sql.MavenMultiTreeDependencyAnalyzer");
		
		final List<String> pathList = new ArrayList<String>(args.length);
		for (int i = 0; i < args.length; i++) {
			pathList.add(args[i]);
		}
	
		analyzer.setSourceTreePaths(pathList);
		try
		{
			analyzer.analyzePaths();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
