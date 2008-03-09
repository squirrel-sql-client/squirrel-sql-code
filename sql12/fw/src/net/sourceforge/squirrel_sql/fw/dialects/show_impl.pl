#! /bin/perl

$pattern = shift;

print "Processing pattern: [$pattern]\n";

opendir( DIR, '.' ) or die "can't opendir .: $!";

while ( defined( $file = readdir(DIR) ) ) {
	next unless ($file =~ /Dialect\.java$/ || $file =~ /DialectExt\.java$/);
	print "Processing file $file\n";
	open( FILE, $file );
	$foundPattern = 0;
	$inBody = 0;
	while (<FILE>) {
		if (/\s$pattern/ && $_ !~ /^\s*\*/) {
			#print "Line [" . $_ . "] matches pattern: $pattern\n";
			$foundPattern = 1;
			next;
		}
		if ($foundPattern && !$inBody) {
			if (/{/) {
				$inBody = 1;
				next;
			}
		}
		if ($inBody && /^\s*}/  ) {
			#print "Line [" . $_ . "] contains a closing bracket\n";
            $inBody = 0;
            $foundPattern = 0;
            next;
        }
        if ($inBody) {
        	print "\t" . $_;
        }
    }
    close(FILE);
    
}

