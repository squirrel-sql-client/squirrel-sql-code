
For a long time now it has been apparent that having an RPM for users to download (and linux distros to include) would be a good thing.  However, with so many SQuirreL plugins that are built and delivered in lock-step with base software it seemed like it would be a nightmare to determine 1) which plugins go into the base RPM, and 2) providing/maintaining separate RPMs for all of those plugins and translations that don't get shipped in the base RPM.  But now there is the software update feature which allows any base installed version of SQuirreL to fetch and install other plugins from the cloud.  So, shipping a base version of SQuirreL in an RPM now makes a lot of sense.  So, a spec file and patches were developed to produce the RPM from the squirrel source archive that gets produced with every release.  This document describes the setup steps that are necessary to create the RPM.

== RPM Build Environment ==
 1. Obviously, you need a machine with RPM build tools installed (These instructions were tested with Fedora 12)
     * Fedora 12 comes without the dev tools installed, so you may need to do the following as root:
{{{
yum groupinstall "Development Tools"
yum install rpmdevtools
}}}
     * Since you never want to build RPMs as root, login as a regular user and create the RPM build directories in your home directory using the following command:
{{{
rpmdev-setuptree
}}}
 1. Since SQuirreL is currently built with Ant, you need at least 1.7 version of Ant.
{{{
sudo yum install ant
}}}
 1. Since we are starting from source, javac is required, but only Sun javac 1.6 or greater is supported:
{{{
sudo yum install java-1.6.0-openjdk-devel
}}}
 1. Part of installing SQuirreL into a place that rpmbuild can package involves creating the [http://izpack.org/ IzPack] installer. So you need to have that installed (the SQuirreL build script expects it to be located in /opt/IzPack).  So download it from http://izpack.org/downloads/, and install it using the following command (be sure to choose /opt/IzPack as the install directory):
{{{
sudo java -jar IzPack-install-<version>.jar
}}}

Note: You only need to install the IzPack Core libraries option with the installer.

== Copying Source Files ==
 1. Copy the spec file attached to this page into ~/rpmbuild/SPECS.
 1. Extract the patches archive attached to this page into ~/rpmbuild/SOURCES.
    1. The file called install.patch has a hard-coded reference the version being built, which needs to be replaced when the version changes.  For this, there is a perl script called fix-install-patch.pl in the patches archive.  Execute it as follows and enter the version when prompted:
{{{
perl fix-install-patch.pl
}}}
 1. Get the release source archive (.zip) and copy it into ~/rpmbuild/SOURCES (don't extract it).

== Create the Binary RPM ==
 1. Create the RPM by executing the following commands:
{{{
cd ~/rpmbuild/SPECS
rpmbuild -bb squirrel-sql.spec
}}}
 1. When that is done, the RPM can be found in ~/rpmbuild/RPMS/noarch.
