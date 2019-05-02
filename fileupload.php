<?php

$image=$_POST["image"];
$name=$_POST["title"];
$target_dir="uploads";


if(!file_exists($target_dir)){
	mkdir($target_dir,0777,true);
}

$target_dir1=$target_dir."/".$name.".jpeg";
if(file_put_contents($target_dir1,base64_decode($image))){
	echo json_encode([
		"Message"=>"The file has been uploaded.",
		"Status"=>"OK"
	]);
}else{
	echo json_encode([
		"Message"=>"Sorry,Uploading Faiiled",
		"Status"=>"Error"
	]);
}

?>