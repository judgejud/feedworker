#USE
use strict;
use XML::FeedPP;
use DateTime::Format::ISO8601;
#REQUIRE
require "/volume1/web/download.pl";
#VARIABLES
my $fileData = "/volume1/web/data.txt";
my $fileLog = "/volume1/web/log.txt";#file log
my $lastFeed="";
#MAIN
_MAIN();
#SUB
sub _MAIN{
	if (-e $fileData) {
	    open FILE, "<", $fileData or die $!;
		$lastFeed = parseDate(<FILE>);
	    close FILE;
	    run();
	} else {	
		writeFileData(firstDate(getFeedRss()));
	}
}
sub run{	
	my $feed = getFeedRss();
	my $tempDate = firstDate($feed);
	if ($lastFeed < $tempDate){
		my $id = getSynologyId();
		my $i = 0;
		my $item = $feed->get_item($i++);
		writeLog($item->title());
		fireLinkNas($id, $item->link());
		while (1){	
			$item = $feed->get_item($i++);			
			if ($lastFeed < parseDate($item->pubDate())){
				writeLog($item->title());
		        fireLinkNas($id, $item->link()); 				        
			} else {
				last; #esce dal for
			}
	    }
    	writeFileData($tempDate);
    } else {
    	writeLog("Connessione al feed, trovati 0 nuovi item");
    }
}
sub getFeedRss{
	my $urlFeed = "http://pipes.yahoo.com/pipes/pipe.run?_id=bf9bca0ba64ac304f9a7e29aff11775a&_render=rss";
	XML::FeedPP->new( $urlFeed ); #return
}
sub firstDate{
	parseDate(shift->get_item(0)->pubDate()); #return
}
sub writeFileData{
	open FILE, ">", $fileData or die $!;
	print FILE shift;
    close FILE;
}
sub writeLog{
	open FILE, ">>", $fileLog or die $!;	
	print FILE getTime(), " ", shift, "\n";
    close FILE;
}
sub parseDate{
	DateTime::Format::ISO8601->parse_datetime( shift ); #return	
}
sub getTime{
	(my $second, my $minute, my $hour, my $dayOfMonth, my $month, my $yearOffset, my $dayOfWeek, my $dayOfYear, my $daylightSavings) = localtime();
	my $year = 1900 + $yearOffset;
	my $theTime = "$dayOfMonth/$month/$year $hour:$minute:$second"; #return
}