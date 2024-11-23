package org.apache.logging.log4j;

public class LoggerImpl implements Logger
{
   @Override
   public LogBuilder atTrace()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder atDebug()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder atInfo()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder atWarn()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder atError()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder atFatal()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder always()
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }

   @Override
   public LogBuilder atLevel(Level level)
   {
      return new FixPoiLog4jDependencyLogBuilderMock();
   }
}
