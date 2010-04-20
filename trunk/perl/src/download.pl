#USE
use strict;
use HTTP::Request::Common qw(POST);
use LWP::UserAgent;
use JSON;

#VARIABLES
my $synoUser = "admin";
my $synoUrl  = "http://127.0.0.1:5000/download/download_redirector.cgi";

#my $synoUrl = "http://192.168.1.100:5000/download/download_redirector.cgi";
#SUB
#restituisce l'id sessione
sub getSynologyId {
	my $synoPwd = "adminsynology";
	my $ua      = LWP::UserAgent->new;
	my $req     = POST $synoUrl,
	  [ action => 'login', username => $synoUser, passwd => $synoPwd ];
	getJSON( $ua->request($req)->content )->{id};    #return
}

#trasforma in json la response passata per shift
sub getJSON {
	new JSON->allow_nonref->utf8->relaxed->escape_slash->loose
	  ->allow_singlequote->allow_barekey->decode(shift);
}

#restituisce sotto json la risposta del server cgi
sub contentStatus {
	my $ua = LWP::UserAgent->new;
	my $req = POST $synoUrl, [ id => shift, action => 'getall' ];
	getJSON( $ua->request($req)->content );    #return
}

#restituisce l'array dei file completati
sub getFilesCompleted {
	my $json = contentStatus(shift);
	my @files;
	foreach my $item ( @{ $json->{items} } ) {
		if ( $item->{status} eq 5 ) {
			push( @files, $item->{filename} );
		}
	}
	@files;                                    #return
}

#effettua
sub fireLinkNas {
	my ( $_id, $_link ) = @_;
	my $req = POST $synoUrl, [ id => $_id, action => 'addurl', url => $_link ];
	my $ua  = LWP::UserAgent->new;
	my $res = $ua->request($req);
}

sub clearTask {
	my $ua  = LWP::UserAgent->new;
	my $req = POST $synoUrl,
	  [ id => shift, action => 'clear', username => $synoUser ];
	my $res = $ua->request($req);
}
