#! /usr/bin/perl -w

#
# This script is intended to automate the conversion of SQuirreL SQL Client from using Ant for builds to Maven 2.
# It is anticipated that it will take some months to fully switch the build which currently produces multiple platform
# installers, pushes updated artifacts to an update site, and uploads installers and source archives to SourceForge.
# Rather than do the conversion by hand and risk inconsistencies in the many pom files, I decided that a script to
# automate would be more desireable.  When the converted projects can completely replicate the automated release and
# distribution process that is currently done by Ant, this script will be run one final time against a new project
# repository hosted in subversion, to make it easier to relocate files without losing their history.  Then, since
# the script will no longer be needed, it will be removed and no longer used.
#
# RMM 20090808
#
#

use File::Find;
use Text::Template;

################################################################################################
# Global Declarations
################################################################################################

$mavenizeDir = `pwd`;
$mavenizeDir =~ s/\s//g;

$topDir       = shift;
if (! defined $topDir) {
	exit("Must specify top-level dir (absolute path to sql12)");
}
$fwDir        = $topDir . "/fw";
$appDir       = $topDir . "/app";
$pluginsDir   = $topDir . "/plugins";
$lafPluginDir = $pluginsDir . "/laf";
$installerDir = $topDir . "/installer";
$docDir       = $topDir . "/doc";
$websiteDir   = $topDir . "/web-site";
$testDir      = $topDir . "/test";

$onlyCopyPoms = 0;

$cache_deps = <<"EOF";
<dependencies>
      <dependency>
         <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
         <artifactId>CacheDB</artifactId>
         <version>2008.2.0.526.0</version>
      </dependency>
   </dependencies>
EOF
$firebirdmanager_deps = <<"EOF";
<dependencies>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>jaybird</artifactId>
        <version>2.1.1</version>
    </dependency>
   </dependencies>
EOF
$oracle_deps = <<"EOF";
<dependencies>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>treetable</artifactId>
        <version>20040121</version>
    </dependency>
   </dependencies>
EOF
$sqlval_deps = <<"EOF";
<dependencies>
      <dependency>
         <groupId>axis</groupId>
         <artifactId>axis</artifactId>
         <version>1.2</version>
      </dependency>
      <dependency>
         <groupId>axis</groupId>
         <artifactId>axis-jaxrpc</artifactId>
         <version>1.2</version>
      </dependency>
      <dependency>
         <groupId>axis</groupId>
         <artifactId>axis-saaj</artifactId>
         <version>1.2</version>
      </dependency>
      <dependency>
         <groupId>axis</groupId>
         <artifactId>axis-wsdl4j</artifactId>
         <version>1.2</version>
      </dependency>
   </dependencies>
EOF
$syntax_deps = <<"EOF";
<dependencies>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>com-fifesoft-common</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>com-fifesoft-rtext</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>com-fifesoft-autocomplete</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>com-fifesoft-rsyntaxtextarea</artifactId>
        <version>1.0.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>openide</artifactId>
        <version>4.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>org-netbeans-modules-editor</artifactId>
        <version>4.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>org-netbeans-modules-editor-fold</artifactId>
        <version>4.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>org-netbeans-modules-editor-lib</artifactId>
        <version>4.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>org-netbeans-modules-editor-util</artifactId>
        <version>4.0</version>
    </dependency>
    <dependency>
        <groupId>net.sf.squirrel-sql.thirdparty-non-maven</groupId>
        <artifactId>ostermiller-syntax</artifactId>
        <version>1.1.1</version>
    </dependency>
   </dependencies>
EOF

#################################################################################################
# Begin working
#################################################################################################

# copy in the root pom - this pom builds all of SQuirreL
copyRootPom();

# copy in test dependencies pom
copyTestUtilsProject();

# copy in plugins support projects
copyPluginsSupportProjects();

# copy in the installer projects
copyInstallerProjects();

# copy in the translations project
copyTranslationProjects();

# copy in the update site projects
copyUpdateSiteProjects();

# restructure fw module
restructureFwModule();

# restructure app module
restructureAppModule();

# Restructure the doc module
restructureDocModule();

# Restructure the web-site module
restructureWebsiteModule();

# As a side effect, this will generate an array (@artifacts) containing the names of the directories
# containing each of the plugins.
generatePluginPoms();

# This uses the @artifacts array to build a modules pom which lists all of the plugins
generatePluginModulesPomFile();

# Recurse through the plugin directories to find "plugin_build.xml" - this time to copy source code under /src/main/java
find( { wanted => \&wanted_for_source, no_chdir => 0 }, $pluginsDir );

# Recurse through all source directories to find java files to build package map so that test files can be relocated.
find( { wanted => \&wanted_for_packagemap, no_chdir => 0 }, $topDir );

# Recurse through all test source tree to find java unit test files to copy to the projects under src/main/test
find( { wanted => \&wanted_for_testsources, no_chdir => 0 }, $topDir . "/test/src" );

`svn delete $topDir/test/src`;

# Miscellaneous
installLafPluginAssembly();

removeRemainingUnnecessaryFiles();

# End of script; Begin Subroutines

sub wanted_for_create_plugin_poms {

	if ( $_ !~ /plugin_build.xml$/ ) {
		return;
	}

	$newPomFile = $File::Find::dir . "/" . 'pom.xml';
	@parts      = split /\//, $File::Find::dir;
	$artifactId = $parts[$#parts];
	$artifactId =~ s/\.\///;
	push @artifacts, $artifactId;
	$pluginName = "$artifactId Plugin";
	$pluginName =~ s/\b(\w)/\u$1/g;
	$pluginDescription = $pluginName;


	if ( $artifactId =~ 'cache' ) {
		$dependencies = $cache_deps;
	}
	elsif ( $artifactId =~ 'firebirdmanager' ) {
		$dependencies = $firebirdmanager_deps;
	}
	elsif ( $artifactId =~ 'isqlj' ) {
		#$dependencies = $isqlj_deps;
		# 
		# Since this plugin isn't complete and has dependencies on software of 
		# unknown origin, we decided to skip including this in maven.  Also, 
		# we plan to remove it from the repo after our move to SVN.
		
		pop @artifacts;
		
		return;
	}
	elsif ( $artifactId =~ 'laf' ) {

		# laf plugin assembly is a radical departure from other plugins, so it has it's own custom pom now.
		`cp $mavenizeDir/laf-plugin/pom.xml $newPomFile`;
		return;
	}
	elsif ( $artifactId =~ 'oracle' ) {
		$dependencies = $oracle_deps;
	}
	elsif ( $artifactId =~ 'sqlval' ) {
		$dependencies = $sqlval_deps;
	}
	elsif ( $artifactId =~ 'syntax' ) {
		$dependencies = $syntax_deps;
	}
	else {
		$dependencies = '';
	}

    print "Creating new pom: $newPomFile \n"
      . "\t artifactId=$artifactId \n"
      . "\t name=$pluginName \n"
      . "\t description=$pluginDescription\n";

	open( POMFILE, "> $newPomFile" );
	my %vars = (
		artifactId        => $artifactId,
		pluginName        => $pluginName,
		pluginDescription => $pluginDescription,
		dependencies      => $dependencies
	);

	my $result = $plugin_pom_template->fill_in( HASH => \%vars );
	print POMFILE $result;
	close(POMFILE);

    # This is painful to do, but InterSystems leaves me with little choice.  Remove Cache plugin from 
    # the plugin modules pom so that it doesn't get picked up or deployed.  Perhaps we should think about
    # using reflection for this instead.
    if ($artifactId =~ 'cache' ) {
    	pop @artifacts;
    }

	!$onlyCopyPoms && `svn add $newPomFile`;
}

sub wanted_for_source {
	if ( $_ =~ /^plugin_build.xml$/ ) {

		chdir('./src') or die "Couldn't change to src directory in $File::Find::dir : $!\n";
		
		if (!$onlyCopyPoms) {
			print "Removing main directory in $File::Find::dir\n";
			`rm -rf main`;
			`rm -rf test`;
		}

		# Java source files into src/main/java
		findAndCopyJava();

		# Properties files into src/main/resources
		findAndCopyResources('*.properties');

		# *.gif image files into src/main/resources
		findAndCopyResources('*.gif');

		# *.jpg image files into src/main/resources
		findAndCopyResources('*.jpg');

		# *.png image files into src/main/resources
		findAndCopyResources('*.png');

        !$onlyCopyPoms && `svn remove net`;        

		chdir($File::Find::dir) or die "Couldn't change dir back to $File::Find::dir: $!\n";

        #`svn remove plugin_build.xml`;
        !$onlyCopyPoms && `svn propset --file $mavenizeDir/svn.ignore.prop`;

		if ( -d "$File::Find::dir/doc" ) {
			findAndCopyDoc($File::Find::dir);
		}

		chdir($File::Find::dir) or die "Couldn't change dir back to $File::Find::dir: $!\n";
	}
}

# Need to go through the source, build map for packages.  When this is done,
# the $packagemap hash will contain every package found in any Java source file
# as a key and the value of each being the category it was found in (app, fw or plugins).
# The goal is to be able to take each test and relocate it to it's appropriate place
# in the source tree.
sub wanted_for_packagemap {
	if ( $_ !~ /\.java$/ ) {
		return;
	}

	$package = getPackageFromFile($File::Find::name);

	print "wanted_for_packagemap: javafile: $_ (package=$package)\n";

	@parts = split /src/, $File::Find::name;

	if ( $parts[0] =~ /app\/$/ ) {

		print "Found an app package: $package\n\tfor $File::Find::name\n";
		$packagemap->{$package} = "app";

	}
	elsif ( $parts[0] =~ /fw\/$/ ) {

		print "Found an fw package: $package\n\tfor $File::Find::name\n";
		$packagemap->{$package} = "fw";

	}
	elsif ( $File::Find::name =~ /plugins/ ) {

		print "Found a plugin package: $package\n\tfor $File::Find::name\n";
		$packagemap->{$package} = "plugin";

	}
	elsif ( $parts[0] =~ /build\/$/ ) {

		print "Found a build package: $package\n\tfor $File::Find::name\n";

	}
	elsif ( $File::Find::name =~ /sql12\/test/ ) {

		# ignore test files during this pass

	}
	elsif ( $File::Find::name =~ /sql12\/squirrelsql-test-utils/ ) {

		# ignore maven test utilities project

	}
	elsif ( $File::Find::name =~ /sql12\/installer/ ) {

		# ignore installer project

	}
	elsif ( $File::Find::name =~ /sql12\/update-site/ ) {
		
		# ignore update-site project
		
	} else {
		print "Unable to categorize package of file: $File::Find::name\n";
		exit 1;
	}

}

sub wanted_for_testsources {

	if ( $onlyCopyPoms ||  $_ !~ /\.java$/ ) {
		return;
	}

	$package = getPackageFromFile($File::Find::name);

	print "wanted_for_testsources:  \$_= $_  filename=$File::Find::name | package=$package\n";

	$category = $packagemap->{$package};

	# the following three checks are being made because there are test classes
	# that live in a package that no source classes live in.
	if ( $package eq 'utils' ) {
		$category = 'fw';
	}
	if ( $package eq 'net.sourceforge.squirrel_sql' ) {
		$category = 'fw';
	}
	if ( $package eq 'net.sourceforge.squirrel_sql.fw' ) {
		$category = 'fw';
	}
	if ( $package eq 'net.sourceforge.squirrel_sql.test' ) {
		$category = 'fw';
	}
	if ( $package =~ /^'net.sourceforge.squirrel_sql.plugins.'/ ) {
		$category = 'plugin';
	}

	if ( !defined $category ) {
		print "No category found for package $package\n";
		exit 1;
	}

	$relativeDir = convertPackageToDirectory($package);

	if ( $category eq 'app' ) {

		#print "copying test $File::Find::name to app folder $relativeDir\n";
		svnmkdir("$appDir/src/test/java/$relativeDir");

		`svn move $File::Find::name $appDir/src/test/java/$relativeDir`;
	}
	elsif ( $category eq 'fw' ) {

		#print "copying test $File::Find::name to fw folder $relativeDir\n";
		svnmkdir("$fwDir/src/test/java/$relativeDir");
		`svn move $File::Find::name $fwDir/src/test/java/$relativeDir`;
	}
	elsif ( $category eq 'plugin' ) {

		print "relativeDir: $relativeDir\n";
		@parts = split /plugins\//, $relativeDir;
		$pluginName = $parts[1];
		if ( !defined $pluginName ) {

		   # Hack : currently, only firebird tests have a package which doesn't contain the "plugins" package.
			$pluginName = "firebird";
		}
		print "pluginName: $pluginName\n";

		svnmkdir("$pluginsDir/$pluginName/src/test/java/$relativeDir");
		`svn add $pluginsDir/$pluginName/src/test`;
		`svn move $File::Find::name $pluginsDir/$pluginName/src/test/java/$relativeDir`;

	}
}

sub svnmkdir {
	my $absolutepath = shift;

	`svn mkdir --parents $absolutepath`;

}

sub getPackageFromFile {
	$file = shift;
	open( FILE, $file );
	@lines = <FILE>;
	close(FILE);

	$package = "";
	foreach $line (@lines) {
		if ( $line =~ /^package/ ) {
			@pkgparts = split /\s/, $line;
			$package = $pkgparts[1];
			chop($package);
			last;
		}
	}
	return $package;
}

# Converts the package argument into a directory string
sub convertPackageToDirectory {
	$package = shift;
	$package =~ s/\./\//g;
	return $package;
}

sub copyRootPom {
	print "Copying in root pom\n";
	`cp root-pom.xml $topDir/pom.xml`;
	return if $onlyCopyPoms;
	`svn add $topDir/pom.xml`;
}

sub copyTestUtilsProject {
	print "Copying in test dependencies pom\n";

	if ($onlyCopyPoms) {
		`cp $mavenizeDir/squirrelsql-test-utils/pom.xml $topDir/squirrelsql-test-utils/pom.xml`;
		return;
	}

	`rm -rf $topDir/squirrelsql-test-utils`;
	svnmkdir("$topDir/squirrelsql-test-utils");
	`cp $mavenizeDir/squirrelsql-test-utils/pom.xml $topDir/squirrelsql-test-utils/pom.xml`;
	`svn add $topDir/squirrelsql-test-utils/pom.xml`;
}

sub copyPluginsSupportProjects {
	print "Copying in plugins support projects\n";
	
	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";
	
	svnmkdir("$pluginsDir/squirrelsql-plugins-assembly-descriptor/src/main/resources/assemblies");
	`tar --exclude .svn -cvf - squirrelsql-plugins-assembly-descriptor | ( cd $pluginsDir; tar -xvf -)`;
	`svn st $pluginsDir/squirrelsql-plugins-assembly-descriptor | grep "^\?" | awk '{print $2}' | xargs svn add`;

	svnmkdir("$pluginsDir/squirrelsql-plugins-parent-pom");
	`cp $mavenizeDir/squirrelsql-plugins-parent-pom/pom.xml $pluginsDir/squirrelsql-plugins-parent-pom/pom.xml`;
	`svn add $pluginsDir/squirrelsql-plugins-parent-pom/pom.xml`;

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";
}

sub restructureFwModule {
	print "Restructuring fw module\n";

	if ( !$onlyCopyPoms ) {

		# remove effects of previous run
		`rm -rf $fwDir/src/main`;
		`rm -rf $fwDir/src/test`;

		# create maven directories
		svnmkdir("$fwDir/src/main");
		svnmkdir("$fwDir/src/test/resources");
		svnmkdir("$fwDir/src/test/java");
	}
	`cp $mavenizeDir/fw-pom.xml $fwDir/pom.xml`;
	!$onlyCopyPoms && `svn add $fwDir/pom.xml`;
	`cp $mavenizeDir/test-log4j.properties $fwDir/src/test/resources/log4j.properties`;
	!$onlyCopyPoms && `svn add $fwDir/src/test/resources/log4j.properties`;

	return if $onlyCopyPoms;

	chdir("$fwDir/src") or die "Couldn't change directory to $fwDir/src: $!\n";

	findAndCopyJava();

	print "Restructuring fw resources\n";
	findAndCopyResources('*.properties');
	findAndCopyResources('*.gif');
	findAndCopyResources('*.png');

	`svn add --quiet main`;
	`svn add --quiet test`;
	`svn delete net`;
	`svn delete lib`;

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";
}

sub restructureAppModule {

	print "Restructuring app module\n";

	if ( !$onlyCopyPoms ) {
		# remove effects of previous run
		`rm -rf $appDir/src/main`;
		`rm -rf $appDir/src/test`;
	}
	
	`cp app-pom.xml $appDir/pom.xml`;
	
	return if $onlyCopyPoms;
	 
	`svn add $appDir/pom.xml`;
	svnmkdir("$appDir/src/main/java");
	svnmkdir("$appDir/src/main/resources");
	svnmkdir("$appDir/src/test/java");

	chdir("$appDir/src") or die "Couldn\'t change directory to $appDir/src: $!\n";

	findAndCopyJava();

	print "Restructuring fw resources\n";

	findAndCopyResources('*.properties');
	findAndCopyResources('*.gif');
	findAndCopyResources('*.png');
	findAndCopyResources('*.xml');
	findAndCopyResources('*.jpg');

    `cp $mavenizeDir/DefaultFormBuilder.java $appDir/src/main/java/net/sourceforge/squirrel_sql/client/gui/builders`;

	`svn delete net`;
	`svn add test`;
	`svn delete lib`;

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";
}

sub findAndCopyJava {
	return if $onlyCopyPoms;
	print "Copying source files from src/... to /src/main/java...\n";
`find . -name *.java -printf "%h\n" | grep -v "^./main/" | grep -v "^./test/" | grep -v ".svn" | uniq | sort | xargs -ti svn mkdir --parents ./main/java/{}`;
	`svn add --quiet main`;
	`find main -type d | grep -v .svn | sort | xargs -ti svn add --quiet {}`;
`find . -type f -name *.java -print | grep -v "^./main/" | grep -v "^./test/" | grep -v ".svn" | uniq | sort | xargs -ti svn move {} ./main/java/{}`;
}

sub findAndCopyResources {
	return if $onlyCopyPoms;
	my $fileType = shift;
`find . -name $fileType -printf "%h\n" | grep -v "^./main/" | grep -v "^./test/" | grep -v ".svn" | uniq | xargs -ti svn mkdir --parents main/resources/{}`;
	`svn add --quiet main`;
	`find main -type d | grep -v .svn | sort | xargs -ti svn add --quiet {}`;
`find . -type f -name $fileType -print | grep -v "^./main/" | grep -v "^./test/" | grep -v ".svn" | uniq | xargs -ti svn move {} main/resources/{}`;
}

sub findAndCopyDoc {
	return if $onlyCopyPoms;
	
	my $baseDir = shift;
	print
"findAndCopyDoc: moving documentation files from $baseDir/doc/... to $baseDir/src/main/resources/doc...\n";
	chdir("$baseDir/doc") or die "findAndCopyDoc: Couldn't chdir to $baseDir: $!\n";
`find . -type f -printf "%h\n" | grep -v "^./main/" | grep -v ".svn" | uniq | sort | xargs -ti svn mkdir --parents $baseDir/src/main/resources/doc/{}`;
`find . -type f -print | grep -v "^./main/" | grep -v ".svn" | uniq | sort | xargs -ti svn move {} $baseDir/src/main/resources/doc/{}`;
}

sub copyInstallerProjects {

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";

	print "Copying in installer projects\n";
	
	if (!$onlyCopyPoms) {
	   `rm -rf $installerDir`;
	   `mkdir -p $installerDir`;
    }     
	
	svnmkdir("$installerDir/squirrelsql-java-version-checker/src/main/java");
	svnmkdir("$installerDir/squirrelsql-java-version-checker/src/main/resources");
	`tar --exclude .svn -cvf - squirrelsql-java-version-checker | ( cd $installerDir; tar -xvf -)`;
		
	svnmkdir("$installerDir/squirrelsql-launcher/src/main/resources/icons");
	svnmkdir("$installerDir/squirrelsql-launcher/src/main/resources/plugins");
	`tar --exclude .svn -cvf - squirrelsql-launcher | ( cd $installerDir; tar -xvf -)`;
	
	svnmkdir("$installerDir/squirrelsql-other-installer/src/main/resources");
	`tar --exclude .svn -cvf - squirrelsql-other-installer | ( cd $installerDir; tar -xvf -)`;

	svnmkdir("$installerDir/squirrelsql-macosx-installer/src/main/resources");
	`tar --exclude .svn -cvf - squirrelsql-macosx-installer | ( cd $installerDir; tar -xvf -)`;
	
	`cp $mavenizeDir/installer-pom.xml $installerDir/pom.xml`;

	`svn st $installerDir | grep "^\?" | awk '{print $2}' | xargs svn add`;

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";
}

sub copyTranslationProjects {

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";

	print "Copying in translations project\n";
	
	!$onlyCopyPoms && `rm -rf $topDir/squirrelsql-translations`;
	!$onlyCopyPoms && `svn mkdir --parents $topDir/squirrelsql-translations/src/main/resources`;
	
	`cp $mavenizeDir/squirrelsql-translations/pom.xml $topDir/squirrelsql-translations`;
	
	return if $onlyCopyPoms;
	
	`svn add $topDir/squirrelsql-translations/pom.xml`;

	chdir("$topDir/translations") or die "Couldn't change directory to $topDir/translations: $!\n";

	`find . -name "*.jar" | xargs -ti svn move {} $topDir/squirrelsql-translations/src/main/resources`;

	chdir("$topDir") or die "Couldn't change directory to $topDir: $!\n";

	`svn remove translations`;

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";

}

sub copyUpdateSiteProjects {
    chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";

    print "Copying in update-site project\n";
    
	!$onlyCopyPoms && `rm -rf $topDir/update-site`;
    !$onlyCopyPoms && `svn mkdir --parents $topDir/update-site/squirrelsql-update-site-plugin/src/main/java/net/sf/squirrel_sql`;
    !$onlyCopyPoms && `svn mkdir --parents $topDir/update-site/squirrelsql-update-site`;
    
    `cp $mavenizeDir/update-site/pom.xml $topDir/update-site/`;
    `cp $mavenizeDir/update-site/squirrelsql-update-site/pom.xml $topDir/update-site/squirrelsql-update-site/pom.xml`;
    `cp $mavenizeDir/update-site/squirrelsql-update-site-plugin/pom.xml $topDir/update-site/squirrelsql-update-site-plugin/pom.xml`;
	`cp $mavenizeDir/update-site/squirrelsql-update-site-plugin/src/main/java/net/sf/squirrel_sql/BuildUpdateSiteMojo.java $topDir/update-site/squirrelsql-update-site-plugin/src/main/java/net/sf/squirrel_sql/`;
	
	return if $onlyCopyPoms;
	
	`svn add $topDir/update-site/pom.xml`;
	`svn add $topDir/update-site/squirrelsql-update-site/pom.xml`;
	`svn add $topDir/update-site/squirrelsql-update-site-plugin/pom.xml`;
	`svn add $topDir/update-site/squirrelsql-update-site-plugin/src/main/java/net/sf/squirrel_sql/BuildUpdateSiteMojo.java`;
}

sub restructureDocModule {

	print "Restructuring doc module\n";
	
	`cp $mavenizeDir/doc-pom.xml $docDir/pom.xml`;
	
	return if $onlyCopyPoms;
	
	`rm -rf $docDir/src`;
	`mkdir -p $docDir/src/main/resources`;

	chdir("$docDir") or die "Couldn't change directory to $docDir: $!\n";

	`svn add pom.xml`;
	`svn add src`;

	print "Creating directories beneath src/main/resources";
`find . -type f -printf "%h\n" | grep -v .svn| grep -v pom.xml | grep -v target | uniq | sort | xargs -ti mkdir -p src/main/resources/{}`;
	print "Adding directories beneath src/main/resources to subversion";
	`find src -type d | egrep -v "\.svn" | uniq | sort | xargs -ti svn add --quiet {}`;
	print "Moving files in subversion to src/main/resources/...";
`find . -type f -print | grep -v .svn | grep -v pom.xml | grep -v target | uniq | sort | xargs -ti svn move {} src/main/resources/{}`;

	chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";
}

sub restructureWebsiteModule {

	print "Restructuring web-site module\n";
	
	`cp $mavenizeDir/website-pom.xml $websiteDir/pom.xml`;
	
	return if $onlyCopyPoms;
	
	`svn add $websiteDir/pom.xml`;
	`rm -rf $websiteDir/src/main`;
	`mkdir -p $websiteDir/src/main/resources`;
	`cp $websiteDir/faq.html $websiteDir/src/main/resources`;
	`svn add $websiteDir/src`;
}

sub generatePluginPoms {

	$plugin_pom_template = Text::Template->new(
		DELIMITERS => [ '@@--', '--@@' ],
		TYPE       => 'FILE',
		SOURCE     => "$mavenizeDir/plugin-pom.xml"
	);

   # Recurse through the plugin directories to find "plugin_build.xml" - this time to create the pom.xml files
	find( { wanted => \&wanted_for_create_plugin_poms, no_chdir => 1 }, $pluginsDir );
}

sub generatePluginModulesPomFile {
	$modules = "\t<module>squirrelsql-plugins-parent-pom</module>\n";
	$modules .= "\t<module>squirrelsql-plugins-assembly-descriptor</module>\n";
	for $artifact ( sort @artifacts ) {
		$modules .= "\t<module>$artifact</module>\n";
	}

	$pluginsDir =~ s/\s//g;
	$modulespomfile = $pluginsDir . '/pom.xml';
	print "Creating modules pom file ( $modulespomfile ) with modules: \n$modules";

	# Create the plugins module pom file
	open( MODULEPOMFILE, "> $modulespomfile" )
	  or die "Couldn't open file ($modulespomfile): $!\n";
	$plugin_module_pom_template =
	  Text::Template->new( TYPE => 'FILE', SOURCE => "$mavenizeDir/plugin-module-pom.xml" );
	print MODULEPOMFILE $plugin_module_pom_template->fill_in();
	close(MODULEPOMFILE);

	!$onlyCopyPoms && `svn add $modulespomfile`;
}

sub installLafPluginAssembly {

	my $lafPluginAssemblyFile = "$mavenizeDir/laf-plugin/laf-plugin-assembly.xml";
	my $targetFolder          = "$lafPluginDir/src/main/resources/assemblies";
	
	print "Installing L&F Plugin Assembly ($lafPluginAssemblyFile) in $targetFolder\n";
	
	!$onlyCopyPoms && `svn mkdir --parents $targetFolder`;
	`cp $lafPluginAssemblyFile $targetFolder`;
	
	return if $onlyCopyPoms;
	
	`svn add $lafPluginDir/src`;
	`svn add $lafPluginDir/pom.xml`;
	`svn add $targetFolder/laf-plugin-assembly.xml`;
	chdir($lafPluginDir) or die "Couldn't change dir to ($lafPluginDir): $!\n";
}

sub removeRemainingUnnecessaryFiles {
	chdir($topDir) or die "Couldn't change dir to ($topDir): $!\n";
	`svn delete stats`;
	`svn delete mac`;
	chdir($testDir) or die "Couldn't change dir to ($testDir): $!\n";
	`svn delete lib`;
	
}
