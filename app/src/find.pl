

use File::Find;

$filecount = 0;

find(\&wanted, '.');

print "Total files: $filecount\n";



sub wanted {

	if ($_ =~ /\.java$/) {
		#print "Processing file $_\n";
		open(FILE, $_);
		@lines = <FILE>;
		$incomment = 0;
		$inI18nInterface = 0;
		$count = 0;
		@matchlines;
		foreach $line (@lines) {
			$count++;
			if ($line =~ /^\s*\*\//) {
				#print "Out of comment at line $count\n";
				$incomment = 0;
            }
            if ($incomment) {
            	#print "\tSkipping line $count because inside of comment\n";
            	next;
            }
			if ($line =~ /\/\*/) {
				#print "in comment at line $count\n";
				$incomment = 1;
				next;
			}
			if ($line =~ /interface\s+.*18(N|n)/) {
				#print "in I18n interface at line $count\n";
				$inI18nInterface = 1;
				next;
			}
			if ($inI18nInterface && $line =~ "\}") {
				#print "out of I18n interface at line $count\n";
				$inI18nInterface = 0;
				next;
			}			
			if ($inI18nInterface && $line =~ /\"/ && $line !~ /s\_stringMgr/) {
				push(@matchlines, "$count: $line");
			}
			next if ($incomment);
			next if ($line =~ /^\s*\/\//);
			next if ($line =~ /s_stringMgr/);
			next if ($line =~ /\=\=\s*null/);
			#print "line $count: $line";
			if ($line =~ /JOptionPane\.showMessageDialog\(.*\,\s*\"/) {
				push(@matchlines, "$count: $line");
			}
		}
		if (@matchlines > 0) {
			print "$File::Find::name : \n";
			$filecount++;
			foreach $line (@matchlines) {
				print $line;
			}
		}
		@matchlines = ();
        }

}
