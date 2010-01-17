#! /usr/bin/perl

$compileroot = `pwd`;

$compileroot =~ s/SOURCES/BUILD/;
$compileroot =~ s/\s*//g;
$compileroot .= '/squirrel-sql';

print "Enter the version being built: ";
$version=<STDIN>;
chomp($version);

open(FILE, 'install.patch');
@lines = <FILE>;
close(FILE);

$searchpattern1 = '\+cvs\.dest\.dir\=';
$searchpattern2 = '\+appversion\=';

open(OUT, '> install.patch');
for $line (@lines) {
	if ($line =~ /$searchpattern1/) {
		$line = '+cvs.dest.dir=' . $compileroot . "\n";
	}
	if ($line =~ /$searchpattern2/) {
		$line = '+appversion=' . $version . "\n";
	}
	print OUT $line;
}
close(OUT);




