#! /usr/bin/perl

use Benchmark;
use File::Spec::Functions;
use Cwd;

$startrun = new Benchmark;

$currDir = getcwd();

$resultsfile = 'results_' . getTime() . '.xls';
$resultsfile = catfile ('summary', $resultsfile);
open (RESULTS, "> $resultsfile") or 
	die "Couldn't open results file ($resultsfile) for writing: $!\n";


#@defaultFrom = ('postgres', 'axion', 'db2', 'derby', 'daffodil',
#                'firebird', 'frontbase', 'h2', 'hsql', 'ingres', 'maxdb', 
#                'mckoi', 'mysql', 'oracle', 'pointbase', 'sqlserver');

# Cannot use Sybase 11.0 on Linux because it doesn't support nullable columns
@defaultFrom = ('firebird', 'postgres', 'daffodil', 'derby', 'h2', 'hsql', 'mckoi', 
                'mysql', 'sybase', 'sqlserver', 'timesten');


#@defaultTo = ( 'postgres', 'axion', 'db2', 'derby', 'daffodil',
#               'firebird', 'frontbase', 'h2', 'hsql', 'ingres', 'maxdb',
#               'mckoi', 'mysql', 'oracle', 'pointbase', 'sqlserver');

@defaultTo = ('firebird', 'postgres', 'daffodil', 'derby', 'h2', 'hsql', 'mckoi', 
              'mysql', 'sybase', 'sqlserver', 'timesten');

$arg1 = shift (@ARGV);
$arg2 = shift (@ARGV);
$showgnumeric = 1;

if (defined $arg1 && defined $arg2) {
	if ($arg1 eq "*") {
		@from = @defaultFrom;
	} else {
		@from = ( $arg1 );
	}
	if ($arg2 eq "*") {
		@to = @defaultTo;
	} else {
		@to =  ( $arg2 );
	}
	if ($arg1 ne "*" && $arg2 ne "*") {
		$showgnumeric = 0;
	}
} else {
	@from = @defaultFrom;
	@to = @defaultTo;
}

		 
#@from = ('postgres', 'sqlserver', 'axion' );

#@to = ('postgres', 'sqlserver', 'axion');
		 

# Set autoflush to true
$| = 0;

@sourcepropkeys = ( 'sourceDriver', 'sourceJdbcUrl', 'sourceCatalog',
                    'sourceSchema', 'sourceUser', 'sourcePass' );
					
@destpropkeys = ( 'destDriver', 'destJdbcUrl', 'destCatalog',  
				  'destSchema', 'destUser', 'destPass');

$mainclass = 'net.sourceforge.squirrel_sql.plugins.dbcopy.CopyExecutorTestRunner';


# These two are HoH indexed first by dbname (@from) and then by source (or dest)
# keys.
%sourceprops;
%destprops;
%copyresulttimes;

$classpath = buildClasspath();

print "Using CLASSPATH=$classpath\n";

#$ENV{CLASSPATH}=$classpath;

####################
# Main routine
####################

readPropertyFiles();

runTests();

printResults();

$endrun = new Benchmark;
$td = timediff($endrun, $startrun);
print "The script total run time was:",timestr($td),"\n";

if ($showgnumeric) {
	# launch gnumeric to display the results summary, but only if it has 
	# more than one cell.
	system("gnumeric $resultsfile &");
}

####################
# routines below
####################

sub buildClasspath {
    if ($^O =~ /linux/) {
        print "Opening cp.conf.unix\n";
        open(FILE, 'cp.conf.unix');
        $pathsep = ":";  
    } else {
        print "Opening cp.conf\n";    
        open(FILE, 'cp.conf');    
        $pathsep = ";";
    }
	@lines = <FILE>;
	close(FILE);
	foreach $line (@lines) {
		next if $line =~ /^#/;		
        next if $line =~ /^$/;
        $value = $line;
        $value =~ s/\n//;
        push @resultarr, $value;
	}
	$cpath = join $pathsep, @resultarr;
    return $cpath;
}

sub createPropsFile {
    my $from = shift;
    my $to = shift;
    my $filename = catfile('tmp', $from . "_to_" . $to . '.properties');
	open(OUTPROPS, "> $filename");
	print OUTPROPS "\n" . '#' . " $from\n";
    foreach $key (@sourcepropkeys) {
		print OUTPROPS "$key=$sourceprops{$from}{$key}\n";
	}
	print OUTPROPS "\n" . '#' . " $to\n";
	foreach $key (@destpropkeys) {
		print OUTPROPS "$key=$destprops{$to}{$key}\n";	
	}
	print OUTPROPS "\n";
	print OUTPROPS "tablesToCopy=*\n";
	print OUTPROPS "\n";
	print OUTPROPS '#' . " whether or not to print SQL statements out to console during copy operation\n";
	print OUTPROPS "showSqlStatements=true\n";
	print OUTPROPS "\n";
	print OUTPROPS '#' . " the directory where the DBCopy preferences file (prefs.xml) is stored.\n";
	$prefsDir = $currDir;
	print OUTPROPS "prefsDir=$prefsDir\n";	
	
	close(OUTPROPS);
}

sub getTime {
	($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) = localtime;

	$fullyear = $year + 1900;

	$mon += 1;
	
	if ($mon < 10) {
		$mon = '0' . $mon;	
	}
	if ($mday < 10) {
		$mday = '0' . $mday;
	}
	
	if ($hour =~ /^\d$/) {
		$hour = '0' . $hour;
	}

	if ($min =~ /^\d$/) {
		$min = '0' . $min;
	}

	if ($sec =~ /^\d$/) {
		$sec = '0' . $sec;
	}
	
	$timestr = $fullyear . $mon . $mday . '_'. $hour . $min . $sec;

	return $timestr;
}

sub printResults {
	print "Printing results from run\n\n";
	print RESULTS "\t";
	foreach $todb (sort @to) {
		print RESULTS "$todb\t";	
	}
	print RESULTS "\n";
	foreach $fromdb (sort keys %copyresulttimes ) {
		print RESULTS "$fromdb\t";
		foreach $todb (sort keys %{ $copyresulttimes{$fromdb} } ) {
			print RESULTS $copyresulttimes{$fromdb}{$todb} . "\t";
		}
		print RESULTS "\n";
	}
	close(RESULTS);
}

sub readPropertyFiles {
	my @all = (@from, @to);
	%seen = ();
	foreach $db (@all) {
		unless ($seen{$db}) {
			$seen{$db} = 1;
			readPropertyFile($db);
		}
	}	 
}

sub readPropertyFile {
	my $db = shift;
	$propertyfile = catfile("conf" , $db . '.properties');
	print "Reading property file $propertyfile\n";
	open(INPROPS, $propertyfile);
	@lines = <INPROPS>;
	close(INPROPS);
	foreach $line (@lines) {
		($key, $value) = split '=', $line;
		$value =~ s/\n//g;
		if ($key =~ /^source/) {
			$sourceprops{$db}{$key} = $value;
		} else {
			$destprops{$db}{$key} = $value;	
		}
	}		
}

sub runCopy {
	$from = shift;
	$to = shift;
	
	$propsfile = $from . _to_ . $to;
	$logsdir = catfile('logs', $from);
	$logfile = ($logsdir, $from . _to_ . $to . '.log');
	print "Running $from -> $to...";
	$logfile = catfile('logs', $from,  $from . "_to_" . $to . '.log');
	#print "CLASSPATH=$classpath\n";
	$cmd="java -cp $classpath $mainclass $propsfile 2>&1";
	#print "Running command cmd=$cmd\n";

	open(LOG, "> $logfile");

	$copytime = '';
	open(CMDOUT, "$cmd|") or die "Can't run the cmd - \n$cmd \nReason: $!\n";
	while (<CMDOUT>) {
		if (defined $arg1 || defined $arg2) {
			print "$_";
		} 
		print LOG "$_";
		if ($_ =~ /Copy\soperation\sfinished\sin\s/) {
			@parts = split /in\s/, $_;
			@parts = split /\s/, $parts[1];
			$copytime = $parts[0];
		}
	}
	close(CMDOUT);
	if ($copytime eq '') {
		$abslogfile = catfile($currDir, $logfile);
		print "failed ( $abslogfile )\n";
		$copyresulttimes{$from}{$to} = 'fail';
	} else {
		print "$copytime seconds\n";
		$copyresulttimes{$from}{$to} = $copytime;
	}
	
	close(LOG);
	
}

sub runTests {
	foreach $from (@from) {
		foreach $to (@to) {
			if ($from ne $to) {
				createPropsFile($from, $to);
				runCopy($from, $to);
			} else {
				$copyresulttimes{$from}{$to} = 'X';
			}
		}	
	}
}
