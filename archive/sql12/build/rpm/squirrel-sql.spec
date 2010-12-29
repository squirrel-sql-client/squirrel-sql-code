
Name: squirrel-sql
Group: Development/Tools
Version: 3.0.3
Release: 1
Prefix: /opt
Distribution: Red Hat Linux
URL: http://squirrelsql.org
Packager: Rob Manning <robert [DOT] m [DOT] manning [AT] gmail.com>
License: LGPL
Summary: A database front-end written in Java

# This is the directory where the source gets extracted to and compiled
%define compileroot %(echo ~/rpmbuild/BUILD)/squirrel-sql

# This is where the install image files will be staged.  Files and folders under this directory 
# will appear under root ('/') directory on the target machine.
%define buildroot %{_tmppath}/%{name}-root
Buildroot: %{buildroot}

BuildArch: noarch 
BuildRequires: ant >= 1.7
BuildRequires: java-1.6.0-openjdk-devel

Requires: java-1.6.0-openjdk

Source0: http://sf.net/projects/squirrel-sql/files/1-stable/%20%28Installer%29/squirrel-sql-3.0.1-src.zip/download

# This patch adds an encoding attribute (utf-8) to each of the ant build
# files wherever the javac ant task is invoked.
# (modified files = all ant build files)
Patch1: ant-javac-encoding.patch

# This patch sets the location to build the installer and use it 
# to product an install image in buildroot folder.
# (modified files = autoinstall.xml.template and build-weekly.properties)
Patch2: install.patch

# This patch set the location of the install directory in squirrel-sql.sh
# Normally this is done by the installer, but since we are driving the 
# installer from rpm, the installer thinks the final install directory is 
# in buildroot instead of /opt/SQuirreLSQLClient.
# (modified files = squirrel-sql.sh)
Patch3: launch-script.patch

# This patch removes the dependencies from build.xml on findbugs and pmd.
# (modified files = build.xml)
Patch4: build-remove-findbugs-pmd-deps.patch

%description
SQuirreL SQL Client is a graphical Java program that will allow you to view the structure of a JDBC compliant database, browse the data in tables, issue SQL commands, etc. The minimum version of Java supported is 1.6.x as of SQuirreL version 3.0. A short introduction can be found here: http://www.squirrelsql.org/index.php?page=screenshots

%prep
echo %{buildroot}
rm -rf %{compileroot}
mkdir %{compileroot}
cd %{compileroot}
jar -xf ~/rpmbuild/SOURCES/squirrel-sql-%{version}-src.zip
%patch1
%patch2
%patch3
%patch4

# In order to include a valid release.xml into the rpm for using the 
# update feature, we need to create the update site.  The process of 
# cataloging the jars for the update site is required to generate a
# valid release.xml file.
%build
cd %{compileroot}/sql12/build/snapshot
ant -f build-weekly.xml init executebuild createupdatesite

# We build the installer here so that we can just use it to do the 
# install into the buildroot/opt/SQuirreLSQLClient directory.  The 
# installer has an autorun feature that we use so that we don't have
# to deal with the graphical wizard.  Finally, we remove files that 
# aren't relevant to an RPM installation.
%install
rm -rf %{buildroot}
mkdir -p %{buildroot}/opt/SQuirreLSQLClient
cd %{compileroot}/sql12/build/snapshot
ant -f build-weekly.xml init createOtherInstaller createinstallscript runOtherInstaller
rm -rf %{buildroot}/opt/SQuirreLSQLClient/Uninstaller
rm %{buildroot}/opt/SQuirreLSQLClient/squirrel-sql.bat
rm %{buildroot}/opt/SQuirreLSQLClient/addpath.bat
rm %{buildroot}/opt/SQuirreLSQLClient/.installationinformation
mkdir %{buildroot}/opt/SQuirreLSQLClient/update/backup
mkdir %{buildroot}/opt/SQuirreLSQLClient/update/downloads

# The "/opt" prefix is necessary here.  It is where we install
# by default, when the rpm is installed without being relocated.
%files
%defattr(-,root,root)
%attr(0777,root,root) /opt/SQuirreLSQLClient/doc
%attr(0777,root,root) /opt/SQuirreLSQLClient/icons
%attr(0777,root,root) /opt/SQuirreLSQLClient/lib
%attr(0666,root,root) /opt/SQuirreLSQLClient/log4j.properties
%attr(0777,root,root) /opt/SQuirreLSQLClient/plugins
%attr(0666,root,root) /opt/SQuirreLSQLClient/squirrel-sql.jar
%attr(0777,root,root) /opt/SQuirreLSQLClient/squirrel-sql.sh
# The update dir has to be writable by everyone since SQuirreL on the user's
# behalf will create the changeList.xml file there. 
%attr(0777,root,root) /opt/SQuirreLSQLClient/update
%attr(0666,root,root) /opt/SQuirreLSQLClient/update-log4j.properties

# Since this RPM is relocatable, we need to fix the SQuirreL home environment 
# variable in the squirrel-sql.sh launch script to point to the correct location
# if RPM_INSTALL_PREFIX is set. RPM_INSTALL_PREFIX is only set when --relocate
# is specified during install.  We need to make the top-level directory writable
# since the update feature will remove files in this location under certain 
# circumstances.
%post
if [ "$RPM_INSTALL_PREFIX" != "" ]; then
    sed -i  "s:/opt:$RPM_INSTALL_PREFIX:g" $RPM_INSTALL_PREFIX/SQuirreLSQLClient/squirrel-sql.sh; chmod 777 $RPM_INSTALL_PREFIX/SQuirreLSQLClient
else 
    chmod 777 /opt/SQuirreLSQLClient
fi

# RPM tracks the files that are installed by filesize/checksum.  Since the 
# update feature will overwrite files, this can cause RPM not to remove 
# those overwritten files when it is uninstalling.  So we need to manually
# remove everything after RPM has uninstalled what it knows about.
%postun
if [ "$RPM_INSTALL_PREFIX" != "" ]; then
    rm -rf $RPM_INSTALL_PREFIX/SQuirreLSQLClient
else
    rm -rf /opt/SQuirreLSQLClient
fi

%changelog
* Sat Jan 9 2010 Rob Manning <robert [DOT] m [DOT] manning [AT] gmail [DOT] com>
- initial RPM release


