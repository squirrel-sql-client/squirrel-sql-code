This test directory contains all of the code and scripts that are required to 
test SQuirreL.  All JUnit tests (internal and external) are stored here in the 
same package structure of the classes that they test.  The directories found 
here are:

 * adhoc - test drivers that are used to connect to a specific database for 
           performing some integration test.
           
 * docs - documents that describe the testing process and testing results

 * external - integration tests that require some external (non-mocked) resource
              such as the filesystem or a database.
            
 * jfctests - integration tests that run a particular GUI component for the 
              purpose of testing that component in isolation from SQuirreL.  
              This speeds the iterative process of customizing view components.

 * lib - libraries that are required to execute the code here.(e.g. JUnit)
 
 * mockobjects - some early attempts to provide fixtures for various key objects
                 used in SQuirreL by some JUnit tests.  EasyMock has been 
                 chosen to supercede the use of these mocks, and a class called
                 TestUtilities provides EasyMock factory methods for many of 
                 the classes held here.  These classes may still be used by some
                 integration tests.
                 
 * scripts - various scripts that have been used to perform adhoc or integration
             tests against particular databases.
             
 * src - All JUnit tests that are unit tests (completely self-contained tests 
         that do not require external resources like filesystems or databases.