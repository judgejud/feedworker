<?php
require_once('httpclient-2009-09-02/http.php');
require_once('magpierss-0.72/rss_fetch.inc');

$urlFeedTorrent = "http://pipes.yahoo.com/pipes/pipe.run?_id=bf9bca0ba64ac304f9a7e29aff11775a&_render=rss";
$urlDownRedir="http://127.0.0.1:5000/download/download_redirector.cgi";
//$urlDownRedir="http://judgejud.dyndns.org:5000/download/download_redirector.cgi";

/**
 * Description of PHPClass
 *
 * @author luca
 */
class PHPClass {
    /**Preleva il feed rss
     *
     * @param <type> $url url di riferimento
     * @return <type> array di array contenente link, title e data
     */
    public function getFeedRss(){
        global $urlFeedTorrent;
        $rss = fetch_rss ($urlFeedTorrent); //apre il feed
        $items = array(); //contiene tutti gli elementi
        foreach ($rss->items as $feed) {
            $item = array(); //definisce un singolo elemento
            //Variabilizzo tutto
            $item['link'] = $feed['link'];
            $item['title'] = $feed['title'];
            //Formatto la data con un intero UNIX TIMESTAMP
            //(del tipo 1129218794) per poterla maneggiare
            $item['date'] = strtotime ($feed['pubdate']);
            $items[] = $item; //aggiungo l'elemento all'array generale
        } // chiudo il ciclo FOREACH ITEM
        return $items;
    } //end getfeedrss

    /**Cerca la versione 720p nel titolo
     *
     * @param <type> $title titolo
     * @return <type> booleano
     */
    public function searchVersion720($title){
        $array = explode(" ", $title);
        $_720p = "[720p";
        $search = false;
        foreach($array as $item) { //ciclo gli elementi
            if (strtolower ($item) == $_720p){
                $search = true;
                break;
            }
        }
        return $search;
    }//end searchVersion720

    /**Si connette al download redirectory per prelevare l'id di sessione
     *
     * @param <type> $url url del down redir synology
     * @return <type> id di sessione synology
     */
    public function getIdSynology(){
        global $urlDownRedir;
        $user="admin";
        $password="adminsynology";

        set_time_limit(0);
        $http=new http_class;
        $http->timeout=0;
        $http->data_timeout=0;
        $http->debug=0;
        $http->html_debug=1;
        $error=$http->GetRequestArguments($urlDownRedir,$arguments);
        $arguments["RequestMethod"]="POST";
        $arguments["PostValues"]=array(
            "action"=>"login",
            "username"=>$user,
            "passwd"=>$password);

        $error=$http->Open($arguments);
        if($error==""){
            $error=$http->SendRequest($arguments);
            if($error==""){
                $error=$http->ReadReplyBody($body,1000);
                if($error!="" || strlen($body)==0)
                    break;
                $array = explode("\"", substr($body,13));
            }
        }
        $http->Close();
        return $array[0];
    } //end

    public function fireLinkSynology($id, $array){
        global $urlDownRedir;
        set_time_limit(0);
        foreach($array as $item) { //ciclo gli elementi
            $http=new http_class;
            $http->timeout=0;
            $http->data_timeout=0;
            $http->debug=0;
            $http->html_debug=1;
            $error=$http->GetRequestArguments($urlDownRedir,$arguments);
            $arguments["RequestMethod"]="POST";
            $arguments["PostValues"]=array(
            "id"=>$id,
            "action"=>"addurl",
            "url"=>$item);
            $error=$http->Open($arguments);
            if($error=="")
                $error=$http->SendRequest($arguments);
            $http->Close();
        }
    } //end

    public function getStatusSynology($id){
        global $urlDownRedir;
        set_time_limit(0);
        $http=new http_class;
        $http->timeout=0;
        $http->data_timeout=0;
        $http->debug=0;
        $http->html_debug=1;
        $error=$http->GetRequestArguments($urlDownRedir,$arguments);
        $arguments["RequestMethod"]="POST";
        $arguments["PostValues"]=array(
        "id"=>$id,
        "action"=>"getall");
        $error=$http->Open($arguments);
        if($error==""){
            $error=$http->SendRequest($arguments);
            if($error==""){
                $error=$http->ReadReplyBody($body,20000);
                if($error!="" || strlen($body)==0)
                    break;
            }
        }
        $http->Close();
        return $body;
    } //end

    
}//end class
?>