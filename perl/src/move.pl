#USE
use strict;
use XML::XPath;
use XML::XPath::XMLParser;

#REQUIRE
require "/volume1/web/download.pl";

#VARIABLES
my $dirFrom = "/volume1/download/";        #directory download
my $dirTo   = "/volume1/divx/";            #directory destinazione
my $fileXML = '/volume1/web/roles.xml';    #file regole
my $fileLog = "/volume1/web/log.txt";      #file log

#MAIN
my $id    = getSynologyId();               #recupero l'id sessione synology
my @files =
  getFilesCompleted($id);    #mi faccio restituire l'array dei files completati
if ( scalar(@files) > 0 ) {  # se array > 0 quindi con almeno 1file completato
	clearTask($id);          #pulisco la lista dei task eliminando i completati
	my $xpath = XML::XPath->new( filename => $fileXML );    #istanzio xpath
	foreach my $item (@files)
	{    #per ogni elemento dell'array files assegno e scorro
		my @text    = split( /\./, $item );   #splitto il nome del file
		my $version = searchVersion(@text);   #recupero la versione
		my $pos     = searchPosSeason(@text); #cerco la posizione della stagione
		my $season = substr $text[$pos], 1, 2;    #recupero la stagione
		if ( $season < 10 ) {
			$season = substr $season, 1, 1;
		}
		my $compose = $text[0];                   #compongo il nome serie
		for ( my $i = 1 ; $i < $pos ; $i++ ) {
			$compose = "$compose $text[$i]";
		}
		$compose = lc($compose);                  #lowercase
		                                          #creo la query
		my $query =
"//ROLE[NAME/text()='$compose' and SEASON/text()='$season' and QUALITY/text()='$version']/PATH/text()";
		my $nodeset = $xpath->find($query);       #cerco il nodo corrispondente
		my $count   = $nodeset->get_nodelist;     #conto i nodi trovati
		if ( $count > 0 ) {                       #nodo trovato
			my $path =
			  XML::XPath::XMLParser::as_string( $nodeset->get_nodelist )
			  ;                                   #restituisco il path
			my $newPath = "$dirTo$path/";         #compongo il nuovo path
			if ( -d $newPath ) {                  #se esiste la directory path
				my $oldFile =
				  "$dirFrom$item"
				  ;    #compongo il percorso assoluto del file da spostare
				my $newName = substr $item,
				  length($compose);    #tronca la prima parte del nome
				my $newFile =
				  "$newPath$newName";    #crea il nuovo percorso assoluto
				rename( $oldFile, $newFile );    #effettua lo spostamento
				writeLog("$newFile");            #scrive nel file di log
			}
			else {
				writeLog("non posso spostare $item in $newPath");
			}
		}
	}
}

#SUB
#cerca la versione
sub searchVersion {
	my @text   = @_;
	my $search = "normale";
	foreach my $item (@text) {
		if ( $item eq "720p" ) {
			$search = "720p";
			last;    #esce dal for
		}
	}
	$search;         #return;
}

#cerca la posizione della stringa corrispondente alla stagione
sub searchPosSeason {
	my $found = -1;
	my @text  = @_;
	for ( my $i = 0 ; $i < @text ; $i++ ) {
		if ( $text[$i] =~ /s(\d+)e(\d+)/i ) {
			$found = $i;
			last;    #esce dal for
		}
	}
	$found;          #return
}

#restituisce l'ora e data attuale
sub getTime {
	(
		my $second,
		my $minute,
		my $hour,
		my $dayOfMonth,
		my $month,
		my $yearOffset,
		my $dayOfWeek,
		my $dayOfYear,
		my $daylightSavings
	  )
	  = localtime();
	my $year    = 1900 + $yearOffset;
	my $theTime = "$dayOfMonth/$month/$year $hour:$minute:$second";    #return
}

#scrive nel file di log
sub writeLog {
	open FILE, ">>", $fileLog or die $!;
	print FILE getTime(), " ", shift, "\n";
	close FILE;
}
