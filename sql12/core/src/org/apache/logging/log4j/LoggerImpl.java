package org.apache.logging.log4j;

public class LoggerImpl implements Logger
{
   private FixPoiLog4jDependencyLogBuilderMock _traceMock = new FixPoiLog4jDependencyLogBuilderMock(Level.TRACE);
   private FixPoiLog4jDependencyLogBuilderMock _debugMock = new FixPoiLog4jDependencyLogBuilderMock(Level.DEBUG);
   private FixPoiLog4jDependencyLogBuilderMock _infoMock = new FixPoiLog4jDependencyLogBuilderMock(Level.INFO);
   private FixPoiLog4jDependencyLogBuilderMock _warnMock = new FixPoiLog4jDependencyLogBuilderMock(Level.WARN);
   private FixPoiLog4jDependencyLogBuilderMock _errorMock = new FixPoiLog4jDependencyLogBuilderMock(Level.ERROR);
   private FixPoiLog4jDependencyLogBuilderMock _fatalMock = new FixPoiLog4jDependencyLogBuilderMock(Level.FATAL);
   private FixPoiLog4jDependencyLogBuilderMock _allMock = new FixPoiLog4jDependencyLogBuilderMock(Level.ALL);
   private FixPoiLog4jDependencyLogBuilderMock _offMock = new FixPoiLog4jDependencyLogBuilderMock(Level.OFF);

   @Override
   public LogBuilder atTrace()
   {
      return _traceMock;
   }

   @Override
   public LogBuilder atDebug()
   {
      return _debugMock;
   }

   @Override
   public LogBuilder atInfo()
   {
      return _infoMock;
   }

   @Override
   public LogBuilder atWarn()
   {
      return _warnMock;
   }

   @Override
   public LogBuilder atError()
   {
      return _errorMock;
   }

   @Override
   public LogBuilder atFatal()
   {
      return _fatalMock;
   }

   @Override
   public LogBuilder always()
   {
      return _allMock;
   }

   @Override
   public LogBuilder atLevel(Level level)
   {
      if(Level.OFF == level) return _offMock;
      if(Level.FATAL == level) return _fatalMock;
      if(Level.ERROR == level) return _errorMock;
      if(Level.WARN == level) return _warnMock;
      if(Level.INFO == level) return _infoMock;
      if(Level.DEBUG == level) return _debugMock;
      if(Level.TRACE == level) return _traceMock;
      if(Level.ALL == level) return _allMock;

      return _infoMock;
   }
}
