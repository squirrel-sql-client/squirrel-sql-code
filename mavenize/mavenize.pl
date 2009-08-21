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

$mavenizeDir = `pwd`;
$mavenizeDir =~ s/\s//g;

$topDir = "/home/manningr/projects/squirrel_maven2/sql12";
$pluginsDir = $topDir . "/plugins";
$lafPluginDir = $pluginsDir . "/laf";
$installerDir = $topDir . "/installer";
$docDir = $topDir . "/doc";
$websiteDir = $topDir . "/web-site";

# copy in the root pom - this pom builds all of SQuirreL
`cp root-pom.xml $topDir/pom.xml`;

# copy in the test utilities project - I nix'd this since there were dependency issues
# with BaseSQuirreLJUnit4TestCase depending on fw's LoggerController.  It proved to be
# too difficult to move BaseSQuirreLJUnit4TestCase out of the fw module.
#`cp -r squirrelsql-test-utils $topDir`;

# copy in plugins support projects 
print "Copying in plugins support projects\n";
`rm -rf squirrelsql-swingsetthemes/squirrelsql-swingsetthemes`;
`cp -r squirrelsql-plugins-assembly-descriptor $pluginsDir`;
`rm -rf $pluginsDir/squirrelsql-plugins-parent-pom`;
`cp -r squirrelsql-plugins-parent-pom $pluginsDir`;
`rm -rf $pluginsDir/squirrelsql-swingsetthemes`;
`cp -r squirrelsql-swingsetthemes $pluginsDir`;

# copy in the installer projects
print "Copying in installer projects\n";
`rm -rf $installerDir`;
`mkdir -p $installerDir`;
`cp -r squirrelsql-java-version-checker $installerDir`;
`cp -r squirrelsql-launcher $installerDir`;
`cp -r squirrelsql-other-installer $installerDir`;
`cp installer-pom.xml $installerDir/pom.xml`;

# copy in the translations project
print "Copying in translations project\n";
`rm -rf $topDir/squirrelsql-translations`;
`cp -r squirrelsql-translations $topDir`;

# restructure fw module
print "Restructuring fw module\n";
$fwDir = $topDir . "/fw";
`cp fw-pom.xml $fwDir/pom.xml`;
`cp test-log4j.properties $fwDir/src/test/resources/log4j.properties`;
`mkdir -p $fwDir/src/main/java`;
`mkdir -p $fwDir/src/main/resources`;
`mkdir -p $fwDir/src/test/java`;
`cp -r $fwDir/src/net  $fwDir/src/main/java`;
chdir("$fwDir/src") or die "Couldn't change directory to $fwDir/src: $!\n";
`find . -name *.properties -printf "%h\n" | grep -v main | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name *.properties -print | grep -v main | xargs -i cp {} main/resources/{}`;
`find . -name *.gif -printf "%h\n" | grep -v main | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name *.gif -print | grep -v main | xargs -i cp {} main/resources/{}`;
`find . -name *.png -printf "%h\n" | grep -v main | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name *.png -print | grep -v main | xargs -i cp {} main/resources/{}`;

`find $fwDir/src/main/java -name *.properties | xargs -i rm {}`;
`find $fwDir/src/main/java -name "*.gif" | xargs -i rm {}`;
`find $fwDir/src/main/java -name "*.png" | xargs -i rm {}`;

chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";

# restructure app module
print "Restructuring app module\n";
$appDir = $topDir . "/app";
`cp app-pom.xml $appDir/pom.xml`;
`mkdir -p $appDir/src/main/java`;
`mkdir -p $appDir/src/main/resources`;
`mkdir -p $appDir/src/test/java`;
`cp -r $appDir/src/net  $appDir/src/main/java`;


chdir("$appDir/src") or die "Couldn't change directory to $appDir/src: $!\n";
`find . -name *.properties -printf "%h\n" | grep -v "^./main" | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name *.properties -print | grep -v "^./main" | xargs -i cp {} main/resources/{}`;
`find . -name *.gif -printf "%h\n" | grep -v "^./main" | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name *.gif -print | grep -v "^./main" | xargs -i cp {} main/resources/{}`;
`find . -name *.png -printf "%h\n" | grep -v "^./main" | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name *.png -print | grep -v "^./main" | xargs -i cp {} main/resources/{}`;
`find . -name "*.xml" -printf "%h\n" | grep -v "^./main" | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name "*.xml" -print | grep -v "^./main" | xargs -i cp {} main/resources/{}`;
`find . -name "*.jpg" -printf "%h\n" | grep -v "^./main" | xargs -i mkdir -p main/resources/{}`;
`find . -type f -name "*.jpg" -print | grep -v "^./main" | xargs -i cp {} main/resources/{}`;


`find $appDir/src/main/java -name *.properties | xargs -i rm {}`;
`find $appDir/src/main/java -name "*.gif" | xargs -i rm {}`;
`find $appDir/src/main/java -name "*.png" | xargs -i rm {}`;
`find $appDir/src/main/java -name "*.xml" | xargs -i rm {}`;
`find $appDir/src/main/java -name "*.jpg" | xargs -i rm {}`;


chdir($mavenizeDir) or die "Couldn't change directory to $mavenizeDir: $!\n";

# Restructure the doc module
print "Restructuring doc module\n";
`cp $mavenizeDir/doc-pom.xml $docDir/pom.xml`;
`rm -rf $docDir/src`;
`mkdir -p $docDir/src/main/resources`;
`cp $docDir/*.txt  $docDir/src/main/resources/`;
`cp $docDir/*.html  $docDir/src/main/resources/`;
`cp $docDir/*.css  $docDir/src/main/resources/`;
`cp -r $docDir/images $docDir/src/main/resources/`;
`cp -r $docDir/licences $docDir/src/main/resources/`;

# Restructure the web-site module
print "Restructuring web-site module\n";
`cp $mavenizeDir/website-pom.xml $websiteDir/pom.xml`;
`rm -rf $websiteDir/src/main`;
`mkdir -p $websiteDir/src/main/resources`;
`cp $websiteDir/faq.html $websiteDir/src/main/resources`;

$cache_deps = <<"EOF";
<dependencies>
      <dependency>
         <groupId>com.intersystems.cachedb</groupId>
         <artifactId>CacheDB</artifactId>
         <version>2008.2.0.526.0</version>
      </dependency>
   </dependencies>
EOF

$firebirdmanager_deps = <<"EOF";
<dependencies>
   	<dependency>
   		<groupId>org.firebird</groupId>
   		<artifactId>jaybird-full</artifactId>
   		<version>2.1.1</version>
   	</dependency>
   </dependencies>
EOF

$isqlj_deps = <<"EOF";
<dependencies>
   	<dependency>
   		<groupId>org.rege</groupId>
   		<artifactId>isqlj.jar</artifactId>
   		<version>1.8</version>
   	</dependency>
   </dependencies>
EOF

$oracle_deps = <<"EOF";
<dependencies>
   	<dependency>
   		<groupId>com.sun</groupId>
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
      <dependency>
         <groupId>com.techtrader</groupId>
         <artifactId>tt-bytecode</artifactId>
         <version>1.0</version>
      </dependency>
   </dependencies>
EOF

$syntax_deps = <<"EOF";
<dependencies>
   	<dependency>
   		<groupId>org.netbeans</groupId>
   		<artifactId>openide</artifactId>
   		<version>041121</version>
   	</dependency>
   	<dependency>
   		<groupId>org.netbeans</groupId>
   		<artifactId>org-netbeans-modules-editor</artifactId>
   		<version>041121</version>
   	</dependency>
   	<dependency>
   		<groupId>org.netbeans</groupId>
   		<artifactId>org-netbeans-modules-editor-fold</artifactId>
   		<version>041121</version>
   	</dependency>
   	<dependency>
   		<groupId>org.netbeans</groupId>
   		<artifactId>org-netbeans-modules-editor-lib</artifactId>
   		<version>041121</version>
   	</dependency>
   	<dependency>
   		<groupId>org.netbeans</groupId>
   		<artifactId>org-netbeans-modules-editor-util</artifactId>
   		<version>041121</version>
   	</dependency>
   	<dependency>
   		<groupId>com.ostermiller</groupId>
   		<artifactId>syntax</artifactId>
   		<version>1.0</version>
   	</dependency>
   </dependencies>
EOF

$plugin_pom_template = Text::Template->new(DELIMITERS => [ '@@--' , '--@@' ], TYPE => 'FILE',  SOURCE => 'plugin-pom.xml');


# Recurse through the plugin directories to find "plugin_build.xml" - this time to create the pom.xml files
find( { wanted => \&wanted_for_poms, no_chdir => 1 }, $pluginsDir);

$modules = "\t<module>squirrelsql-plugins-parent-pom</module>\n";
$modules .= "\t<module>squirrelsql-plugins-assembly-descriptor</module>\n";
for $artifact (sort @artifacts) {
	$modules .= "\t<module>$artifact</module>\n";
}

$pluginsDir =~ s/\s//g;
$modulespomfile = $pluginsDir . '/pom.xml';
print "Creating modules pom file ( $modulespomfile ) with modules: \n$modules";


# Create the plugins module pom file
open (MODULEPOMFILE,"> $modulespomfile")
   or die "Couldn't open file ($modulespomfile): $!\n";
$plugin_module_pom_template = Text::Template->new(TYPE => 'FILE',  SOURCE => 'plugin-module-pom.xml');
print MODULEPOMFILE $plugin_module_pom_template->fill_in();
close(MODULEPOMFILE);

# Recurse through the plugin directories to find "plugin_build.xml" - this time to copy source code under /src/main/java
find( { wanted => \&wanted_for_source, no_chdir => 0 }, $pluginsDir);

# Recurse through all source directories to find java files to build package map so that test files can be relocated.
find( { wanted => \&wanted_for_packagemap, no_chdir => 0 }, $topDir);

# Recurse through all test source tree to find java unit test files to copy to the projects under src/main/test
find( { wanted => \&wanted_for_testsources, no_chdir => 0 }, $topDir . "/test/src");

# Miscellaneous
print "Installing L&F Plugin Assembly ($mavenizeDir/laf-plugin/laf-plugin-assembly.xml) in $lafPluginDir/src/main/resources/assemblies\n";
`mkdir -p $lafPluginDir/src/main/resources/assemblies`;
`cp $mavenizeDir/laf-plugin/laf-plugin-assembly.xml $lafPluginDir/src/main/resources/assemblies`;



# End of script; Begin Subroutines

sub wanted_for_poms {

	if ( $_ !~ 'plugin_build.xml' ) {
		return;
	}
	$newPomFile = $File::Find::dir . "/" . 'pom.xml';
	@parts = split /\//, $File::Find::dir;
	$artifactId = $parts[$#parts];
	$artifactId =~ s/\.\///;
	push @artifacts, $artifactId;
	$pluginName = "$artifactId Plugin";
	$pluginName =~ s/\b(\w)/\u$1/g;
	$pluginDescription = $pluginName;

	print "Creating new pom: $newPomFile \n\t artifactId=$artifactId \n\t name=$pluginName \n\t description=$pluginDescription\n";
	
	if ($artifactId =~ 'cache') {
		$dependencies = $cache_deps;
	} elsif ($artifactId =~ 'firebirdmanager') {
		$dependencies = $firebirdmanager_deps;
 	} elsif ($artifactId =~ 'isqlj') {
 		$dependencies = $isqlj_deps;
	} elsif ($artifactId =~ 'laf') {
		# laf plugin assembly is a radical departure from other plugins, so it has it's own custom pom now.
		`cp $mavenizeDir/laf-plugin/pom.xml $newPomFile`;
		return;		
	} elsif ($artifactId =~ 'oracle') {
		$dependencies = $oracle_deps;
        } elsif ($artifactId =~ 'sqlval') {
		$dependencies = $sqlval_deps;			
        } elsif ($artifactId =~ 'syntax') {
		$dependencies = $syntax_deps;
	} else {
		$dependencies = '';
	}

	open (POMFILE, "> $newPomFile");
	my %vars = ( 	artifactId => $artifactId,
			pluginName => $pluginName,
			pluginDescription => $pluginDescription,
			dependencies => $dependencies );
	
	my $result = $plugin_pom_template->fill_in(HASH => \%vars);
	print POMFILE $result;
	close(POMFILE);

}

sub wanted_for_source {
	if ( $_ =~ 'plugin_build.xml') {
		chdir('./src') or die "Couldn't change to src directory in $File::Find::dir : $!\n";	
		print "Removing main directory in $File::Find::dir\n";
		`rm -rf main`;
		`rm -rf test`;
		
		# Java source files into src/main/java
		print "Creating main/java directory and sub-directories\n";
		`find . -type d | grep -v CVS | xargs -i mkdir -p main/java/{}`;
		print "Copying source files into main/java sub-tree\n";	
		`find . -type f -name *.java -print | grep -v CVS | xargs -i cp {} main/java/{}`;

		# Properties files into src/main/resources
		print "Creating main/resources directory and sub-directories\n";
		`find . -name *.properties -printf "%h\n" | xargs -i mkdir -p main/resources/{}`;
		print "Copying properties files into main/resources sub-tree\n";	
		`find . -type f -name *.properties -print | grep -v CVS | xargs -i cp {} main/resources/{}`;

		# *.gif image files into src/main/resources
		print "Copying image files (*.gif) into main/resources sub-tree\n";
		`find . -type f -name *.gif -printf "%h\n" | grep -v CVS | xargs -i mkdir -p main/resources/{}`;
		`find . -type f -name *.gif -print | grep -v CVS | xargs -i cp {} main/resources/{}`;

		# *.jpg image files into src/main/resources
		print "Copying image files (*.jpg) into main/resources sub-tree\n";
		`find . -type f -name *.jpg -printf "%h\n" | grep -v CVS | xargs -i mkdir -p main/resources/{}`;
		`find . -type f -name *.jpg -print | grep -v CVS | xargs -i cp {} main/resources/{}`;

		# *.png image files into src/main/resources
		print "Copying image files (*.png) into main/resources sub-tree\n";
		`find . -type f -name *.png -printf "%h\n" | grep -v CVS | xargs -i mkdir -p main/resources/{}`;
		`find . -type f -name *.png -print | grep -v CVS | xargs -i cp {} main/resources/{}`;

		chdir('..');
		
		if (-d 'doc') {
			`cp -r doc src/main/resources`;
		}
	}
}

# Need to go through the source, build map for packages.  When this is done,
# the $packagemap hash will contain every package found in any Java source file 
# as a key and the value of each being the category it was found in (app, fw or plugins).
# The goal is to be able to take each test and relocate it to it's appropriate place
# in the source tree.
sub wanted_for_packagemap {
	if ($_ !~ /\.java$/) {
		return;
	}
	$package = getPackageFromFile($File::Find::name);

	@parts = split /src/,  $File::Find::name;
	
	if ($parts[0] =~ /app\/$/) {

		#print "Found an app package: $package\n\tfor $File::Find::name\n";
		$packagemap->{$package} = "app";

	} elsif ($parts[0] =~ /fw\/$/) {

		#print "Found an fw package: $package\n\tfor $File::Find::name\n";
		$packagemap->{$package} = "fw";		
	
	} elsif ($File::Find::name =~ /plugins/) {

		#print "Found a plugin package: $package\n\tfor $File::Find::name\n";
		$packagemap->{$package} = "plugin";	

    } elsif ($parts[0] =~ /build\/$/) {

		#print "Found a build package: $package\n\tfor $File::Find::name\n";
		
    } elsif ($File::Find::name =~ /sql12\/test/) {

		# ignore test files during this pass

    } elsif ($File::Find::name =~ /sql12\/squirrelsql-test-utils/) {

		# ignore maven test utilities project

    } elsif ($File::Find::name =~ /sql12\/installer/) {

        # ignore installer project

	} else {
		print "Unable to categorize package of file: $File::Find::name\n";
		exit 1;
	}

}

sub wanted_for_testsources {
	
	if ($_ !~ /\.java$/) {
		return;
	}
	$package = getPackageFromFile($File::Find::name);
	
	$category = $packagemap->{$package};

	# the following three checks are being made because there are test classes
        # that live in a package that no source classes live in.
	if ($package eq 'utils') {
		$category = 'fw';
	}
	if ($package eq 'net.sourceforge.squirrel_sql') {
		$category = 'fw';		
	}
	if ($package eq 'net.sourceforge.squirrel_sql.fw') {
		$category = 'fw';		
	}
	if ($package eq 'net.sourceforge.squirrel_sql.test') {
		$category = 'fw';		
	}
	if ($package =~ /^'net.sourceforge.squirrel_sql.plugins.'/) {
		$category = 'plugin';
	}

	if (!defined $category) {
		print "No category found for package $package\n";
		exit 1;
	}

	$relativeDir = convertPackageToDirectory($package);

	if ($category eq 'app') {
		#print "copying test $File::Find::name to app folder $relativeDir\n";
		`mkdir -p $appDir/src/test/java/$relativeDir`;
		`cp $File::Find::name $appDir/src/test/java/$relativeDir`;
	} elsif ($category eq 'fw') {
		#print "copying test $File::Find::name to fw folder $relativeDir\n";
		`mkdir -p $fwDir/src/test/java/$relativeDir`;
		`cp $File::Find::name $fwDir/src/test/java/$relativeDir`;
	} elsif ($category eq 'plugin') {		
		#if (! defined $onetimeonly) {		
			#$onetimeonly = 1;		
			print "relativeDir: $relativeDir\n";
			@parts = split /plugins\//, $relativeDir;
			$pluginName = $parts[1];
			if (! defined $pluginName) {
			    # Hack : currently, only firebird tests have a package which doesn't contain the "plugins" package.
				$pluginName = "firebird";
			}
			print "pluginName: $pluginName\n";
			`mkdir -p $pluginsDir/$pluginName/src/test/java/$relativeDir`;
			`cp $File::Find::name $pluginsDir/$pluginName/src/test/java/$relativeDir`;
			print "Copied $File::Find::name to $pluginsDir/$pluginName/src/test/java/$relativeDir\n";
		#}
		
	}
}

sub getPackageFromFile {
	$file = shift;
	open(FILE, $file);
	@lines = <FILE>;
	close(FILE);

	$package = "";
	foreach $line (@lines) {
		if ($line =~ /^package/) {
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



