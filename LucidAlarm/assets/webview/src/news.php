<?php
$news = simplexml_load_file('http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&topic=n&output=rss');

$feeds = array();

$i = 0;

foreach ($news->channel->item as $item) 
{
    preg_match('@src="([^"]+)"@', $item->description, $match);
    $parts = explode('<font size="-1">', $item->description);

    $feeds[$i]['title'] = (string) $item->title;
    // $feeds[$i]['link'] = (string) $item->link;
    $feeds[$i]['image'] = $match[1];
    // $feeds[$i]['site_title'] = strip_tags($parts[1]);
    // $feeds[$i]['story'] = strip_tags($parts[2]);

    $i++;
}

//echo '<pre>';
//print_r($feeds);
//echo '</pre>';

$limit = 12;
	for($x=0;$x<$limit;$x++) {
		$title = str_replace(' & ', ' &amp; ', $feeds[$x]['title']);
		$image =  str_replace(' & ', ' &amp; ', $feeds[$x]['image']);
		//$link = $feed[$x]['link'];
		
		//$date = date('l F d, Y', strtotime($feed[$x]['date']));
		echo $title . '<udunno>' . $image;
		//echo '<small><em>Posted on '.$date.'</em></small></p>';
		echo '<wagwan>';
	}
?>