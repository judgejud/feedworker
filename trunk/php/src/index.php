<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta http-equiv="Cache-Control" content="no-store,no-cache,must-revalidate">
        <meta http-equiv="Pragma" content="no-cache">
        <title></title>
    </head>
    <body>
    <?php
        require_once('PHPClass.php');
        main();

        function main(){            
            //$rss = $instance->getFeedRss();
            //loadXML();
            countDown();
            //printTable($rss);
        }//END MAIN

        function countDown(){
            $instance = new PHPClass();
            $idSynology = $instance->getIdSynology();
            $test = $instance->getStatusSynology($idSynology);
            $test1 = explode("\"filename\"", $test);
            foreach ($test1 as $variable) {
                echo "<br>$variable";
            }
        }

        function printTable($rss){
            global $instance;
            //Comincio a scrivere l'output
            echo "<table border=1>\n";
            echo "<tr><td>Data</td><td>Descrizione</td><td>Link</td></tr>\n";
            //Incominciamo un ciclo per ogni item
            foreach($rss as $item) { //ciclo gli elementi
                //Variabilizzo tutto
                $link = $item['link'];
                $title = $item['title'];
                if ($instance->searchVersion720($title))
                    $title = "<b> $title </b>";
                //Formatto la stringa della data secondo i miei gusti
                $date = date("d/m/Y G:i", $item['date']);
                //Ora potrei cominciare a stampare il feed a video
                echo '<tr><td>'.$date.'</td><td>'.$title.'</td><td>'.$link.'</td></tr>',"\n";
            } // chiudo il ciclo FOREACH ITEM
            //Chiudo la tabella
            echo "</table>\n";
        }

        function loadXML(){
            $xml = simplexml_load_file('roles.xml');
            echo "<table border=1>\n";
            echo "<tr><td>Nome</td><td>Stagione</td><td>Versione</td><td>Path</td></tr>\n";
            foreach($xml->ROLE as $role)
                echo "<tr><td>$role->NAME</td><td>$role->SEASON
                    </td><td>$role->QUALITY</td><td>$role->PATH</td></tr>\n";
            echo "</table>\n";
        }
    ?>
</body></html>