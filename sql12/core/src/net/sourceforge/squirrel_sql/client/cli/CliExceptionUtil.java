package net.sourceforge.squirrel_sql.client.cli;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CliExceptionUtil
{

   public static UnsupportedOperationException createUnsupportedOperationException(Method method)
   {
      return new UnsupportedOperationException("NYI: " + method.getName() + "(" + Arrays.stream(method.getParameterTypes()).map(t -> t.getName()).collect(Collectors.joining(", "))  + ")");
   }
}
